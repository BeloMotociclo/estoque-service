package br.com.Belo.Motociclo.estoque_service.mapper;

import br.com.Belo.Motociclo.estoque_service.dto.PecaRequestDTO;
import br.com.Belo.Motociclo.estoque_service.dto.PecaResponseDTO;
import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import org.springframework.stereotype.Component;

@Component
public class PecaMapper {

    public Peca toEntity(PecaRequestDTO dto) {
        Peca peca = new Peca();
        peca.setCodigo(dto.codigo());
        peca.setQuantidade(dto.quantidade());
        peca.setCategoria(dto.categoria());
        peca.setMarca(dto.marca());
        peca.setPrecoVenda(dto.precoVenda());
        return peca;
    }

    public PecaResponseDTO toResponseDTO(Peca peca) {
        return new PecaResponseDTO(
                peca.getId(), peca.getCodigo(), peca.getQuantidade(),
                peca.getCategoria(), peca.getMarca(), peca.getPrecoVenda()
        );
    }
}