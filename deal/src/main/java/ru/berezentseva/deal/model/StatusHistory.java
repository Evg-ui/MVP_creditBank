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
@Entity
@Table
//@TypeDef(name = "json", typeClass = JsonType.class)
public class StatusHistory {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "status", nullable = true, unique = false)
    private Statement statusHistory;

    @Column(name = "time", nullable = false, unique = false)
    private Timestamp time;

  //  @OneToOne
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "change_type", nullable = true, unique = false)
    private ChangeType changeType;
}
