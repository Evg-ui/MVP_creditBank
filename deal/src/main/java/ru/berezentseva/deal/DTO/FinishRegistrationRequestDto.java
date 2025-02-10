package ru.berezentseva.deal.DTO;



import ru.berezentseva.deal.DTO.Enums.Gender;
import ru.berezentseva.deal.DTO.Enums.MaritalStatus;

import java.time.LocalDate;

public class FinishRegistrationRequestDto {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDto employment;
    private String accountNumber;

    public Gender getGender() {
        return this.gender;
    }

    public MaritalStatus getMaritalStatus() {
        return this.maritalStatus;
    }

    public Integer getDependentAmount() {
        return this.dependentAmount;
    }

    public LocalDate getPassportIssueDate() {
        return this.passportIssueDate;
    }

    public String getPassportIssueBranch() {
        return this.passportIssueBranch;
    }

    public EmploymentDto getEmployment() {
        return this.employment;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setDependentAmount(Integer dependentAmount) {
        this.dependentAmount = dependentAmount;
    }

    public void setPassportIssueDate(LocalDate passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public void setPassportIssueBranch(String passportIssueBranch) {
        this.passportIssueBranch = passportIssueBranch;
    }

    public void setEmployment(EmploymentDto employment) {
        this.employment = employment;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
