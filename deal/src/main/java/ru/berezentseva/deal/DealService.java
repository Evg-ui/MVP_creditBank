package ru.berezentseva.deal;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.*;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.DTO.Enums.ChangeType;
import ru.berezentseva.deal.DTO.Enums.CreditStatus;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.DTO.StatementStatusHistoryDto;
import ru.berezentseva.deal.model.*;
import ru.berezentseva.deal.repositories.*;
import ru.berezentseva.calculator.DTO.LoanOfferDto;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Slf4j
public class DealService {


    private final RestTemplate restTemplate;

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final PassportRepository passportRepository;

    //Constructor
    @Autowired
    public DealService(RestTemplate restTemplate,
                       ClientRepository clientRepository,
                       StatementRepository statementRepository,
                       CreditRepository creditRepository,
                       PassportRepository passportRepository) {
        this.restTemplate = restTemplate;
        this.clientRepository = clientRepository;
        this.statementRepository = statementRepository;
        this.creditRepository = creditRepository;
        this.passportRepository = passportRepository;
    }

    @Transactional
    void saveStatement(Statement statement) {statementRepository.save(statement); }

    @Transactional
    Client saveClient(Client client) {return clientRepository.save(client);}

    @Transactional
    void savePassport(Passport passport) {
        passportRepository.save(passport);
    }

    @Transactional
    Credit saveCredit(Credit credit) {
        return creditRepository.save(credit);
    }

