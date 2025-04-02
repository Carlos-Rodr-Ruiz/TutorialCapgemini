package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {

    List<Loan> findAll();

    ResponsePage<LoanDto> find(PageableRequest pageable, LoanSearchDto filters);

    /**
     * Nuevo método para listado sin paginación con filtros (como Game)
     */
    List<Loan> findFiltered(Long clientId, Long gameId, LocalDate date);

    void save(LoanDto dto);

    void delete(Long id);
}
