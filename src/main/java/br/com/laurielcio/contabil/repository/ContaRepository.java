package br.com.laurielcio.contabil.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.laurielcio.contabil.entity.ContaEntity;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ContaRepository extends JpaRepository<ContaEntity, Long> {

    Page<ContaEntity> findByDataVencimentoAndDescricaoContainingIgnoreCase(LocalDate dataVencimento, String descricao, Pageable pageable);

    Page<ContaEntity> findByDataVencimento(LocalDate dataVencimento, Pageable pageable);

    Page<ContaEntity> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);

    @Query("SELECT SUM(c.valor) FROM ContaEntity c WHERE c.dataVencimento BETWEEN :dataInicial AND :dataFinal")
    BigDecimal findValorTotalPorPeriodo(LocalDate dataInicial, LocalDate dataFinal);
}

