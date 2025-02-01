package ru.berezentseva.calculator.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
@Builder

public class LoanOfferDto {
    private UUID statementId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

}
