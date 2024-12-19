package ru.berezentseva.deal.model;

import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.berezentseva.calculator.DTO.Enums.Gender;

import jakarta.persistence.*;
import ru.berezentseva.calculator.DTO.Enums.MaritalStatus;

import java.time.LocalDate;
import java.util.UUID;

@ToString
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
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

    @Column(name = "passport", nullable = false, columnDefinition ="jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Passport passport;

    @Column(name = "employment", columnDefinition ="jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Employment employment;

    @Column(name = "account_number")
    private String accountNumber;
}
