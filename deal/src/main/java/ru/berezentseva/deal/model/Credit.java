package ru.berezentseva.deal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.berezentseva.deal.DTO.Enums.CreditStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID creditUuid;

    @Column(name = "amount", nullable = false, unique = false)
    private BigDecimal amount;

    @Column(name = "term", nullable = false, unique = false)
    private int term;

    @Column(name = "monthly_payment", nullable = false, unique = false)
    private BigDecimal monthlyPayment;

    @Column(name = "rate", nullable = false, unique = false)
    private BigDecimal rate;

    @Column(name = "psk", nullable = false, unique = false)
    private BigDecimal psk;

 //   @Type(type = "jsonb")
    @Column(name = "payment_schedule", columnDefinition ="jsonb")
   // private PaymentScheduleElementDto payment_schedule;
    private String paymentSchedule;

    @Column(name = "insurance_enabled", nullable = false, unique = false)
    private Boolean insuranceEnabled;

    @Column(name = "salary_client", nullable = false, unique = false)
    private Boolean salaryClient;

    // @OneToOne
    @JoinColumn(name = "credit_status", nullable = true, unique = false)
    private CreditStatus creditStatus;
}
