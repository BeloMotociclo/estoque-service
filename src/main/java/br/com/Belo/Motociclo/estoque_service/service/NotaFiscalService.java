package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.HistoricoPrecoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ItemNotaFiscalDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalImportadaDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.Fornecedor;
import br.com.Belo.Motociclo.estoque_service.entity.HistoricoPreco;
import br.com.Belo.Motociclo.estoque_service.entity.NotaFiscal;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.exception.RegraNegocioException;
import br.com.Belo.Motociclo.estoque_service.repository.FornecedorRepository;
import br.com.Belo.Motociclo.estoque_service.repository.HistoricoPrecoRepository;
import br.com.Belo.Motociclo.estoque_service.repository.NotaFiscalRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final FornecedorRepository fornecedorRepository;
    private final PecaRepository pecaRepository;
    private final HistoricoPrecoRepository historicoPrecoRepository;
    private final LogAlteracaoService logService;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository,
                             FornecedorRepository fornecedorRepository,
                             PecaRepository pecaRepository,
                             HistoricoPrecoRepository historicoPrecoRepository,
                             LogAlteracaoService logService) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.pecaRepository = pecaRepository;
        this.historicoPrecoRepository = historicoPrecoRepository;
        this.logService = logService;
    }

    // Importação do XML da NF-e
    public NotaFiscalImportadaDTO parseXml(MultipartFile arquivo) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(arquivo.getInputStream());
            doc.getDocumentElement().normalize();

            String numero = doc.getElementsByTagName("nNF").item(0).getTextContent();
            String chaveAcesso = doc.getElementsByTagName("chNFe").item(0) != null
                    ? doc.getElementsByTagName("chNFe").item(0).getTextContent() : null;
            String cnpjFornecedor = doc.getElementsByTagName("CNPJ").item(0).getTextContent();
            BigDecimal valorTotal = new BigDecimal(doc.getElementsByTagName("vNF").item(0).getTextContent());
            LocalDate data = LocalDate.parse(
                    doc.getElementsByTagName("dhEmi").item(0).getTextContent().substring(0, 10)
            );

            List<ItemNotaFiscalDTO> itens = new ArrayList<>();
            NodeList detList = doc.getElementsByTagName("det");
            for (int i = 0; i < detList.getLength(); i++) {
                Element det = (Element) detList.item(i);
                String codigo = det.getElementsByTagName("cProd").item(0).getTextContent();
                BigDecimal preco = new BigDecimal(det.getElementsByTagName("vUnCom").item(0).getTextContent());
                Integer quantidade = new BigDecimal(
                        det.getElementsByTagName("qCom").item(0).getTextContent()
                ).intValue();
                itens.add(new ItemNotaFiscalDTO(codigo, preco, quantidade));
            }

            return new NotaFiscalImportadaDTO(numero, chaveAcesso, cnpjFornecedor, valorTotal, data, itens);

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar XML da NF-e: " + e.getMessage());
        }
    }

    @Transactional
    public NotaFiscalResponseDTO importar(MultipartFile arquivo) {
        NotaFiscalImportadaDTO dadosXml = parseXml(arquivo);

        // Valida se nota já existe
        Fornecedor fornecedor = fornecedorRepository.findByCnpjAndAtivoTrue(dadosXml.cnpjFornecedor())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Fornecedor com CNPJ " + dadosXml.cnpjFornecedor() + " não cadastrado"));

        if (dadosXml.chaveAcesso() != null && notaFiscalRepository.existsByChaveAcesso(dadosXml.chaveAcesso())) {
            throw new RegraNegocioException("Nota fiscal já importada anteriormente");
        }

        if (notaFiscalRepository.existsByFornecedorIdAndNumero(fornecedor.getId(), dadosXml.numero())) {
            throw new RegraNegocioException("Nota fiscal já importada anteriormente");
        }

        // Salva a nota fiscal
        NotaFiscal nota = new NotaFiscal();
        nota.setFornecedor(fornecedor);
        nota.setNumero(dadosXml.numero());
        nota.setChaveAcesso(dadosXml.chaveAcesso());
        nota.setValorTotal(dadosXml.valorTotal());
        nota.setData(dadosXml.data());
        nota = notaFiscalRepository.save(nota);

        // Processa os itens — atualiza estoque e registra histórico de preço
        List<HistoricoPrecoResponseDTO> itensProcessados = new ArrayList<>();
        List<String> pecasNaoCadastradas = new ArrayList<>();

        for (ItemNotaFiscalDTO item : dadosXml.itens()) {
            Optional<Peca> pecaOpt = pecaRepository.findByCodigoAndAtivoTrue(item.codigoPeca());

            if (pecaOpt.isEmpty()) {
                pecasNaoCadastradas.add(item.codigoPeca());
                continue;
            }

            Peca peca = pecaOpt.get();

            // Atualiza quantidade em estoque
            peca.setQuantidade(peca.getQuantidade() + item.quantidade());
            pecaRepository.save(peca);

            // Registra histórico de preço
            HistoricoPreco historico = new HistoricoPreco();
            historico.setPeca(peca);
            historico.setFornecedor(fornecedor);
            historico.setNotaFiscal(nota);
            historico.setPrecoCompra(item.precoUnitario());
            historico.setData(dadosXml.data());
            HistoricoPreco salvo = historicoPrecoRepository.save(historico);

            itensProcessados.add(new HistoricoPrecoResponseDTO(
                    salvo.getId(), peca.getId(), peca.getCodigo(), salvo.getPrecoCompra(), salvo.getData()
            ));
        }

        // Avisa quais peças do XML não estavam cadastradas
        if (!pecasNaoCadastradas.isEmpty()) {
            System.out.println("Peças não cadastradas no sistema: " + pecasNaoCadastradas);
            // futuramente pode virar um campo na response
        }

        NotaFiscal notaFinal = nota;
        logService.registrar("NotaFiscal", notaFinal.getId().toString(), AcaoLog.CRIACAO,
                "Nota fiscal importada: " + notaFinal.getNumero());
        return new NotaFiscalResponseDTO(
                notaFinal.getId(), fornecedor.getId(), fornecedor.getNome(),
                notaFinal.getNumero(), notaFinal.getChaveAcesso(),
                notaFinal.getValorTotal(), notaFinal.getData(), itensProcessados
        );
    }

    public Page<NotaFiscalResponseDTO> listar(Pageable pageable) {
        return notaFiscalRepository.findAllByAtivoTrue(pageable).map(nota ->
                new NotaFiscalResponseDTO(
                        nota.getId(), nota.getFornecedor().getId(), nota.getFornecedor().getNome(),
                        nota.getNumero(), nota.getChaveAcesso(), nota.getValorTotal(), nota.getData(),
                        historicoPrecoRepository.findByNotaFiscalIdOrderByDataDesc(nota.getId())
                                .stream()
                                .map(h -> new HistoricoPrecoResponseDTO(
                                        h.getId(), h.getPeca().getId(), h.getPeca().getCodigo(),
                                        h.getPrecoCompra(), h.getData()))
                                .toList()
                )
        );
    }

    public List<HistoricoPrecoResponseDTO> historicoPrecosPorPeca(UUID pecaId) {
        return historicoPrecoRepository.findByPecaIdOrderByDataDesc(pecaId)
                .stream()
                .map(h -> new HistoricoPrecoResponseDTO(
                        h.getId(), h.getPeca().getId(), h.getPeca().getCodigo(),
                        h.getPrecoCompra(), h.getData()))
                .toList();
    }
}