package ru.berezentseva.deal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.berezentseva.deal.DTO.Enums.ChangeType;

import java.sql.Timestamp;

@Getter
@Setter
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
}
