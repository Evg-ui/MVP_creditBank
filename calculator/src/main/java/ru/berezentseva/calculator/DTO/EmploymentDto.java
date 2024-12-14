package ru.berezentseva.calculator.DTO;

import lombok.*;
import ru.berezentseva.calculator.DTO.Enums.EmploymentStatus;
import ru.berezentseva.calculator.DTO.Enums.Position;

import java.math.BigDecimal;

@Setter @Getter
@ToString

public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;

}
