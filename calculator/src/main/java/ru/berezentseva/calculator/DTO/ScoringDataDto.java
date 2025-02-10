package ru.berezentseva.calculator.DTO;

import lombok.ToString;
import ru.berezentseva.calculator.DTO.Enums.Gender;
import ru.berezentseva.calculator.DTO.Enums.MaritalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@ToString
public class ScoringDataDto {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    public ScoringDataDto(BigDecimal amount, Integer term, String firstName, String lastName, String middleName, Gender gender, LocalDate birthdate, String passportSeries, String passportNumber, LocalDate passportIssueDate, String passportIssueBranch, MaritalStatus maritalStatus, Integer dependentAmount, EmploymentDto employment, String accountNumber, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        this.amount = amount;
        this.term = term;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.gender = gender;
        this.birthdate = birthdate;
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.passportIssueDate = passportIssueDate;
        this.passportIssueBranch = passportIssueBranch;
        this.maritalStatus = maritalStatus;
        this.dependentAmount = dependentAmount;
        this.employment = employment;
        this.accountNumber = accountNumber;
        this.isInsuranceEnabled = isInsuranceEnabled;
        this.isSalaryClient = isSalaryClient;
    }

    public ScoringDataDto() {
    }

    public static ScoringDataDtoBuilder builder() {
        return new ScoringDataDtoBuilder();
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Integer getTerm() {
        return this.term;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public Gender getGender() {
        return this.gender;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public String getPassportSeries() {
        return this.passportSeries;
    }

    public String getPassportNumber() {
        return this.passportNumber;
    }

    public LocalDate getPassportIssueDate() {
        return this.passportIssueDate;
    }

    public String getPassportIssueBranch() {
        return this.passportIssueBranch;
    }

    public MaritalStatus getMaritalStatus() {
        return this.maritalStatus;
    }

    public Integer getDependentAmount() {
        return this.dependentAmount;
    }

    public EmploymentDto getEmployment() {
        return this.employment;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public Boolean getIsInsuranceEnabled() {
        return this.isInsuranceEnabled;
    }

    public Boolean getIsSalaryClient() {
        return this.isSalaryClient;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setPassportIssueDate(LocalDate passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public void setPassportIssueBranch(String passportIssueBranch) {
        this.passportIssueBranch = passportIssueBranch;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setDependentAmount(Integer dependentAmount) {
        this.dependentAmount = dependentAmount;
    }

    public void setEmployment(EmploymentDto employment) {
        this.employment = employment;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setIsInsuranceEnabled(Boolean isInsuranceEnabled) {
        this.isInsuranceEnabled = isInsuranceEnabled;
    }

    public void setIsSalaryClient(Boolean isSalaryClient) {
        this.isSalaryClient = isSalaryClient;
    }

    public static class ScoringDataDtoBuilder {
        private BigDecimal amount;
        private Integer term;
        private String firstName;
        private String lastName;
        private String middleName;
        private Gender gender;
        private LocalDate birthdate;
        private String passportSeries;
        private String passportNumber;
        private LocalDate passportIssueDate;
        private String passportIssueBranch;
        private MaritalStatus maritalStatus;
        private Integer dependentAmount;
        private EmploymentDto employment;
        private String accountNumber;
        private Boolean isInsuranceEnabled;
        private Boolean isSalaryClient;

        ScoringDataDtoBuilder() {
        }

        public ScoringDataDtoBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public ScoringDataDtoBuilder term(Integer term) {
            this.term = term;
            return this;
        }

        public ScoringDataDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ScoringDataDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ScoringDataDtoBuilder middleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public ScoringDataDtoBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public ScoringDataDtoBuilder birthdate(LocalDate birthdate) {
            this.birthdate = birthdate;
            return this;
        }

        public ScoringDataDtoBuilder passportSeries(String passportSeries) {
            this.passportSeries = passportSeries;
            return this;
        }

        public ScoringDataDtoBuilder passportNumber(String passportNumber) {
            this.passportNumber = passportNumber;
            return this;
        }

        public ScoringDataDtoBuilder passportIssueDate(LocalDate passportIssueDate) {
            this.passportIssueDate = passportIssueDate;
            return this;
        }

        public ScoringDataDtoBuilder passportIssueBranch(String passportIssueBranch) {
            this.passportIssueBranch = passportIssueBranch;
            return this;
        }

        public ScoringDataDtoBuilder maritalStatus(MaritalStatus maritalStatus) {
            this.maritalStatus = maritalStatus;
            return this;
        }

        public ScoringDataDtoBuilder dependentAmount(Integer dependentAmount) {
            this.dependentAmount = dependentAmount;
            return this;
        }

        public ScoringDataDtoBuilder employment(EmploymentDto employment) {
            this.employment = employment;
            return this;
        }

        public ScoringDataDtoBuilder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public ScoringDataDtoBuilder isInsuranceEnabled(Boolean isInsuranceEnabled) {
            this.isInsuranceEnabled = isInsuranceEnabled;
            return this;
        }

        public ScoringDataDtoBuilder isSalaryClient(Boolean isSalaryClient) {
            this.isSalaryClient = isSalaryClient;
            return this;
        }

        public ScoringDataDto build() {
            return new ScoringDataDto(this.amount, this.term, this.firstName, this.lastName, this.middleName, this.gender, this.birthdate, this.passportSeries, this.passportNumber, this.passportIssueDate, this.passportIssueBranch, this.maritalStatus, this.dependentAmount, this.employment, this.accountNumber, this.isInsuranceEnabled, this.isSalaryClient);
        }

        public String toString() {
            return "ScoringDataDto.ScoringDataDtoBuilder(amount=" + this.amount + ", term=" + this.term + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ", middleName=" + this.middleName + ", gender=" + this.gender + ", birthdate=" + this.birthdate + ", passportSeries=" + this.passportSeries + ", passportNumber=" + this.passportNumber + ", passportIssueDate=" + this.passportIssueDate + ", passportIssueBranch=" + this.passportIssueBranch + ", maritalStatus=" + this.maritalStatus + ", dependentAmount=" + this.dependentAmount + ", employment=" + this.employment + ", accountNumber=" + this.accountNumber + ", isInsuranceEnabled=" + this.isInsuranceEnabled + ", isSalaryClient=" + this.isSalaryClient + ")";
        }
    }
}

