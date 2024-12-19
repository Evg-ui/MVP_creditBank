package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import lombok.*;
import ru.berezentseva.calculator.DTO.Enums.EmploymentStatus;
import ru.berezentseva.calculator.DTO.Enums.Position;

import java.math.BigDecimal;
import java.util.UUID;

@ToString
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table
public class Employment {

    @Id
    @GeneratedValue
    private UUID employmentUuid;

   // @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private EmploymentStatus status;

    @Column(name = "employment_inn")
    private String employmentInn;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

  //  @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "position")
    private Position position;

    @Column(name = "work_experience_total", nullable = false)
    private int workExperienceTotal;

    @Column(name = "work_experience_current", nullable = false)
    private int workExperienceCurrent;
}
