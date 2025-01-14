package ru.berezentseva.calculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class preScoringCheckTest {

    @InjectMocks
    private CalculatorService calculatorService;

    @BeforeEach
    public void setup() {
        calculatorService = new CalculatorService();
    }

    // проверяем, что выбрасываются исключения при некорректно заполненных полях

    @Test
    public void testLoanApplicationShouldNotThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");

        Assertions.assertDoesNotThrow(() -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidFirstNameShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("E1");     // тут
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");

        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidLastNameShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("B0");          // тут
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");

        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidMiddleNameShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("1");     // тут
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");

        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidAmountShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(5000));            // тут
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");

        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidTermShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(2);                         // тут
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");
        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidBirthDateShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2023-12-01"));            // тут
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");
        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidEmailShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123example");       // тут
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");

        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidPassportSerialShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("12565");          // тут
        request.setPassportNumber("567050");

        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

    @Test
    public void testNotValidPassportNumberShouldBeWithThrowException() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("56705");            // тут

        assertThrows(ScoreException.class, () -> calculatorService.preScoringCheck(request));
    }

}
