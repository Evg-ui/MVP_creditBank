package ru.berezentseva.deal.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.Enums.ChangeType;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StatementStatusHistoryDto {
    private ApplicationStatus status;
    private LocalDateTime time;
    private ChangeType changeType;

    public ApplicationStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public ChangeType getChangeType() {
        return this.changeType;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }
}
