package ru.berezentseva.deal.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.berezentseva.deal.DTO.Enums.ChangeType;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
public class StatusHistory {

    @Column(name = "status")
    private Statement statusHistory;

    @Column(name = "time", nullable = false)
    private Timestamp time;

    //  @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "change_type")
    private ChangeType changeType;

    public Statement getStatusHistory() {
        return this.statusHistory;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public ChangeType getChangeType() {
        return this.changeType;
    }

    public void setStatusHistory(Statement statusHistory) {
        this.statusHistory = statusHistory;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }
}
