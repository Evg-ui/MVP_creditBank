package ru.berezentseva.calculator.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
public class LoanStatementRequestDto {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private LocalDate birthDate;
    private String passportSeries;
    private String passportNumber;
}

