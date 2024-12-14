package ru.berezentseva.deal.model;

import lombok.*;
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

    public void setClientUuid(UUID clientUuid) {
        this.clientUuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue//(strategy = GenerationType.IDENTITY)
    private UUID clientUuid;

    @Column(name = "last_name", nullable = false, unique = false)
    private String lastName;

    @Column(name = "first_name", nullable = false, unique = false)
    private String firstName;

    @Column(name = "middle_name", nullable = true, unique = false)
    private String middleName;

    @Column(name = "birth_date", nullable = false, unique = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false, unique = false)
    private String email;

  //  @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "gender", nullable = true, unique = false)
    private Gender gender;

    //@OneToOne
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", nullable = true, unique = false)
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount", nullable = true, unique = false)
    private Integer dependentAmount;

    @OneToOne
    @JoinColumn(name = "passport_id", nullable = true) // Внешний ключ на таблицу паспортов
    private Passport passport;

    @OneToOne
    @JoinColumn(name = "employment_id", nullable = true) // Внешний ключ на таблицу работы
    private Employment employment;

    @Column(name = "account_number", nullable = true, unique = false)
    private String accountNumber;
}
