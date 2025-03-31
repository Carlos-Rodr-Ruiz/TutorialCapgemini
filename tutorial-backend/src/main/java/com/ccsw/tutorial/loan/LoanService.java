package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

public interface LoanService {

    ResponsePage<LoanDto> find(PageableRequest pageable, LoanSearchDto filters);

    void save(LoanDto dto);

    void delete(Long id);
}
