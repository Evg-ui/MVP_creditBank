package ru.berezentseva.calculator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.berezentseva.calculator.DTO.*;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.calculator.utils.PreScoring;
import ru.berezentseva.calculator.utils.Scoring;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.berezentseva.calculator.utils.Scoring.inn_REGEX;

@Slf4j
@Service        // для обозначения класса как сервиса, который содержит бизнес-логику приложения

public class CalculatorService {

    private BigDecimal baseRate;

    @Value(value = "${baseRate}")
    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    /*формирование списка из 4 предложений, на вход - данные заявки, которая прошла прескоринг */
    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto requestDto){
        log.info("Расчет предложений для клиента...");
        List<LoanOfferDto> offersDto = new ArrayList<>();
        BigDecimal preMonthlyPayment, preTotalAmount, preRate;   // для предложений

        /*поочередно им рассчитываем показатели*/
        log.info("Расчет 1го предложения...");
        preRate = baseRate.add(BigDecimal.valueOf(2));    // повышаем базовую ставку, если нет зп и нет страховки на 2%
        preMonthlyPayment = this.calcMonthlyPayment(requestDto.getAmount(), preRate, requestDto.getTerm());
        preTotalAmount = preMonthlyPayment.multiply(BigDecimal.valueOf(requestDto.getTerm()));
        LoanOfferDto current1 = LoanOfferDto.builder()
                .isSalaryClient(false)
                .isInsuranceEnabled(false)
                .term(requestDto.getTerm())
                .requestedAmount(requestDto.getAmount())
                .rate(preRate)     // сохраняем базовую ставку, если нет зп и нет страховки
                .term(requestDto.getTerm())
                .totalAmount(preTotalAmount)
                .monthlyPayment(preMonthlyPayment)
                .statementId(UUID.randomUUID()) // Генерация случайного UUID
                .build();
        offersDto.add(current1);

        log.info("Расчет 2го предложения...");
        preRate = baseRate.subtract(BigDecimal.valueOf(1));   // уменьшаем ставку на 1% клиенту с зп
        preMonthlyPayment = this.calcMonthlyPayment(requestDto.getAmount(), preRate, requestDto.getTerm());
        preTotalAmount = preMonthlyPayment.multiply(BigDecimal.valueOf(requestDto.getTerm()));
        LoanOfferDto current2 = LoanOfferDto.builder()
                .isSalaryClient(true)
                .isInsuranceEnabled(false)
                .term(requestDto.getTerm())
                .requestedAmount(requestDto.getAmount())
                .rate(preRate)
                .term(requestDto.getTerm())
                .totalAmount(preTotalAmount)
                .monthlyPayment(preMonthlyPayment)
                .statementId(UUID.randomUUID())
                .build();
        offersDto.add(current2);

        log.info("Расчет 3го предложения...");
        preRate = baseRate;   // сохраняем ставку клиенту со страховкой и без зп
        preMonthlyPayment = this.calcMonthlyPayment(requestDto.getAmount(), preRate, requestDto.getTerm());
        preTotalAmount = preMonthlyPayment.multiply(BigDecimal.valueOf(requestDto.getTerm()));
        LoanOfferDto current3 = LoanOfferDto.builder()
                .isSalaryClient(false)
                .isInsuranceEnabled(true)
                .term(requestDto.getTerm())
                .requestedAmount(requestDto.getAmount())
                .rate(preRate)
                .term(requestDto.getTerm())
                .totalAmount(preTotalAmount)
                .monthlyPayment(preMonthlyPayment)
                .statementId(UUID.randomUUID())
                .build();
        offersDto.add(current3);

        log.info("Расчет 4го предложения...");
        preRate = baseRate.subtract(BigDecimal.valueOf(3));   // уменьшаем ставку на 3% клиенту со страховкой и c зп
        preMonthlyPayment = this.calcMonthlyPayment(requestDto.getAmount(), preRate, requestDto.getTerm());
        preTotalAmount = preMonthlyPayment.multiply(BigDecimal.valueOf(requestDto.getTerm()));
        LoanOfferDto current4 = LoanOfferDto.builder()
                .isSalaryClient(true)
                .isInsuranceEnabled(true)
                .term(requestDto.getTerm())
                .requestedAmount(requestDto.getAmount())
                .rate(preRate)
                .term(requestDto.getTerm())
                .totalAmount(preTotalAmount)
                .monthlyPayment(preMonthlyPayment)
                .statementId(UUID.randomUUID())
                .build();
        offersDto.add(current4);

        log.info("Список предложений для клиента сформирован...");
        return offersDto.stream().sorted(Comparator.comparing(LoanOfferDto::getRate).reversed()).collect(Collectors.toList());
    }

    /*расчет ежемесячного платежа от суммы кредита, ставки и срока*/
    public BigDecimal calcMonthlyPayment(BigDecimal amount,
                                         BigDecimal rate,
                                         Integer term) {
        log.info("Расчет ежемесячного платежа...");
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL64); // Преобразование годовой ставки в месячную
        log.info("Расчет ежемесячного платежа закончен!");

        return monthlyRate.add(BigDecimal.ONE)              // monthlyrate + 1
                .pow(term)                      // ^12
                .multiply(monthlyRate)                  // *monthlyrate
                .divide(monthlyRate.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE), MathContext.DECIMAL64)
                .multiply(amount);                  // * amount
            }

    public CreditDto calcCredit(ScoringDataDto scoringDataDto) throws ScoreException {
        log.info("Расчет финального предложения");
        CreditDto creditDto = new CreditDto();
        try {
            BigDecimal rate = scoringCheck(scoringDataDto);
            creditDto.setAmount(scoringDataDto.getAmount());
            creditDto.setTerm(scoringDataDto.getTerm());
            creditDto.setRate(rate);
            creditDto.setMonthlyPayment(calcMonthlyPayment(scoringDataDto.getAmount(), rate, scoringDataDto.getTerm()));
            creditDto.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());
            creditDto.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
            creditDto.setPaymentSchedule(calcPaymentSchedule(scoringDataDto.getAmount(), scoringDataDto.getTerm(),
                    rate, creditDto.getMonthlyPayment()));
            creditDto.setPsk(calcPsk(scoringDataDto.getAmount(), scoringDataDto.getTerm(), creditDto.getPaymentSchedule()));

            System.out.println(creditDto);
        } catch (ScoreException e)
        {
           // log.info("Скоринг не рассчитан. Расчет кредита невозможен!");
             //   throw new ScoreException("The scoring was disrupted. Credit's calculation is impossible!");
            throw e;
        }
        return creditDto;
    }

    BigDecimal calcPsk(BigDecimal amount, Integer term,
                       List<PaymentScheduleElementDto> paymentSchedule){
        log.info("Расчет ПСК...");

        /*Суммируем все платежи из графика*/
        BigDecimal totalPay = BigDecimal.ZERO;
        for (int i = 0; i < paymentSchedule.size(); i++) {
            totalPay = totalPay.add(paymentSchedule.get(i).getTotalPayment());
        }
        log.info("Сумма всех платежей = " + totalPay);

        /*месяцы в годы*/
        BigDecimal termY = BigDecimal.valueOf(term).divide(BigDecimal.valueOf(12), 2, RoundingMode.CEILING);

        /*ПСК*/
        BigDecimal psk = totalPay.divide(amount, 2, RoundingMode.CEILING)
                .subtract(BigDecimal.ONE).divide(termY, 3, RoundingMode.CEILING)
                .multiply(BigDecimal.valueOf(100));

        log.info("Расчет ПСК завершен!");
        return psk;
    }

    public List<PaymentScheduleElementDto> calcPaymentSchedule(BigDecimal amount,
                                                               Integer term,
                                                               BigDecimal rate,
                                                               BigDecimal monthlyPayment)
{
    log.info("Расчет графика платежей...");

    BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL64); // Преобразование годовой ставки в месячную
    List<PaymentScheduleElementDto> result = new ArrayList<>();
    PaymentScheduleElementDto pScheduleElement = new PaymentScheduleElementDto();
    LocalDate payDate = LocalDate.now();
    BigDecimal curAmount = amount; // для расчета остатка надо запоминать текущие суммы

    for(int i =0; i < term; i++){
        pScheduleElement = new PaymentScheduleElementDto();
        pScheduleElement.setNumber(i+1);
        pScheduleElement.setDate(payDate.plusMonths(i+1));
        pScheduleElement.setTotalPayment(monthlyPayment.setScale(2, RoundingMode.HALF_UP));
        pScheduleElement.setInterestPayment(curAmount.multiply(monthlyRate).setScale(2,RoundingMode.HALF_UP));
        pScheduleElement.setDebtPayment(monthlyPayment.subtract(pScheduleElement.getInterestPayment()).setScale(2,RoundingMode.HALF_UP));
        pScheduleElement.setRemainingDebt(curAmount.negate().add(pScheduleElement.getDebtPayment().setScale(2, RoundingMode.HALF_UP)));

        /*уменьшаем основной долг на сумму уплаченного по графику*/
        curAmount = curAmount.subtract(pScheduleElement.getDebtPayment());

        result.add(pScheduleElement);
        }

    // Проверяем остаток долга
    if (curAmount.compareTo(BigDecimal.ZERO) != 0) {
        log.info("График рассчитан некорректно. Остаток долга: "  +
                curAmount.setScale(2, RoundingMode.HALF_UP));

        // Добавляем остаток к последнему элементу графика
        PaymentScheduleElementDto lastElement = result.get(result.size() - 1);
        lastElement.setTotalPayment(lastElement.getTotalPayment().subtract(curAmount).setScale(2, RoundingMode.HALF_UP));
        lastElement.setRemainingDebt(lastElement.getRemainingDebt().add(curAmount).setScale(2, RoundingMode.HALF_UP));
    }
    return result;
    }

    /*Процесс прескоринга*/
    public void preScoringCheck(LoanStatementRequestDto request) throws ScoreException {
        log.info("Прескоринг...", request.toString());
        PreScoring preScoring = new PreScoring();
        try {
            preScoring.validate(request);
            log.info("Прескоринг успешен!");
        } catch (ScoreException e) {
            log.info("Ошибка прескоринга: " + e.getMessage());
            throw e;
        }
    }

    public BigDecimal scoringCheck(ScoringDataDto scoringDataDto) throws ScoreException {
        log.info("Скоринг...");
        try {
            BigDecimal preRate = baseRate;
            Scoring scoring = new Scoring();

            /*Некорректный ИНН*/
            if (!scoringDataDto.getEmployment().getEmployerINN().matches(inn_REGEX)) {
                throw new ScoreException("INN must contain from 9 to 12 digits. ");
            }

            /*Сумма займа больше, чем 24 зарплаты --> Отказ*/
            if (scoringDataDto.getEmployment().getSalary().multiply(BigDecimal.valueOf(24)).compareTo(scoringDataDto.getAmount()) != 1) {
                log.info("Сумма займа больше 24 зарплат. Отказано.");
                throw new ScoreException("Credit's sum is more than 24 salaries. Denied.");
            }
            /*Общий стаж менее 18 месяцев → Отказ*/
            if (scoringDataDto.getEmployment().getWorkExperienceTotal() < 18) {
                log.info("Общий стаж меньше требуемого. Отказано.");
                throw new ScoreException("Total Experience less then 18 months. Denied.");
            }
            /*Текущий стаж менее 3 месяцев → Отказ*/
            if (scoringDataDto.getEmployment().getWorkExperienceCurrent() < 3) {
                log.info("Стаж на текущем месте работы меньше требуемого. Отказано.");
                throw new ScoreException("Current Experience less then 3 months. Denied.");
            }
            /*Возраст менее 20 или более 65 лет → отказ*/
            int age = Period.between(scoringDataDto.getBirthdate(), LocalDate.now()).getYears();
            if (age < 20 || age > 65) {
                log.info("Заявитель не соответствует возрастным рамкам. Отказано.");
                throw new ScoreException("The applicant does not meet the age requirements. Denied.");
            }
            /*Некорректный ИНН работодателя*/
            try {
                scoring.validateInn(scoringDataDto.getEmployment().getEmployerINN());
            } catch(ScoreException e){
                log.info("Ошибка скоринга: " + e.getMessage());
                throw e;
            }

            /*Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 2; Владелец бизнеса → ставка увеличивается на 1*/
            preRate = preRate.add(Scoring.getEmploymentRate(scoringDataDto.getEmployment().getEmploymentStatus()));

            /*Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на */
            preRate = preRate.add(Scoring.getPositionRate(scoringDataDto.getEmployment().getPosition()));

            /*Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1*/
            preRate = preRate.add(Scoring.getMaritalStatusRate(scoringDataDto.getMaritalStatus()));

            /*Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3;*/
            /*Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3; Не бинарный → ставка увеличивается на 7*/
            preRate = preRate.add(Scoring.getGenderAndAgeRate(scoringDataDto.getGender(), age));
            log.info("Скоринг завершен!");
            return preRate;
        }
        catch(ScoreException e){
                log.info("The scoring was disrupted.");
                log.info(e.getMessage());
                throw e;
        }
        }
    }

