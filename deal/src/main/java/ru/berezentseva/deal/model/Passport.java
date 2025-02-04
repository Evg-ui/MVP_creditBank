package ru.berezentseva.deal.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Passport {
    @Id
    @GeneratedValue
    private UUID passportUuid;

    @Column(name = "series", nullable = false)
    private String series;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "issue_branch")
    private String issueBranch;

    @Column(name = "issue_date")
    private Date issueDate;

    public UUID getPassportUuid() {
        return this.passportUuid;
    }

    public String getSeries() {
        return this.series;
    }

    public String getNumber() {
        return this.number;
    }

    public String getIssueBranch() {
        return this.issueBranch;
    }

    public Date getIssueDate() {
        return this.issueDate;
    }

    public void setPassportUuid(UUID passportUuid) {
        this.passportUuid = passportUuid;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setIssueBranch(String issueBranch) {
        this.issueBranch = issueBranch;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
}