    public List<LoanOfferDto> createNewApplicationAndClient(LoanStatementRequestDto request) {
        // try catch добавить
        log.info("Received request into createApp: {}", request);
        
        log.info("Заполняем данные  паспорта:");
        Passport passport = createPassport(request);
        log.info("Создание нового клиента");
        Client client = createClient(request, passport);
        log.info("Создание новой заявки");
        Statement statement = createStatement(client);

//         Отправка запроса на /calculator/offers.
//          try catch надо
        log.info("Отправляем запрос в /calculator/offers");

        ResponseEntity<LoanOfferDto[]> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    "http://localhost:8080/calculator/offers",
                    HttpMethod.POST,
                    new HttpEntity<>(request, new HttpHeaders()),
                    LoanOfferDto[].class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                // Обработка ошибок HTTP
                String errorMessage = "Ошибка при вызове API: " + responseEntity.getStatusCode() +
                        ", тело ответа: " + responseEntity.getBody();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            Statement finalStatement = statement;
            List<LoanOfferDto> offers = Arrays.asList(responseEntity.getBody());
            offers.forEach(offer -> offer.setStatementId(finalStatement.getStatementId()));
            return offers;

        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            throw new RuntimeException("Ошибка при вызове API: " + e.getMessage(), e);
        }
    }

    public void selectOffer(LoanOfferDto offerDto) throws JsonProcessingException {
        //проверка существования заявки с таким ID
        Statement statement = statementRepository.findStatementByStatementId(offerDto.getStatementId()).orElseThrow(()
                -> new NoSuchElementException("Заявка с указанным ID не найдена: " + offerDto.getStatementId()));
        log.info("Запрос по заявке: {}", offerDto.getStatementId().toString());

        log.info("Устанавливаем значение json: {}", statement.getAppliedOffer());
        log.info("Обновляем данные заявки");
        statement.setStatus(ApplicationStatus.PREAPPROVAL);
        log.info("Новый статус заявки: {}", statement.getStatus());
        statement.setAppliedOffer(offerDto);
        log.info("Поле AppliedOffer сущности: {}", statement.getAppliedOffer());
       // saveStatement(statement);
        log.info("Выбранное предложение: {}", statement.getAppliedOffer());
        log.info("Данные заявки обновлены!");
      //  log.info("Выбранное предложение обновлено в базе данных.");
        log.info("Обновляем историю заявки");
        List<StatementStatusHistoryDto> status = new ArrayList<>();
        status = statement.getStatusHistory();
        if (status == null) {
            status = new ArrayList<>();
        }
        StatementStatusHistoryDto statusHistory = new StatementStatusHistoryDto();
        statusHistory.setStatus(statement.getStatus());
        statusHistory.setTime(new Timestamp(System.currentTimeMillis()).toLocalDateTime()); // Устанавливаем текущее время
        statusHistory.setChangeType(ChangeType.AUTOMATIC);
        log.info("Текущий статус заявки: {}", statusHistory);
        status.add(statusHistory);
        statement.setStatusHistory(status);
        log.info("История заявки: {}", statement.getStatusHistory().toString());
        saveStatement(statement);
        log.info("История заявки обновлена!");
        //status.add(statusHistory);
    }

    public void finishRegistration(UUID statementId, FinishRegistrationRequestDto request) throws IOException {
        Statement statement = statementRepository.findStatementByStatementId(statementId).orElseThrow(()
                -> new NoSuchElementException("Заявка с указанным ID не найдена: " + statementId));

        Client client = statementRepository.findStatementByClientUuid(statement.getClientUuid()).orElseThrow(()
                -> new NoSuchElementException("Клиент с указанным ID не найден: " + statement.getClientUuid())).getClientUuid();

        Employment employment = createEmployment(request);

        ScoringDataDto scoringDataDto = createScoringDataDto(request, statement, client);

        // насыщаем клиент
        log.info("Дополняем данные по клиенту...");
        client.setAccountNumber(request.getAccountNumber());
        client.setDependentAmount(request.getDependentAmount());
        client.setGender(request.getGender());
        client.setMaritalStatus(request.getMaritalStatus());
        client.setEmployment(employment);
        saveClient(client);
        log.info("Данные по клиенту обновлены!");

        log.info("Отправляем запрос в /calculator/calc");
        ResponseEntity<CreditDto> responseEntity;
        CreditDto creditDto;
        try {
            responseEntity = restTemplate.exchange(
                    "http://localhost:8080/calculator/calc",
                    HttpMethod.POST,
                    new HttpEntity<>(scoringDataDto, new HttpHeaders()),
                    CreditDto.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                // Обработка ошибок HTTP
                String errorMessage = "Ошибка при вызове API: " + responseEntity.getStatusCode() +
                        ", тело ответа: " + responseEntity.getBody();
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            creditDto = responseEntity.getBody();

            if (creditDto == null) {
                String errorMessage = "Ответ от API не содержит данных.";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            throw new RuntimeException("Ошибка при вызове API: " + e.getMessage(), e);
        }

        log.info("Полученный кредит из calc: {}", creditDto);
        Credit credit = createCredit(creditDto);
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setCreditUuid(credit);
        
        log.info("Обновляем историю заявки");
        List<StatementStatusHistoryDto> status = new ArrayList<>();
        status = statement.getStatusHistory();
        if (status == null) {status = new ArrayList<>();}        
        StatementStatusHistoryDto statusHistory = new StatementStatusHistoryDto();
        statusHistory.setStatus(statement.getStatus());
        statusHistory.setTime(new Timestamp(System.currentTimeMillis()).toLocalDateTime()); // Устанавливаем текущее время
        statusHistory.setChangeType(ChangeType.AUTOMATIC);
        log.info("Текущий статус заявки: {}", statusHistory);
        status.add(statusHistory);
        statement.setStatusHistory(status);
        log.info("История заявки: {}", statement.getStatusHistory().toString());
        saveStatement(statement);
        log.info("История заявки обновлена!");
    }

    private @NotNull ScoringDataDto createScoringDataDto(FinishRegistrationRequestDto request, Statement statement, Client client) {
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        log.info("Подготавливаем Скоринг...");
        scoringDataDto.setAmount(statement.getAppliedOffer().getRequestedAmount());
        scoringDataDto.setTerm(statement.getAppliedOffer().getTerm());
        scoringDataDto.setFirstName(client.getFirstName());
        scoringDataDto.setLastName(client.getLastName());
        scoringDataDto.setMiddleName(client.getMiddleName());
        scoringDataDto.setGender(request.getGender());
        scoringDataDto.setBirthdate(client.getBirthDate());
        log.info("Установленная дата рождения: {}", scoringDataDto.getBirthdate().toString());
        scoringDataDto.setPassportSeries(client.getPassport().getSeries());
        scoringDataDto.setPassportNumber(client.getPassport().getNumber());
        scoringDataDto.setPassportIssueDate(request.getPassportIssueDate());
        scoringDataDto.setPassportIssueBranch(request.getPassportIssueBrach());
        scoringDataDto.setMaritalStatus(request.getMaritalStatus());
        scoringDataDto.setDependentAmount(request.getDependentAmount());
        scoringDataDto.setEmployment(request.getEmployment());
        log.info("Установленный  работодатель: {}", scoringDataDto.getEmployment().toString());
        scoringDataDto.setAccountNumber(request.getAccountNumber());
        scoringDataDto.setIsSalaryClient(statement.getAppliedOffer().getIsSalaryClient());
        scoringDataDto.setIsInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnabled());
        log.info("Данные для скоринга готовы!");
        return scoringDataDto;
    }

    @NotNull
    private Employment createEmployment(FinishRegistrationRequestDto request) {
        Employment employment = new Employment();
        log.info("UUID работодателя {}", employment.getEmploymentUuid());
        employment.setStatus(request.getEmployment().getEmploymentStatus());
        log.info("Статус работника {}", employment.getStatus());
        employment.setEmploymentInn(request.getEmployment().getEmployerINN());
        log.info("ИНН работодателя {}", employment.getEmploymentInn());
        employment.setSalary(request.getEmployment().getSalary());
        log.info("Зарплата работника {}", employment.getSalary());
        employment.setPosition(request.getEmployment().getPosition());
        log.info("Позиция работника {}", employment.getPosition());
        employment.setWorkExperienceTotal(request.getEmployment().getWorkExperienceTotal());
        log.info("Общий стаж {}", employment.getWorkExperienceTotal());
        employment.setWorkExperienceCurrent(request.getEmployment().getWorkExperienceCurrent());
        log.info("Текущий стаж {}", employment.getWorkExperienceCurrent());
        return employment;
    }

    @NotNull
    private Statement createStatement(Client client) {
        Statement statement = new Statement();
        statement.setClientUuid(client);
        log.info("ClientId {}", statement.getClientUuid().toString());
        statement.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        log.info("Creation_date {}", statement.getCreationDate().toString());
        log.info("Сохраняем заявку");
        saveStatement(statement);
        log.info("Заявка создана");
        log.info("UUID заявки {}", statement.getStatementId().toString());
        return statement;
    }

    @NotNull
    private Client createClient(LoanStatementRequestDto request, Passport passport) {
        Client client = new Client();
        log.info("UUID клиента {}", client.getClientUuid());
        client.setLastName(request.getLastName());
        log.info("LastName {}", client.getLastName());
        client.setFirstName(request.getFirstName());
        log.info("FirstName {}", client.getFirstName());
        client.setMiddleName(request.getMiddleName());
        log.info("MiddleName {}", client.getMiddleName());
        client.setBirthDate(request.getBirthDate());
        log.info("BirthDate {}", client.getBirthDate().toString());
        client.setEmail(request.getEmail());
        log.info("Email {}", client.getEmail());
        client.setPassport(passport);
        log.info("Passport UUID {}", client.getPassport());
        log.info("Сохраняем клиента");
        saveClient(client);
        log.info("Клиент создан!");
        log.info("UUID клиента {}", client.getClientUuid().toString());
        return client;
    }

    @NotNull
    private Passport createPassport(LoanStatementRequestDto request) {
        Passport passport = new Passport();
        log.info("UUID паспорта {}", passport.getPassportUuid());
        passport.setSeries(request.getPassportSeries());
        log.info("Серия {}", passport.getSeries());
        passport.setNumber(request.getPassportNumber());
        log.info("Номер {}", passport.getNumber());
        log.info("Паспорт клиента сохранен!");
        return passport;
    }
    
    @NotNull
    private Credit createCredit(CreditDto creditDto) {
        Credit credit = new Credit();
        log.info("UUID кредита {}", credit.getCreditUuid());
        credit.setAmount(creditDto.getAmount());
        credit.setTerm(creditDto.getTerm());
        credit.setMonthlyPayment(creditDto.getMonthlyPayment());
        credit.setRate(creditDto.getRate());
        credit.setPsk(creditDto.getPsk());
        credit.setPayment_schedule(creditDto.getPaymentSchedule());
        credit.setSalaryClient(creditDto.getIsSalaryClient());
        credit.setInsuranceEnabled(creditDto.getIsInsuranceEnabled());
        credit.setCreditStatus(CreditStatus.CALCULATED);
        saveCredit(credit);
        log.info("Кредит создан!");
        log.info("UUID созданного кредита {}", credit.getCreditUuid().toString());
        return credit;
    }
}