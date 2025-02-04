package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.berezentseva.deal.DTO.Enums.Gender;
import ru.berezentseva.deal.DTO.Enums.MaritalStatus;

import java.time.LocalDate;
import java.util.UUID;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID clientUuid;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false)
    private String email;

    //  @OneToOne
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    //@OneToOne
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    @Column(name = "passport", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Passport passport;

    @Column(name = "employment", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Employment employment;

    @Column(name = "account_number")
    private String accountNumber;

    public UUID getClientUuid() {
        return this.clientUuid;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public String getEmail() {
        return this.email;
    }

    public Gender getGender() {
        return this.gender;
    }

    public MaritalStatus getMaritalStatus() {
        return this.maritalStatus;
    }

    public Integer getDependentAmount() {
        return this.dependentAmount;
    }

    public Passport getPassport() {
        return this.passport;
    }

    public Employment getEmployment() {
        return this.employment;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setClientUuid(UUID clientUuid) {
        this.clientUuid = clientUuid;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    public void setEmployment(Employment employment) {
        this.employment = employment;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
