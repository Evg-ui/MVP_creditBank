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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID statementId;

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)  // FK
    private Client clientUuid;

    @OneToOne
    @JoinColumn(name = "credit_id")  // FK
    private Credit creditUuid;

   // @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private ApplicationStatus status;

    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate;

    // @Type(type = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer", columnDefinition ="jsonb")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private Timestamp signDate;

    @Column(name = "ses_code")
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition ="jsonb")
    private List<StatementStatusHistoryDto> statusHistory;

}
