package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@ToString
@Getter @Setter
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

    }
