package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.StatementStatusHistoryDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Statement {

//    public void setStatementId(UUID statementId) {
//        this.statementId = UUID.randomUUID();
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID statementId;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)  // FK
    private Client clientUuid;

    @OneToOne
    @JoinColumn(name = "credit_id", nullable = true)  // FK
    private Credit creditUuid;

   // @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status", nullable  = true, unique = false)
    private ApplicationStatus status;

    @Column(name = "creation_date", nullable = false, unique = false)
    private Timestamp creationDate;

    // @Type(type = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer",  nullable = true, columnDefinition ="jsonb")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date", nullable = true, unique = false)
    private Timestamp signDate;

    @Column(name = "ses_code", nullable = true, unique = false)
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", nullable = true, unique = false, columnDefinition ="jsonb") // времеенно, потому что ничего непонятно тут
    private List<StatementStatusHistoryDto> statusHistory;

}
