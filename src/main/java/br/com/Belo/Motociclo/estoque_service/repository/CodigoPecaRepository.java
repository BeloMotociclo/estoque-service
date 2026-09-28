package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.CodigoPeca;
import br.com.Belo.Motociclo.estoque_service.entity.TipoCodigoPeca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CodigoPecaRepository extends JpaRepository<CodigoPeca, Long> {

    Optional<CodigoPeca> findFirstByTipoAndFornecedorIdAndCodigo(
            TipoCodigoPeca tipo, UUID fornecedorId, String codigo);

    List<CodigoPeca> findByPecaIdOrderByTipoAscCodigoAsc(UUID pecaId);

    boolean existsByTipoAndFornecedorIdAndCodigo(TipoCodigoPeca tipo, UUID fornecedorId, String codigo);

    boolean existsByTipoAndCodigo(TipoCodigoPeca tipo, String codigo);

    Optional<CodigoPeca> findByIdAndPecaId(Long id, UUID pecaId);
}
