package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
//@TypeDef(name = "json", typeClass = JsonType.class)

public class Passport {
    @Id
    @GeneratedValue
    private UUID passportUuid;

    @Column(name = "series", nullable = false, unique = false)
    private String series;

    @Column(name = "number", nullable = false, unique = false)
    private String number;

    @Column(name = "issue_branch", nullable = true, unique = false)
    private String issueBranch;

    @Column(name = "issue_date", nullable = true, unique = false)
    private Date issueDate;

    }
