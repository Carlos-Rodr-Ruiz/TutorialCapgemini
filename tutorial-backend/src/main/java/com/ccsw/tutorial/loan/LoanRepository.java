package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Loan> {

    @Override
    @EntityGraph(attributePaths = { "client", "game" })
    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);

    //Validación: evitar préstamos solapados por juego
    @Query("SELECT l FROM Loan l WHERE l.game.id = :gameId AND :startDate <= l.endDate AND :endDate >= l.startDate")
    List<Loan> findByGameIdAndDateOverlap(@Param("gameId") Long gameId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //Validación: evitar más de 2 préstamos al mismo cliente en el mismo periodo
    @Query("SELECT l FROM Loan l WHERE l.client.id = :clientId AND :startDate <= l.endDate AND :endDate >= l.startDate")
    List<Loan> findByClientIdAndDateOverlap(@Param("clientId") Long clientId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
