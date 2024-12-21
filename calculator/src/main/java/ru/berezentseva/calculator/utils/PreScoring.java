package ru.berezentseva.calculator.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

@Getter @Setter
@ToString
@Slf4j
public class PreScoring {

    public static final String firstName_REGEX =  Pattern.compile("[a-zA-z]{2,30}").toString();
    public static final String lastName_REGEX = Pattern.compile("[a-zA-z]{2,30}").toString();
    public static final String middleName_REGEX = Pattern.compile("^$|[a-zA-z]{2,30}").toString();
    public static final BigDecimal amount_MIN = BigDecimal.valueOf(20000);
    public static final Integer term_MIN = 6;
    private static final String passportSeries_REGEX = Pattern.compile("^\\d{4}$").toString();
    private static final String passportNumber_REGEX = Pattern.compile("^\\d{6}$").toString();
    public static final Long age_MIN = 18L;
    public static final String email_REGEX = Pattern.compile("^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$").toString();

    public void validate(LoanStatementRequestDto RequestDto) throws ScoreException {
        validateName(RequestDto.getFirstName(), RequestDto.getLastName(), RequestDto.getMiddleName());
        validateAge(RequestDto.getBirthDate());
        validateTerm(RequestDto.getTerm());
        validateAmount(RequestDto.getAmount());
        validatePassport(RequestDto.getPassportSeries(), RequestDto.getPassportNumber());
        validateEmail(RequestDto.getEmail());
    }

    private void validateAmount(BigDecimal amount) throws ScoreException {
        if (amount.compareTo(amount_MIN) == -1) {
            throw new ScoreException("Сумма кредита должна быть > = 20000. Введено значение: " + amount);
        }
    }

    private void validateTerm(int term) throws ScoreException {
        if (term < term_MIN) {
            throw new ScoreException("Срок кредита должен быть > = 6 месяцев. Введено значение: " + term);
        }
    }

    private void validateName(String firstName, String lastName, String middleName) throws ScoreException {

        if (!firstName.matches(firstName_REGEX)) {
            throw new ScoreException("В имени должно быть от 2 до 30 символов на латинице. Введено значение: " + firstName);
        }
        if (!lastName.matches(lastName_REGEX)) {
            throw new ScoreException("В фамилии должно быть от 2 до 30 символов на латинице. Введено значение: " + lastName);
        }

//        if (middleName == null) {
//            throw new ScoreException("Отчество не должно быть null.");
//        }

        if (middleName != null && !middleName.matches(middleName_REGEX)) {
            throw new ScoreException("В отчестве должно быть от 2 до 30 символов на латинице. Введено значение: " + middleName);
        }
    }

    private void validateEmail(String email) throws ScoreException {
        if (!email.matches(email_REGEX)) {
            throw new ScoreException("Некорректный формат email. Введено значение: " + email);
        }
    }

    private void validateAge(LocalDate birthDate) throws ScoreException {
        if (birthDate.isAfter(LocalDate.now().minusYears(age_MIN))) {
            throw new ScoreException("Возраст заемщика должен быть старше 18 лет.");
        }
    }

    private void validatePassport(String passportSeries, String passportNumber) throws ScoreException {
        if (!passportSeries.matches(passportSeries_REGEX)) {
            throw new ScoreException("Серия паспорта должна содержать 4 цифры. Введено значение: " + passportSeries);
        }
        if (!passportNumber.matches(passportNumber_REGEX)) {
            throw new ScoreException("Номер паспорта должен содержать 6 цифр. Введено значение: " + passportNumber);
        }
    }
}

