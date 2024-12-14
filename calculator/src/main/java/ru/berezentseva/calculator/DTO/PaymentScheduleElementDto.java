package ru.berezentseva.calculator.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString
public class PaymentScheduleElementDto {
    private Integer number;
    private LocalDate date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;  // по %
    private BigDecimal debtPayment; // по долгу
    private BigDecimal remainingDebt;   // остаток долга
}
