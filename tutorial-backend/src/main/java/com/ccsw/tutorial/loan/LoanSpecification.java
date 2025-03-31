package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class LoanSpecification {

    public static Specification<Loan> hasGame(Long gameId) {
        return (root, query, builder) -> {
            if (gameId == null)
                return null;
            return builder.equal(root.get("game").get("id"), gameId);
        };
    }

    public static Specification<Loan> hasClient(Long clientId) {
        return (root, query, builder) -> {
            if (clientId == null)
                return null;
            return builder.equal(root.get("client").get("id"), clientId);
        };
    }

    public static Specification<Loan> includesDate(LocalDate date) {
        return (root, query, builder) -> {
            if (date == null)
                return null;
            return builder.and(builder.lessThanOrEqualTo(root.get("startDate"), date), builder.greaterThanOrEqualTo(root.get("endDate"), date));
        };
    }
}
