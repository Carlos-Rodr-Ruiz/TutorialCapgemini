package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    @Query("SELECT l FROM Loan l WHERE l.game.id = :gameId AND " + "(l.startDate <= :endDate AND l.endDate >= :startDate)")
    List<Loan> findByGameIdAndDateOverlap(@Param("gameId") Long gameId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM Loan l WHERE l.client.id = :clientId AND " + "(l.startDate <= :endDate AND l.endDate >= :startDate)")
    List<Loan> findByClientIdAndDateOverlap(@Param("clientId") Long clientId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
