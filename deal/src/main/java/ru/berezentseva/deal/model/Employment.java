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
 //@TypeDef(name = "json", typeClass = JsonType.class)
public class Employment {

    @Id
    @GeneratedValue
    private UUID employmentUuid;

   // @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status", nullable = true, unique = false)
    private EmploymentStatus status;

    @Column(name = "employment_inn", nullable = false, unique = false)
    private String employmentInn;

    @Column(name = "salary", nullable = false, unique = false)
    private BigDecimal salary;

  //  @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "position", nullable = true, unique = false)
    private Position position;

    @Column(name = "work_experience_total", nullable = false, unique = false)
    private int workExperienceTotal;

    @Column(name = "work_experience_current", nullable = false, unique = false)
    private int workExperienceCurrent;
}
