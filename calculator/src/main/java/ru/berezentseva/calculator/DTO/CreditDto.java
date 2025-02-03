package ru.berezentseva.calculator.DTO;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@ToString
public class CreditDto {
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private List<PaymentScheduleElementDto> paymentSchedule;


    public BigDecimal getAmount() {
        return this.amount;
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

    public BigDecimal getPsk() {
        return this.psk;
    }

    public Boolean getIsInsuranceEnabled() {
        return this.isInsuranceEnabled;
    }

    public Boolean getIsSalaryClient() {
        return this.isSalaryClient;
    }

    public List<PaymentScheduleElementDto> getPaymentSchedule() {
        return this.paymentSchedule;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public void setPsk(BigDecimal psk) {
        this.psk = psk;
    }

    public void setIsInsuranceEnabled(Boolean isInsuranceEnabled) {
        this.isInsuranceEnabled = isInsuranceEnabled;
    }

    public void setIsSalaryClient(Boolean isSalaryClient) {
        this.isSalaryClient = isSalaryClient;
    }

    public void setPaymentSchedule(List<PaymentScheduleElementDto> paymentSchedule) {
        this.paymentSchedule = paymentSchedule;
    }
}
