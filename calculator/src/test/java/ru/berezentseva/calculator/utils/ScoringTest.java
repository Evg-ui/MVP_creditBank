package ru.berezentseva.calculator.utils;

import org.junit.jupiter.api.Test;
import ru.berezentseva.calculator.DTO.Enums.EmploymentStatus;
import ru.berezentseva.calculator.DTO.Enums.Gender;
import ru.berezentseva.calculator.DTO.Enums.MaritalStatus;
import ru.berezentseva.calculator.DTO.Enums.Position;
import ru.berezentseva.calculator.exception.ScoreException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.berezentseva.calculator.utils.Scoring.getEmploymentRate;

class ScoringTest {
    // проверяем корректность скоринга при разных значениях полей полной заявки

    /*Позиция на работе*/
    @Test
    public void testMidManagerReturnsMinusTwoRate() {
        assertEquals(new BigDecimal(-2), Scoring.getPositionRate(Position.MID_MANAGER));
    }

    @Test
    public void testTopManagerReturnsMinusThreeRate() {
        assertEquals(new BigDecimal(-3), Scoring.getPositionRate(Position.TOP_MANAGER));
    }

    @Test
    public void testOwnerReturnsPlusTwoRate() {
        assertEquals(new BigDecimal(2), Scoring.getPositionRate(Position.OWNER));
    }

    @Test
    public void testDefaultPositionReturnsZeroRate() {
        assertEquals(BigDecimal.ZERO, Scoring.getPositionRate(Position.WORKER));
    }

    /*Рабочий статус*/
    @Test
    public void testSelfemployedReturnsPlusTwoRate() throws ScoreException {
        assertEquals(new BigDecimal(2), getEmploymentRate(EmploymentStatus.SELFEMPLOYED));
    }

    @Test
    public void testEmployedReturnsPlusThreeRate() throws ScoreException {
        assertEquals(BigDecimal.ZERO, getEmploymentRate(EmploymentStatus.EMPLOYED));
    }

    @Test
    public void testBusinessOwnerReturnsPlusOneRate() throws ScoreException {
        assertEquals(BigDecimal.ONE, getEmploymentRate(EmploymentStatus.BUSINESSOWNER));
    }

    @Test
    public void testUnemployedReturnsDenied() {
        ScoreException thrown = assertThrows(ScoreException.class, () -> {
            getEmploymentRate(EmploymentStatus.UNEMPLOYED);
        });
        assertEquals("Denied by status \"UNEMPLOYED\"", thrown.getMessage());
    }

    @Test
    // тут дополнительно выбросим исключение по неизвестным статусам
    public void testUnemployedReturnsDeniedException()  {
        ScoreException thrown = assertThrows(ScoreException.class, () -> {
            getEmploymentRate(EmploymentStatus.UNKNOWN);
        });
        assertEquals("Unknown employment status.", thrown.getMessage());
    }


    /*Семейное положение*/
    @Test
    public void testMarriedReturnsMinusThreeRate() {
        assertEquals(new BigDecimal(-3), Scoring.getMaritalStatusRate(MaritalStatus.MARRIED));
    }

    @Test
    public void testDivorcedReturnsPlusOneRate() {
        assertEquals(BigDecimal.ONE, Scoring.getMaritalStatusRate(MaritalStatus.DIVORCED));
    }

    @Test
    public void testWidowedReturnsMinusOneRate() {
        assertEquals(new BigDecimal(-1), Scoring.getMaritalStatusRate(MaritalStatus.WIDOWED));
    }

    @Test
    public void testDefaultMaritalStatusReturnsZeroRate() {
        assertEquals(BigDecimal.ZERO, Scoring.getMaritalStatusRate(MaritalStatus.SINGLE));
    }

    /*Пол и возраст*/
    @Test
    public void testMaleAndAgeNotBetween30And55ReturnsZeroRate() throws ScoreException {
        int age = 57;
        assertEquals(BigDecimal.ZERO, Scoring.getGenderAndAgeRate(Gender.MALE, age));
    }

    @Test
    public void testMaleAndAgeBetween30And55ReturnsZeroRate() throws ScoreException {
        int age = 40;
        assertEquals(new BigDecimal(-3), Scoring.getGenderAndAgeRate(Gender.MALE, age));
    }

    @Test
    public void testFemaleAndAgeNotBetween32And60ReturnsZeroRate() throws ScoreException {
        int age = 62;
        assertEquals(BigDecimal.ZERO, Scoring.getGenderAndAgeRate(Gender.MALE, age));
    }

    @Test
    public void testFemaleAndAgeBetween32And60ReturnsZeroRate() throws ScoreException {
        int age = 45;
        assertEquals(new BigDecimal(-3), Scoring.getGenderAndAgeRate(Gender.FEMALE, age));
    }


    @Test
    public void testOtherGenderReturnsPlusSevenRate() throws ScoreException {
        int age = 21;
        assertEquals(new BigDecimal(7), Scoring.getGenderAndAgeRate(Gender.OTHER, age));
    }
}


