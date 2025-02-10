package ru.berezentseva.calculator.DTO;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;


@ToString
public class PaymentScheduleElementDto {
    private Integer number;
    private LocalDate date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;  // по %
    private BigDecimal debtPayment; // по долгу
    private BigDecimal remainingDebt;   // остаток долга

    public PaymentScheduleElementDto(Integer number, LocalDate date, BigDecimal totalPayment, BigDecimal interestPayment, BigDecimal debtPayment, BigDecimal remainingDebt) {
        this.number = number;
        this.date = date;
        this.totalPayment = totalPayment;
        this.interestPayment = interestPayment;
        this.debtPayment = debtPayment;
        this.remainingDebt = remainingDebt;
    }

    public PaymentScheduleElementDto() {
    }

    public static PaymentScheduleElementDtoBuilder builder() {
        return new PaymentScheduleElementDtoBuilder();
    }

    public Integer getNumber() {
        return this.number;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public BigDecimal getTotalPayment() {
        return this.totalPayment;
    }

    public BigDecimal getInterestPayment() {
        return this.interestPayment;
    }

    public BigDecimal getDebtPayment() {
        return this.debtPayment;
    }

    public BigDecimal getRemainingDebt() {
        return this.remainingDebt;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public void setInterestPayment(BigDecimal interestPayment) {
        this.interestPayment = interestPayment;
    }

    public void setDebtPayment(BigDecimal debtPayment) {
        this.debtPayment = debtPayment;
    }

    public void setRemainingDebt(BigDecimal remainingDebt) {
        this.remainingDebt = remainingDebt;
    }

    public static class PaymentScheduleElementDtoBuilder {
        private Integer number;
        private LocalDate date;
        private BigDecimal totalPayment;
        private BigDecimal interestPayment;
        private BigDecimal debtPayment;
        private BigDecimal remainingDebt;

        PaymentScheduleElementDtoBuilder() {
        }

        public PaymentScheduleElementDtoBuilder number(Integer number) {
            this.number = number;
            return this;
        }

        public PaymentScheduleElementDtoBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public PaymentScheduleElementDtoBuilder totalPayment(BigDecimal totalPayment) {
            this.totalPayment = totalPayment;
            return this;
        }

        public PaymentScheduleElementDtoBuilder interestPayment(BigDecimal interestPayment) {
            this.interestPayment = interestPayment;
            return this;
        }

        public PaymentScheduleElementDtoBuilder debtPayment(BigDecimal debtPayment) {
            this.debtPayment = debtPayment;
            return this;
        }

        public PaymentScheduleElementDtoBuilder remainingDebt(BigDecimal remainingDebt) {
            this.remainingDebt = remainingDebt;
            return this;
        }

        public PaymentScheduleElementDto build() {
            return new PaymentScheduleElementDto(this.number, this.date, this.totalPayment, this.interestPayment, this.debtPayment, this.remainingDebt);
        }

        public String toString() {
            return "PaymentScheduleElementDto.PaymentScheduleElementDtoBuilder(number=" + this.number + ", date=" + this.date + ", totalPayment=" + this.totalPayment + ", interestPayment=" + this.interestPayment + ", debtPayment=" + this.debtPayment + ", remainingDebt=" + this.remainingDebt + ")";
        }
    }
}
