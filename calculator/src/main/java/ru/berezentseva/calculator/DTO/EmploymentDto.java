package ru.berezentseva.calculator.DTO;

import lombok.ToString;
import ru.berezentseva.calculator.DTO.Enums.EmploymentStatus;
import ru.berezentseva.calculator.DTO.Enums.Position;

import java.math.BigDecimal;

@ToString

public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;

    public EmploymentStatus getEmploymentStatus() {
        return this.employmentStatus;
    }

    public String getEmployerINN() {
        return this.employerINN;
    }

    public BigDecimal getSalary() {
        return this.salary;
    }

    public Position getPosition() {
        return this.position;
    }

    public Integer getWorkExperienceTotal() {
        return this.workExperienceTotal;
    }

    public Integer getWorkExperienceCurrent() {
        return this.workExperienceCurrent;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public void setEmployerINN(String employerINN) {
        this.employerINN = employerINN;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setWorkExperienceTotal(Integer workExperienceTotal) {
        this.workExperienceTotal = workExperienceTotal;
    }

    public void setWorkExperienceCurrent(Integer workExperienceCurrent) {
        this.workExperienceCurrent = workExperienceCurrent;
    }
}
