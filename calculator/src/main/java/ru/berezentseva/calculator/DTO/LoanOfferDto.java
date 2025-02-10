package ru.berezentseva.calculator.DTO;

import java.math.BigDecimal;
import java.util.UUID;


public class LoanOfferDto {
    private UUID statementId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    public LoanOfferDto(UUID statementId, BigDecimal requestedAmount, BigDecimal totalAmount, Integer term, BigDecimal monthlyPayment, BigDecimal rate, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        this.statementId = statementId;
        this.requestedAmount = requestedAmount;
        this.totalAmount = totalAmount;
        this.term = term;
        this.monthlyPayment = monthlyPayment;
        this.rate = rate;
        this.isInsuranceEnabled = isInsuranceEnabled;
        this.isSalaryClient = isSalaryClient;
    }

    public LoanOfferDto() {
    }

    public static LoanOfferDtoBuilder builder() {
        return new LoanOfferDtoBuilder();
    }


    public UUID getStatementId() {
        return this.statementId;
    }

    public BigDecimal getRequestedAmount() {
        return this.requestedAmount;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Integer getTerm() {
        return this.term;
    }

    public BigDecimal getMonthlyPayment() {
        return this.monthlyPayment;
    }

    public BigDecimal getRate() {
        return this.rate;
    }

    public Boolean getIsInsuranceEnabled() {
        return this.isInsuranceEnabled;
    }

    public Boolean getIsSalaryClient() {
        return this.isSalaryClient;
    }

    public void setStatementId(UUID statementId) {
        this.statementId = statementId;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setIsInsuranceEnabled(Boolean isInsuranceEnabled) {
        this.isInsuranceEnabled = isInsuranceEnabled;
    }

    public void setIsSalaryClient(Boolean isSalaryClient) {
        this.isSalaryClient = isSalaryClient;
    }

    public String toString() {
        return "LoanOfferDto(statementId=" + this.getStatementId() + ", requestedAmount=" + this.getRequestedAmount() + ", totalAmount=" + this.getTotalAmount() + ", term=" + this.getTerm() + ", monthlyPayment=" + this.getMonthlyPayment() + ", rate=" + this.getRate() + ", isInsuranceEnabled=" + this.getIsInsuranceEnabled() + ", isSalaryClient=" + this.getIsSalaryClient() + ")";
    }

    public static class LoanOfferDtoBuilder {
        private UUID statementId;
        private BigDecimal requestedAmount;
        private BigDecimal totalAmount;
        private Integer term;
        private BigDecimal monthlyPayment;
        private BigDecimal rate;
        private Boolean isInsuranceEnabled;
        private Boolean isSalaryClient;

        LoanOfferDtoBuilder() {
        }

        public LoanOfferDtoBuilder statementId(UUID statementId) {
            this.statementId = statementId;
            return this;
        }

        public LoanOfferDtoBuilder requestedAmount(BigDecimal requestedAmount) {
            this.requestedAmount = requestedAmount;
            return this;
        }

        public LoanOfferDtoBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public LoanOfferDtoBuilder term(Integer term) {
            this.term = term;
            return this;
        }

        public LoanOfferDtoBuilder monthlyPayment(BigDecimal monthlyPayment) {
            this.monthlyPayment = monthlyPayment;
            return this;
        }

        public LoanOfferDtoBuilder rate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }

        public LoanOfferDtoBuilder isInsuranceEnabled(Boolean isInsuranceEnabled) {
            this.isInsuranceEnabled = isInsuranceEnabled;
            return this;
        }

        public LoanOfferDtoBuilder isSalaryClient(Boolean isSalaryClient) {
            this.isSalaryClient = isSalaryClient;
            return this;
        }

        public LoanOfferDto build() {
            return new LoanOfferDto(this.statementId, this.requestedAmount, this.totalAmount, this.term, this.monthlyPayment, this.rate, this.isInsuranceEnabled, this.isSalaryClient);
        }

        public String toString() {
            return "LoanOfferDto.LoanOfferDtoBuilder(statementId=" + this.statementId + ", requestedAmount=" + this.requestedAmount + ", totalAmount=" + this.totalAmount + ", term=" + this.term + ", monthlyPayment=" + this.monthlyPayment + ", rate=" + this.rate + ", isInsuranceEnabled=" + this.isInsuranceEnabled + ", isSalaryClient=" + this.isSalaryClient + ")";
        }
    }
}
