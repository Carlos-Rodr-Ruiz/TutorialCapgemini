package com.ccsw.tutorial.loan.model;

import java.time.LocalDate;

public class LoanSearchDto {
    private Long gameId;
    private Long clientId;
    private LocalDate date;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
