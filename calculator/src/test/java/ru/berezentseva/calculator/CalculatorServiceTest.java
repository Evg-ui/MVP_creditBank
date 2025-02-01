package ru.berezentseva.calculator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.berezentseva.calculator.DTO.*;
import ru.berezentseva.calculator.exception.ScoreException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@ExtendWith(MockitoExtension.class)

@SpringBootTest
class CalculatorServiceTest {

public static final BigDecimal baseRate = BigDecimal.valueOf(15);

    @InjectMocks
    private CalculatorService calculatorService;

    @BeforeEach
    public void setup() {
      calculatorService.setBaseRate(BigDecimal.valueOf(20));
    }


//    @Value("${baseRate}")
//    private BigDecimal baseRate;

    @Test
    public void calcMonthlyPaymentTest() {
        // здесь проверяем, что рассчитанный на стороннем ресурсе платеж совпадет с нашим расчетом
        BigDecimal amount = BigDecimal.valueOf(300000);
        int term = 6;
        BigDecimal rate = BigDecimal.valueOf(15);
        BigDecimal monthlyPayment = calculatorService.calcMonthlyPayment(amount, rate, term);
        // при данных значениях ежемесячный платеж = 52210  его и ожидаем
        assertEquals(BigDecimal.valueOf(52210), monthlyPayment.setScale(0, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void CreditDtoSetterTest() {
        // здесь мы будем проверять, что заданные значения корректно попадают в сущность Кредит
        BigDecimal amount = new BigDecimal("300000");
        Integer term = 12;
        BigDecimal monthlyPayment = new BigDecimal("52210");
        BigDecimal rate = new BigDecimal("15");
        BigDecimal psk = new BigDecimal("10");
        Boolean isInsuranceEnabled = true;
        Boolean isSalaryClient = false;
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();

        CreditDto creditDto = new CreditDto();
        creditDto.setAmount(amount);
        creditDto.setTerm(term);
        creditDto.setMonthlyPayment(monthlyPayment);
        creditDto.setRate(rate);
        creditDto.setPsk(psk);
        creditDto.setIsInsuranceEnabled(isInsuranceEnabled);
        creditDto.setIsSalaryClient(isSalaryClient);
        creditDto.setPaymentSchedule(paymentSchedule);

        Assertions.assertEquals(amount, creditDto.getAmount());
        Assertions.assertEquals(term, creditDto.getTerm());
        Assertions.assertEquals(monthlyPayment, creditDto.getMonthlyPayment());
        Assertions.assertEquals(rate, creditDto.getRate());
        Assertions.assertEquals(psk, creditDto.getPsk());
        Assertions.assertEquals(isInsuranceEnabled, creditDto.getIsInsuranceEnabled());
        Assertions.assertEquals(isSalaryClient, creditDto.getIsSalaryClient());
        Assertions.assertEquals(paymentSchedule, creditDto.getPaymentSchedule());
    }

    @Test
    public void calcPskTest() {
        // проверяем расчет ПСК
        BigDecimal amount = BigDecimal.valueOf(300000); // Сумма кредита
        Integer term = 6; // Срок кредита в месяцах
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();

        paymentSchedule.add(new PaymentScheduleElementDto(1,
                LocalDate.now(),
                BigDecimal.valueOf(52210.14), // ежем платеж
                BigDecimal.valueOf(3698.63), // %
                BigDecimal.valueOf(48511.51), // осн долг
                BigDecimal.valueOf(251488.49))); // остаток 1 мес
        paymentSchedule.add(new PaymentScheduleElementDto(2,
                LocalDate.now(),
                BigDecimal.valueOf(52210.14), // ежем платеж
                BigDecimal.valueOf(3203.89), // %
                BigDecimal.valueOf(49006.25), // осн долг
                BigDecimal.valueOf(202482.24)));   // остаток 2 мес
        paymentSchedule.add(new PaymentScheduleElementDto(2,
                LocalDate.now(),
                BigDecimal.valueOf(52210.14), // ежем платеж
                BigDecimal.valueOf(2579.56), // %
                BigDecimal.valueOf(49630.58), // осн долг
                BigDecimal.valueOf(152851.66)));   // остаток 3 мес
        paymentSchedule.add(new PaymentScheduleElementDto(2,
                LocalDate.now(),
                BigDecimal.valueOf(52210.14), // ежем платеж
                BigDecimal.valueOf(1758.84), // %
                BigDecimal.valueOf(50451.30), // осн долг
                BigDecimal.valueOf(102400.36)));   // остаток 4 мес
        paymentSchedule.add(new PaymentScheduleElementDto(2,
                LocalDate.now(),
                BigDecimal.valueOf(52210.14), // ежем платеж
                BigDecimal.valueOf(1304.55), // %
                BigDecimal.valueOf(51494.77), // осн долг
                BigDecimal.valueOf(8354.07)));   // остаток 5 мес
        paymentSchedule.add(new PaymentScheduleElementDto(2,
                LocalDate.now(),
                BigDecimal.valueOf(52210.14), // ежем платеж
                BigDecimal.valueOf(634.86), // %
                BigDecimal.valueOf(52129.63), // осн долг
                BigDecimal.valueOf(8354.07)));   // остаток 6 мес

        BigDecimal psk = calculatorService.calcPsk(amount, term, paymentSchedule);

        // Assert
       // BigDecimal expectedPsk = BigDecimal.valueOf(-83.000); // Ожидаемое значение PSK
        Assertions.assertEquals(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP),
                psk.setScale(2, RoundingMode.HALF_UP),
                "Значение PSK некорректно");

    }

    @Test
    public void testCalcPaymentScheduleReturnsCountElementsEqualsTerm(){
        List<PaymentScheduleElementDto> result;
        BigDecimal amount = BigDecimal.valueOf(300000);
        Integer term = 6;
        BigDecimal rate = BigDecimal.valueOf(15);
        BigDecimal monthlyPayment = BigDecimal.valueOf(52210.14);
        result = calculatorService.calcPaymentSchedule(amount, term, rate, monthlyPayment);

        assertEquals(term, result.size());
    }

    @Test
    // проверяем, что в графике сумма основного долга суммарно не превышает сумму кредита(с учетом остатка долга)
    public void testCalcPaymentScheduleReturnsSumRemainingPaysEqualsAmount(){
        List<PaymentScheduleElementDto> result;
       // PaymentScheduleElementDto pScheduleElement = new PaymentScheduleElementDto();

        BigDecimal amount = BigDecimal.valueOf(300000);
        Integer term = 6;
        BigDecimal rate = BigDecimal.valueOf(15);
        BigDecimal monthlyPayment = BigDecimal.valueOf(52210.14);

        result = calculatorService.calcPaymentSchedule(amount, term, rate, monthlyPayment);

        BigDecimal totalDebt = BigDecimal.ZERO;
        BigDecimal curAmount = amount;
        for (PaymentScheduleElementDto pElement : result) {
            totalDebt = totalDebt.add(pElement.getDebtPayment().setScale(2, RoundingMode.HALF_UP));

        }
        curAmount = curAmount.subtract(totalDebt.setScale(2, RoundingMode.HALF_UP));
        PaymentScheduleElementDto lastElement = result.get(result.size() - 1);
        lastElement.setDebtPayment(lastElement.getDebtPayment().add(curAmount).setScale(2, RoundingMode.HALF_UP));
        //totalDebt.add(curAmount.setScale(2, RoundingMode.HALF_UP));

        // повторно пересчитываем
        totalDebt = BigDecimal.ZERO;

        for (PaymentScheduleElementDto pElement : result) {
            totalDebt = totalDebt.add(pElement.getDebtPayment().setScale(2, RoundingMode.HALF_UP));

        }
       // curAmount = curAmount.subtract(totalDebt.setScale(2, RoundingMode.HALF_UP));

        assertEquals(amount.setScale(2, RoundingMode.HALF_UP), totalDebt.setScale(2, RoundingMode.HALF_UP));
    }


    /* проверяем, что выбрасываются исключения при несоответствии условиям скоринга*/
    // запрос суммы кредита > 24 зарплат
    @Test
    public void testScoringCheck24Salaries(){
        BigDecimal amount = BigDecimal.valueOf(300000);
        BigDecimal salary = BigDecimal.valueOf(10000);

        String inn = "123457890";

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(amount);

        // Информация о занятости
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(salary);
        employment.setEmployerINN(inn);

        scoringDataDto.setEmployment(employment);

        ScoreException thrown = Assertions.assertThrows(ScoreException.class, () -> {
            calculatorService.scoringCheck(scoringDataDto);
        });
       assertEquals("Credit's sum is more than 24 salaries. Denied.", thrown.getMessage());
    }


    // общий стаж менее 18 месяцев
    @Test
    public void testScoringCheckExperienceTotalLessThan18Months(){
        BigDecimal amount = BigDecimal.valueOf(300000);     // для запуска нужны эти 3 показателя
        BigDecimal salary = BigDecimal.valueOf(100000);
        String inn = "1234567890";

        int experienceTotal = 13;      // прооверка этого показателя

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(amount);

        // Информация о занятости
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(salary);
        employment.setWorkExperienceTotal(experienceTotal);
        employment.setEmployerINN(inn);

        scoringDataDto.setEmployment(employment);

        ScoreException thrown = assertThrows(ScoreException.class, () -> {
            calculatorService.scoringCheck(scoringDataDto);
        });
        assertEquals("Total Experience less then 18 months. Denied.", thrown.getMessage());
    //    assertNotNull(thrown.getMessage());
    }

    // на текущем месте менее 3 месяцев
    @Test
    public void testScoringCheckExperienceCurLessThan3Months(){
        int experienceTotal = 20;       // для запуска нужны эти 4 показателя
        BigDecimal amount = BigDecimal.valueOf(300000);
        BigDecimal salary = BigDecimal.valueOf(100000);
        String inn = "1234567890";

        int experienceCur = 2;      // прооверка этого показателя

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(amount);

        // Информация о занятости
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(salary);
        employment.setWorkExperienceCurrent(experienceCur);
        employment.setWorkExperienceTotal(experienceTotal);
        employment.setEmployerINN(inn);

        scoringDataDto.setEmployment(employment);

        ScoreException thrown = assertThrows(ScoreException.class, () -> {
            calculatorService.scoringCheck(scoringDataDto);
        });
        assertEquals("Current Experience less then 3 months. Denied.", thrown.getMessage());
    }

    // тест на возраст
    @Test
    public void testScoringCheckAgeMoreThan65(){
        int experienceTotal = 20;       // для запуска нужны эти 4 показателя
        int experienceCur = 6;

        String inn = "123456789";
        BigDecimal amount = new BigDecimal(20000);
        BigDecimal salary = new BigDecimal(5000);

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(amount);       //для запуска
        scoringDataDto.setBirthdate(LocalDate.now().minusYears(67));


        // Информация о занятости
        EmploymentDto employment = new EmploymentDto();
        employment.setSalary(salary);
        employment.setWorkExperienceCurrent(experienceCur);
        employment.setWorkExperienceTotal(experienceTotal);
        employment.setEmployerINN(inn);

        scoringDataDto.setEmployment(employment);

        // Проверяем, что метод выбрасывает исключение
        ScoreException thrown = assertThrows(ScoreException.class, () -> {
            calculatorService.scoringCheck(scoringDataDto);
        });
        assertEquals("The applicant does not meet the age requirements. Denied.", thrown.getMessage());
    }

    @Test
    public void testScoringCheckAgeLessThan20()  {
        int experienceTotal = 20;       // для запуска нужны эти 5 показателей
        int experienceCur = 6;

        String inn = "123456789";
        BigDecimal amount = new BigDecimal(300000);
        BigDecimal salary = new BigDecimal(30000);

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        scoringDataDto.setAmount(amount);       //для запуска
        scoringDataDto.setBirthdate(LocalDate.now().minusYears(18));

        // Информация о занятости
        EmploymentDto employment = new EmploymentDto();
        employment.setEmployerINN(inn);
        employment.setSalary(salary);
        employment.setWorkExperienceCurrent(experienceCur);
        employment.setWorkExperienceTotal(experienceTotal);

        scoringDataDto.setEmployment(employment);

        // Проверяем, что метод выбрасывает исключение
        ScoreException thrown = assertThrows(ScoreException.class, () -> {
            calculatorService.scoringCheck(scoringDataDto);
        });

        assertEquals("The applicant does not meet the age requirements. Denied.", thrown.getMessage());
    }

    @Test
    public void testGetLoanOffersFourElements() {
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

       // BigDecimal baseRate = calculatorService.getBaseRate();
        // Выполнение метода
        log.info("Start getLoanOffers...");
        log.info(baseRate.toString());
        List<LoanOfferDto> offers = calculatorService.getLoanOffers(request);
        log.info("Stop getLoanOffers...");

        // Проверка результатов
        assertEquals(4, offers.size(), "Должно быть 4 оффера");
    }
}