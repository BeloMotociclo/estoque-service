package br.com.Belo.Motociclo.estoque_service.service;

import br.com.Belo.Motociclo.estoque_service.dto.CadastrarPecaPendenciaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.HistoricoPrecoResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ItemNotaFiscalDTO;
import br.com.Belo.Motociclo.estoque_service.dto.ItemNotaPendenteResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalImportadaDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalManualRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.NotaFiscalResponseDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaCriadaDTO;
import br.com.Belo.Motociclo.estoque_service.entity.AcaoLog;
import br.com.Belo.Motociclo.estoque_service.entity.CodigoPeca;
import br.com.Belo.Motociclo.estoque_service.entity.Fornecedor;
import br.com.Belo.Motociclo.estoque_service.entity.HistoricoPreco;
import br.com.Belo.Motociclo.estoque_service.entity.ItemNotaPendente;
import br.com.Belo.Motociclo.estoque_service.entity.NotaFiscal;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import br.com.Belo.Motociclo.estoque_service.entity.PecaFornecedor;
import br.com.Belo.Motociclo.estoque_service.entity.ResolucaoPendente;
import br.com.Belo.Motociclo.estoque_service.entity.StatusItemPendente;
import br.com.Belo.Motociclo.estoque_service.entity.TipoCodigoPeca;
import br.com.Belo.Motociclo.estoque_service.exception.RecursoNaoEncontradoException;
import br.com.Belo.Motociclo.estoque_service.exception.RegraNegocioException;
import br.com.Belo.Motociclo.estoque_service.exception.SefazIndisponivelException;
import br.com.Belo.Motociclo.estoque_service.repository.CodigoPecaRepository;
import br.com.Belo.Motociclo.estoque_service.repository.FornecedorRepository;
import br.com.Belo.Motociclo.estoque_service.repository.HistoricoPrecoRepository;
import br.com.Belo.Motociclo.estoque_service.repository.ItemNotaPendenteRepository;
import br.com.Belo.Motociclo.estoque_service.repository.NotaFiscalRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaFornecedorRepository;
import br.com.Belo.Motociclo.estoque_service.repository.PecaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.UUID;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final FornecedorRepository fornecedorRepository;
    private final PecaRepository pecaRepository;
    private final HistoricoPrecoRepository historicoPrecoRepository;
    private final CodigoPecaRepository codigoPecaRepository;
    private final ItemNotaPendenteRepository itemNotaPendenteRepository;
    private final PecaFornecedorRepository pecaFornecedorRepository;
    private final LogAlteracaoService logService;

    public NotaFiscalService(NotaFiscalRepository notaFiscalRepository,
                             FornecedorRepository fornecedorRepository,
                             PecaRepository pecaRepository,
                             HistoricoPrecoRepository historicoPrecoRepository,
                             CodigoPecaRepository codigoPecaRepository,
                             ItemNotaPendenteRepository itemNotaPendenteRepository,
                             PecaFornecedorRepository pecaFornecedorRepository,
                             LogAlteracaoService logService) {
        this.notaFiscalRepository = notaFiscalRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.pecaRepository = pecaRepository;
        this.historicoPrecoRepository = historicoPrecoRepository;
        this.codigoPecaRepository = codigoPecaRepository;
        this.itemNotaPendenteRepository = itemNotaPendenteRepository;
        this.pecaFornecedorRepository = pecaFornecedorRepository;
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
                String descricao = det.getElementsByTagName("xProd").getLength() > 0
                        ? det.getElementsByTagName("xProd").item(0).getTextContent() : null;
                itens.add(new ItemNotaFiscalDTO(codigo, preco, quantidade, descricao));
            }

            return new NotaFiscalImportadaDTO(numero, chaveAcesso, cnpjFornecedor, valorTotal, data, itens);

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar XML da NF-e: " + e.getMessage());
        }
    }

    @Transactional
    public NotaFiscalResponseDTO importar(MultipartFile arquivo, boolean criarPecasAutomaticamente) {
        NotaFiscalImportadaDTO dadosXml = parseXml(arquivo);

        String cnpjNumeros = dadosXml.cnpjFornecedor() == null
                ? "" : dadosXml.cnpjFornecedor().replaceAll("\\D", "");
        Fornecedor fornecedor = fornecedorRepository.buscarAtivoPorCnpjNormalizado(cnpjNumeros)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Fornecedor com CNPJ " + dadosXml.cnpjFornecedor() + " não cadastrado"));

        if (dadosXml.chaveAcesso() != null && notaFiscalRepository.existsByChaveAcesso(dadosXml.chaveAcesso())) {
            throw new RegraNegocioException("Nota fiscal já importada anteriormente");
        }

        if (notaFiscalRepository.existsByFornecedorIdAndNumero(fornecedor.getId(), dadosXml.numero())) {
            throw new RegraNegocioException("Nota fiscal já importada anteriormente");
        }

        NotaFiscal nota = new NotaFiscal();
        nota.setFornecedor(fornecedor);
        nota.setNumero(dadosXml.numero());
        nota.setChaveAcesso(dadosXml.chaveAcesso());
        nota.setValorTotal(dadosXml.valorTotal());
        nota.setData(dadosXml.data());
        nota = notaFiscalRepository.save(nota);

        Processamento processamento = processarItens(nota, fornecedor, dadosXml.data(),
                dadosXml.itens(), criarPecasAutomaticamente);

        logService.registrar("NotaFiscal", nota.getId().toString(), AcaoLog.CRIACAO,
                "Nota fiscal importada: " + nota.getNumero());
        return toResponseDTO(nota, fornecedor, processamento);
    }

    @Transactional
    public NotaFiscalResponseDTO cadastrarManualmente(NotaFiscalManualRequestDTO dto, boolean criarPecasAutomaticamente) {
        Fornecedor fornecedor = fornecedorRepository.findByIdAndAtivoTrue(dto.fornecedorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Fornecedor com id " + dto.fornecedorId() + " não encontrado"));

        if (dto.chaveAcesso() != null && !dto.chaveAcesso().isBlank()
                && notaFiscalRepository.existsByChaveAcesso(dto.chaveAcesso())) {
            throw new RegraNegocioException("Nota fiscal já importada anteriormente");
        }

        if (notaFiscalRepository.existsByFornecedorIdAndNumero(fornecedor.getId(), dto.numero())) {
            throw new RegraNegocioException("Nota fiscal já importada anteriormente");
        }

        NotaFiscal nota = new NotaFiscal();
        nota.setFornecedor(fornecedor);
        nota.setNumero(dto.numero());
        nota.setChaveAcesso(dto.chaveAcesso() != null && dto.chaveAcesso().isBlank() ? null : dto.chaveAcesso());
        nota.setValorTotal(dto.valorTotal());
        nota.setData(dto.data());
        nota = notaFiscalRepository.save(nota);

        Processamento processamento = processarItens(nota, fornecedor, dto.data(),
                dto.itens(), criarPecasAutomaticamente);

        logService.registrar("NotaFiscal", nota.getId().toString(), AcaoLog.CRIACAO,
                "Nota fiscal cadastrada: " + nota.getNumero());
        return toResponseDTO(nota, fornecedor, processamento);
    }

    private record Processamento(List<HistoricoPrecoResponseDTO> processados,
                                 List<ItemNotaPendente> pendentes,
                                 List<Peca> criadas) {}

    private Processamento processarItens(NotaFiscal nota, Fornecedor fornecedor,
                                         LocalDate data, List<ItemNotaFiscalDTO> itens,
                                         boolean criarPecasAutomaticamente) {
        List<HistoricoPrecoResponseDTO> processados = new ArrayList<>();
        List<ItemNotaPendente> pendentes = new ArrayList<>();
        List<Peca> criadas = new ArrayList<>();

        for (ItemNotaFiscalDTO item : itens) {
            Peca peca = resolverPeca(fornecedor, item);

            if (peca == null && criarPecasAutomaticamente && !pecaRepository.existsByCodigo(item.codigoPeca())) {
                peca = criarPecaAutomatica(fornecedor, item);
                criadas.add(peca);
            }

            if (peca == null) {
                pendentes.add(criarPendencia(nota, item));
                continue;
            }

            processados.add(processarItem(nota, fornecedor, data, peca, item));
        }

        return new Processamento(processados, pendentes, criadas);
    }

    private Peca resolverPeca(Fornecedor fornecedor, ItemNotaFiscalDTO item) {
        var mapeado = codigoPecaRepository.findFirstByTipoAndFornecedorIdAndCodigo(
                TipoCodigoPeca.FORNECEDOR, fornecedor.getId(), item.codigoPeca());
        if (mapeado.isPresent() && Boolean.TRUE.equals(mapeado.get().getPeca().getAtivo())) {
            return mapeado.get().getPeca();
        }
        return pecaRepository.findByCodigoAndAtivoTrue(item.codigoPeca()).orElse(null);
    }

    private Peca criarPecaAutomatica(Fornecedor fornecedor, ItemNotaFiscalDTO item) {
        Peca peca = new Peca();
        peca.setCodigo(item.codigoPeca());
        peca.setNome(item.descricao() != null && !item.descricao().isBlank()
                ? item.descricao().trim() : item.codigoPeca());
        peca.setCategoria("A CLASSIFICAR");
        peca.setPrecoVenda(item.precoUnitario());
        peca.setQuantidade(0);
        peca.setAtivo(true);
        peca = pecaRepository.save(peca);
        registrarCodigoFornecedor(peca, fornecedor, item.codigoPeca(), item.descricao());
        logService.registrar("Peca", peca.getId().toString(), AcaoLog.CRIACAO,
                "Peça criada automaticamente pela nota: " + peca.getCodigo());
        return peca;
    }

    private HistoricoPrecoResponseDTO processarItem(NotaFiscal nota, Fornecedor fornecedor,
                                                    LocalDate data, Peca peca, ItemNotaFiscalDTO item) {
        peca.setQuantidade(peca.getQuantidade() + item.quantidade());
        pecaRepository.save(peca);

        HistoricoPreco historico = new HistoricoPreco();
        historico.setPeca(peca);
        historico.setFornecedor(fornecedor);
        historico.setNotaFiscal(nota);
        historico.setPrecoCompra(item.precoUnitario());
        historico.setData(data);
        historico.setQuantidade(item.quantidade());
        HistoricoPreco salvo = historicoPrecoRepository.save(historico);

        return toHistoricoDTO(salvo);
    }

    private void registrarCodigoFornecedor(Peca peca, Fornecedor fornecedor, String codigo, String descricao) {
        if (!codigoPecaRepository.existsByTipoAndFornecedorIdAndCodigo(
                TipoCodigoPeca.FORNECEDOR, fornecedor.getId(), codigo)) {
            CodigoPeca codigoPeca = new CodigoPeca();
            codigoPeca.setPeca(peca);
            codigoPeca.setTipo(TipoCodigoPeca.FORNECEDOR);
            codigoPeca.setFornecedor(fornecedor);
            codigoPeca.setCodigo(codigo);
            codigoPeca.setDescricao(descricao);
            codigoPecaRepository.save(codigoPeca);
        }
        if (!pecaFornecedorRepository.existsByPecaIdAndFornecedorId(peca.getId(), fornecedor.getId())) {
            PecaFornecedor vinculo = new PecaFornecedor();
            vinculo.setPeca(peca);
            vinculo.setFornecedor(fornecedor);
            pecaFornecedorRepository.save(vinculo);
        }
    }

    private ItemNotaPendente criarPendencia(NotaFiscal nota, ItemNotaFiscalDTO item) {
        ItemNotaPendente pendente = new ItemNotaPendente();
        pendente.setNotaFiscal(nota);
        pendente.setCodigo(item.codigoPeca());
        pendente.setDescricao(item.descricao());
        pendente.setQuantidade(item.quantidade());
        pendente.setPrecoUnitario(item.precoUnitario());
        pendente.setStatus(StatusItemPendente.PENDENTE);
        return itemNotaPendenteRepository.save(pendente);
    }

    @Transactional(readOnly = true)
    public List<ItemNotaPendenteResponseDTO> listarItensPendentes(UUID notaId) {
        return itemNotaPendenteRepository.findByNotaFiscalIdOrderByIdAsc(notaId)
                .stream().map(this::toPendenteDTO).toList();
    }

    @Transactional
    public ItemNotaPendenteResponseDTO vincularPendencia(Long pendenteId, UUID pecaId) {
        ItemNotaPendente pendente = buscarPendenteAberto(pendenteId);
        Peca peca = pecaRepository.findById(pecaId)
                .filter(p -> Boolean.TRUE.equals(p.getAtivo()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Peça não encontrada ou inativa"));
        validarCodigoDisponivel(pendente, peca);

        Fornecedor fornecedor = pendente.getNotaFiscal().getFornecedor();
        processarItem(pendente.getNotaFiscal(), fornecedor, pendente.getNotaFiscal().getData(), peca,
                new ItemNotaFiscalDTO(pendente.getCodigo(), pendente.getPrecoUnitario(),
                        pendente.getQuantidade(), pendente.getDescricao()));
        registrarCodigoFornecedor(peca, fornecedor, pendente.getCodigo(), pendente.getDescricao());
        resolver(pendente, peca, ResolucaoPendente.PECA_VINCULADA);

        logService.registrar("NotaFiscal", pendente.getNotaFiscal().getId().toString(),
                AcaoLog.EDICAO, "Item da nota vinculado à peça: " + peca.getCodigo());
        return toPendenteDTO(pendente);
    }

    @Transactional
    public ItemNotaPendenteResponseDTO cadastrarPecaParaPendencia(Long pendenteId,
                                                                  CadastrarPecaPendenciaRequestDTO dto) {
        ItemNotaPendente pendente = buscarPendenteAberto(pendenteId);
        Fornecedor fornecedor = pendente.getNotaFiscal().getFornecedor();

        String codigo = dto.codigo() != null && !dto.codigo().isBlank()
                ? dto.codigo().trim() : pendente.getCodigo();

        if (pecaRepository.existsByCodigo(codigo)) {
            throw new RegraNegocioException("Já existe peça com o código " + codigo
                    + " — use 'vincular' para ligar a uma peça existente");
        }
        if (codigoPecaRepository.existsByTipoAndFornecedorIdAndCodigo(
                TipoCodigoPeca.FORNECEDOR, fornecedor.getId(), codigo)) {
            throw new RegraNegocioException("O código " + codigo
                    + " já está vinculado a uma peça deste fornecedor — use 'vincular'");
        }

        Peca peca = new Peca();
        peca.setCodigo(codigo);
        peca.setNome(dto.nome().trim());
        peca.setCategoria(dto.categoria() == null || dto.categoria().isBlank()
                ? "A CLASSIFICAR" : dto.categoria().trim().toUpperCase());
        peca.setMarca(dto.marca());
        peca.setPrecoVenda(dto.precoVenda());
        peca.setQuantidade(0);
        peca.setAtivo(true);
        peca = pecaRepository.save(peca);

        processarItem(pendente.getNotaFiscal(), fornecedor, pendente.getNotaFiscal().getData(), peca,
                new ItemNotaFiscalDTO(codigo, pendente.getPrecoUnitario(),
                        pendente.getQuantidade(), pendente.getDescricao()));
        registrarCodigoFornecedor(peca, fornecedor, codigo, pendente.getDescricao());
        resolver(pendente, peca, ResolucaoPendente.PECA_CRIADA);

        logService.registrar("Peca", peca.getId().toString(), AcaoLog.CRIACAO,
                "Peça criada pela pendência da nota: " + peca.getCodigo());
        return toPendenteDTO(pendente);
    }

    @Transactional
    public ItemNotaPendenteResponseDTO descartarPendencia(Long pendenteId) {
        ItemNotaPendente pendente = buscarPendenteAberto(pendenteId);
        resolver(pendente, null, ResolucaoPendente.DESCARTADA);
        return toPendenteDTO(pendente);
    }

    private ItemNotaPendente buscarPendenteAberto(Long id) {
        ItemNotaPendente pendente = itemNotaPendenteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item pendente não encontrado"));
        if (pendente.getStatus() != StatusItemPendente.PENDENTE) {
            throw new RegraNegocioException("Item já foi "
                    + (pendente.getStatus() == StatusItemPendente.RESOLVIDA ? "resolvido" : "descartado"));
        }
        return pendente;
    }

    private void validarCodigoDisponivel(ItemNotaPendente pendente, Peca peca) {
        codigoPecaRepository.findFirstByTipoAndFornecedorIdAndCodigo(
                        TipoCodigoPeca.FORNECEDOR, pendente.getNotaFiscal().getFornecedor().getId(),
                        pendente.getCodigo())
                .filter(cp -> !cp.getPeca().getId().equals(peca.getId()))
                .ifPresent(cp -> {
                    throw new RegraNegocioException("O código " + pendente.getCodigo()
                            + " já está vinculado à peça " + cp.getPeca().getCodigo());
                });
    }

    private void resolver(ItemNotaPendente pendente, Peca peca, ResolucaoPendente resolucao) {
        pendente.setStatus(resolucao == ResolucaoPendente.DESCARTADA
                ? StatusItemPendente.DESCARTADA : StatusItemPendente.RESOLVIDA);
        pendente.setResolucao(resolucao);
        pendente.setPeca(peca);
        itemNotaPendenteRepository.save(pendente);
    }

    private ItemNotaPendenteResponseDTO toPendenteDTO(ItemNotaPendente p) {
        return new ItemNotaPendenteResponseDTO(
                p.getId(), p.getCodigo(), p.getDescricao(), p.getQuantidade(), p.getPrecoUnitario(),
                p.getStatus(), p.getResolucao(),
                p.getPeca() != null ? p.getPeca().getId() : null,
                p.getPeca() != null ? p.getPeca().getCodigo() : null,
                p.getPeca() != null ? p.getPeca().getNome() : null);
    }

    private HistoricoPrecoResponseDTO toHistoricoDTO(HistoricoPreco h) {
        return new HistoricoPrecoResponseDTO(
                h.getId(), h.getPeca().getId(), h.getPeca().getCodigo(), h.getPeca().getNome(),
                h.getPrecoCompra(), h.getData(), h.getQuantidade());
    }

    private NotaFiscalResponseDTO toResponseDTO(NotaFiscal nota, Fornecedor fornecedor,
                                                Processamento processamento) {
        return new NotaFiscalResponseDTO(
                nota.getId(), fornecedor.getId(), fornecedor.getNome(),
                nota.getNumero(), nota.getChaveAcesso(),
                nota.getValorTotal(), nota.getData(), processamento.processados(),
                processamento.pendentes().stream().map(this::toPendenteDTO).toList(),
                processamento.criadas().stream()
                        .map(p -> new PecaCriadaDTO(p.getId(), p.getCodigo(), p.getNome()))
                        .toList()
        );
    }

    // Consulta na SEFAZ pela chave de acesso (44 dígitos).
    // A integração real com o WS demanda certificado digital A1, configurado via
    // variáveis de ambiente NFE_CERT_FILE/NFE_CERT_SENHA. Sem certificado, retorna 503.
    public void consultarSefaz(String chaveAcesso) {
        if (certificadoSefazConfigurado()) {
            throw new SefazIndisponivelException("Consulta SEFAZ indisponível: integração ainda não ativada");
        }
        throw new SefazIndisponivelException(
                "Consulta SEFAZ indisponível: certificado digital não configurado");
    }

    private boolean certificadoSefazConfigurado() {
        String arquivo = System.getenv("NFE_CERT_FILE");
        String senha = System.getenv("NFE_CERT_SENHA");
        return arquivo != null && !arquivo.isBlank() && senha != null && !senha.isBlank();
    }

    @Transactional(readOnly = true)
    public Page<NotaFiscalResponseDTO> listar(UUID fornecedorId, Pageable pageable) {
        Page<NotaFiscal> notas = fornecedorId != null
                ? notaFiscalRepository.findAllByAtivoTrueAndFornecedorId(fornecedorId, pageable)
                : notaFiscalRepository.findAllByAtivoTrue(pageable);
        return notas.map(nota ->
                new NotaFiscalResponseDTO(
                        nota.getId(), nota.getFornecedor().getId(), nota.getFornecedor().getNome(),
                        nota.getNumero(), nota.getChaveAcesso(), nota.getValorTotal(), nota.getData(),
                        historicoPrecoRepository.findByNotaFiscalIdOrderByDataDesc(nota.getId())
                                .stream()
                                .map(this::toHistoricoDTO)
                                .toList(),
                        itemNotaPendenteRepository.findByNotaFiscalIdAndStatusOrderByIdAsc(
                                        nota.getId(), StatusItemPendente.PENDENTE)
                                .stream()
                                .map(this::toPendenteDTO)
                                .toList(),
                        List.of()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<HistoricoPrecoResponseDTO> historicoPrecosPorPeca(UUID pecaId) {
        return historicoPrecoRepository.findByPecaIdOrderByDataDesc(pecaId)
                .stream()
                .map(this::toHistoricoDTO)
                .toList();
    }
}
