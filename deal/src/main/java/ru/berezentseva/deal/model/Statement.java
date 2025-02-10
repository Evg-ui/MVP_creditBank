package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.LoanOfferDto;
import ru.berezentseva.deal.DTO.StatementStatusHistoryDto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

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
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private Timestamp signDate;

    @Column(name = "ses_code")
    private String sesCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<StatementStatusHistoryDto> statusHistory;

    public Statement(UUID statementId, Client clientUuid, Credit creditUuid, ApplicationStatus status, Timestamp creationDate, LoanOfferDto appliedOffer, Timestamp signDate, String sesCode, List<StatementStatusHistoryDto> statusHistory) {
        this.statementId = statementId;
        this.clientUuid = clientUuid;
        this.creditUuid = creditUuid;
        this.status = status;
        this.creationDate = creationDate;
        this.appliedOffer = appliedOffer;
        this.signDate = signDate;
        this.sesCode = sesCode;
        this.statusHistory = statusHistory;
    }

    public Statement() {
    }

    public UUID getStatementId() {
        return this.statementId;
    }

    public Client getClientUuid() {
        return this.clientUuid;
    }

    public Credit getCreditUuid() {
        return this.creditUuid;
    }

    public ApplicationStatus getStatus() {
        return this.status;
    }

    public Timestamp getCreationDate() {
        return this.creationDate;
    }

    public LoanOfferDto getAppliedOffer() {
        return this.appliedOffer;
    }

    public Timestamp getSignDate() {
        return this.signDate;
    }

    public String getSesCode() {
        return this.sesCode;
    }

    public List<StatementStatusHistoryDto> getStatusHistory() {
        return this.statusHistory;
    }

    public void setStatementId(UUID statementId) {
        this.statementId = statementId;
    }

    public void setClientUuid(Client clientUuid) {
        this.clientUuid = clientUuid;
    }

    public void setCreditUuid(Credit creditUuid) {
        this.creditUuid = creditUuid;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public void setAppliedOffer(LoanOfferDto appliedOffer) {
        this.appliedOffer = appliedOffer;
    }

    public void setSignDate(Timestamp signDate) {
        this.signDate = signDate;
    }

    public void setSesCode(String sesCode) {
        this.sesCode = sesCode;
    }

    public void setStatusHistory(List<StatementStatusHistoryDto> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public String toString() {
        return "Statement(statementId=" + this.getStatementId() + ", clientUuid=" + this.getClientUuid() + ", creditUuid=" + this.getCreditUuid() + ", status=" + this.getStatus() + ", creationDate=" + this.getCreationDate() + ", appliedOffer=" + this.getAppliedOffer() + ", signDate=" + this.getSignDate() + ", sesCode=" + this.getSesCode() + ", statusHistory=" + this.getStatusHistory() + ")";
    }
}
