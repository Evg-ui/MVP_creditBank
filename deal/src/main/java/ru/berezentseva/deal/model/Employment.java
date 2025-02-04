package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import lombok.ToString;
import ru.berezentseva.deal.DTO.Enums.EmploymentStatus;
import ru.berezentseva.deal.DTO.Enums.Position;

import java.math.BigDecimal;
import java.util.UUID;

@ToString
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

    public Employment(UUID employmentUuid, EmploymentStatus status, String employmentInn, BigDecimal salary, Position position, int workExperienceTotal, int workExperienceCurrent) {
        this.employmentUuid = employmentUuid;
        this.status = status;
        this.employmentInn = employmentInn;
        this.salary = salary;
        this.position = position;
        this.workExperienceTotal = workExperienceTotal;
        this.workExperienceCurrent = workExperienceCurrent;
    }

    public Employment() {
    }

    public UUID getEmploymentUuid() {
        return this.employmentUuid;
    }

    public EmploymentStatus getStatus() {
        return this.status;
    }

    public String getEmploymentInn() {
        return this.employmentInn;
    }

    public BigDecimal getSalary() {
        return this.salary;
    }

    public Position getPosition() {
        return this.position;
    }

    public int getWorkExperienceTotal() {
        return this.workExperienceTotal;
    }

    public int getWorkExperienceCurrent() {
        return this.workExperienceCurrent;
    }

    public void setEmploymentUuid(UUID employmentUuid) {
        this.employmentUuid = employmentUuid;
    }

    public void setStatus(EmploymentStatus status) {
        this.status = status;
    }

    public void setEmploymentInn(String employmentInn) {
        this.employmentInn = employmentInn;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setWorkExperienceTotal(int workExperienceTotal) {
        this.workExperienceTotal = workExperienceTotal;
    }

    public void setWorkExperienceCurrent(int workExperienceCurrent) {
        this.workExperienceCurrent = workExperienceCurrent;
    }
}
