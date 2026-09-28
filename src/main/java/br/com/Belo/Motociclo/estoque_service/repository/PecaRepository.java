package br.com.Belo.Motociclo.estoque_service.repository;

import br.com.Belo.Motociclo.estoque_service.entity.Peca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PecaRepository extends JpaRepository<Peca, UUID> {
    Optional<Peca> findByCodigoAndAtivoTrue(String codigo);
    Page<Peca> findAllByAtivoTrue(Pageable pageable);
    long countByAtivoTrue();
    boolean existsByCodigo(String codigo);

    @Query("SELECT DISTINCT UPPER(p.categoria) FROM Peca p WHERE p.categoria IS NOT NULL ORDER BY 1")
    List<String> findAllCategorias();

    @Query("SELECT DISTINCT p.marca FROM Peca p WHERE p.marca IS NOT NULL ORDER BY 1")
    List<String> findAllMarcas();

    @Query(value = "SELECT DISTINCT p.* FROM peca p "
            + "LEFT JOIN nome_alternativo na ON na.peca_id = p.id "
            + "LEFT JOIN codigo_peca cp ON cp.peca_id = p.id "
            + "WHERE (:incluirInativas = true OR p.ativo = true) "
            + "AND (:q IS NULL "
            + "OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(na.nome) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(cp.codigo) LIKE LOWER(CONCAT('%', :q, '%'))) "
            + "AND (:semCategorias = true OR UPPER(p.categoria) IN (:categorias)) "
            + "AND (:semMarcas = true OR p.marca IN (:marcas)) "
            + "AND (:modeloId IS NULL OR EXISTS "
            + "(SELECT 1 FROM serventia s WHERE s.peca_id = p.id AND s.modelo_id = :modeloId))",
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM peca p "
            + "LEFT JOIN nome_alternativo na ON na.peca_id = p.id "
            + "LEFT JOIN codigo_peca cp ON cp.peca_id = p.id "
            + "WHERE (:incluirInativas = true OR p.ativo = true) "
            + "AND (:q IS NULL "
            + "OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(na.nome) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(cp.codigo) LIKE LOWER(CONCAT('%', :q, '%'))) "
            + "AND (:semCategorias = true OR UPPER(p.categoria) IN (:categorias)) "
            + "AND (:semMarcas = true OR p.marca IN (:marcas)) "
            + "AND (:modeloId IS NULL OR EXISTS "
            + "(SELECT 1 FROM serventia s WHERE s.peca_id = p.id AND s.modelo_id = :modeloId))",
            nativeQuery = true)
    Page<Peca> buscar(@Param("q") String q,
                      @Param("incluirInativas") boolean incluirInativas,
                      @Param("semCategorias") boolean semCategorias,
                      @Param("categorias") List<String> categorias,
                      @Param("semMarcas") boolean semMarcas,
                      @Param("marcas") List<String> marcas,
                      @Param("modeloId") Long modeloId,
                      Pageable pageable);
}
