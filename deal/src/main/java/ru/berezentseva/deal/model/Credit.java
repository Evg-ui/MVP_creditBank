package ru.berezentseva.deal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.berezentseva.deal.DTO.Enums.CreditStatus;
import ru.berezentseva.deal.DTO.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID creditUuid;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "term", nullable = false)
    private int term;

    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    @Column(name = "psk", nullable = false)
    private BigDecimal psk;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_schedule", columnDefinition = "jsonb")
    private List<PaymentScheduleElementDto> payment_schedule;

    @Column(name = "insurance_enabled", nullable = false)
    private Boolean insuranceEnabled;

    @Column(name = "salary_client", nullable = false)
    private Boolean salaryClient;

    // @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "credit_status")
    private CreditStatus creditStatus;

    public UUID getCreditUuid() {
        return this.creditUuid;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public int getTerm() {
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

    public List<PaymentScheduleElementDto> getPayment_schedule() {
        return this.payment_schedule;
    }

    public Boolean getInsuranceEnabled() {
        return this.insuranceEnabled;
    }

    public Boolean getSalaryClient() {
        return this.salaryClient;
    }

    public CreditStatus getCreditStatus() {
        return this.creditStatus;
    }

    public void setCreditUuid(UUID creditUuid) {
        this.creditUuid = creditUuid;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTerm(int term) {
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

    public void setPayment_schedule(List<PaymentScheduleElementDto> payment_schedule) {
        this.payment_schedule = payment_schedule;
    }

    public void setInsuranceEnabled(Boolean insuranceEnabled) {
        this.insuranceEnabled = insuranceEnabled;
    }

    public void setSalaryClient(Boolean salaryClient) {
        this.salaryClient = salaryClient;
    }

    public void setCreditStatus(CreditStatus creditStatus) {
        this.creditStatus = creditStatus;
    }
}
