package ru.berezentseva.deal.DTO;

import lombok.Getter;
import lombok.Setter;
import ru.berezentseva.calculator.DTO.EmploymentDto;
import ru.berezentseva.calculator.DTO.Enums.Gender;
import ru.berezentseva.calculator.DTO.Enums.MaritalStatus;

import java.time.LocalDate;

@Getter
@Setter
public class FinishRegistrationRequestDto {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDto employment;
    private String accountNumber;
}
