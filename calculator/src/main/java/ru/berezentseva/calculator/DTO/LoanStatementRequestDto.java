package ru.berezentseva.calculator.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor

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

