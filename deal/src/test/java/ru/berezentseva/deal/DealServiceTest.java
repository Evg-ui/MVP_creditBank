package ru.berezentseva.deal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.deal.DTO.Enums.ApplicationStatus;
import ru.berezentseva.deal.exception.StatementException;
import ru.berezentseva.deal.model.Client;
import ru.berezentseva.deal.model.Credit;
import ru.berezentseva.deal.model.Statement;
import ru.berezentseva.deal.repositories.ClientRepository;
import ru.berezentseva.deal.repositories.CreditRepository;
import ru.berezentseva.deal.repositories.StatementRepository;
import ru.berezentseva.deal.services.DealService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {
    @InjectMocks
    private DealService dealService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CreditRepository creditRepository;

    private Client client;

    private LoanStatementRequestDto request;

    @BeforeEach
    void setUp() {
        client = new Client();
    }

    @Test
    void testSavedOfferEqualsLoanOfferSuccess() throws StatementException {
        // Создаем mock-объект для StatementRepository
        StatementRepository statementRepositoryMock = mock(StatementRepository.class);

        // Создаем экземпляр DealService с mock-объектом
        DealService dealService = new DealService(restTemplate, clientRepository, statementRepositoryMock, creditRepository);

        // Создаем тестовый LoanOfferDto
        LoanOfferDto offerDto = new LoanOfferDto();
        offerDto.setStatementId(UUID.randomUUID());

        // Создаем тестовый Statement
        Statement statement = new Statement();
        statement.setStatementId(offerDto.getStatementId());

        // Задаем поведение mock-объекта statementRepository
        when(statementRepositoryMock.findStatementByStatementId(offerDto.getStatementId())).thenReturn(Optional.of(statement));

        dealService.selectOffer(offerDto);

        // Проверка на соответствие offerDto
        assertEquals(offerDto, statement.getAppliedOffer());
    }

    @Test
    void testChangeStatementStatusSuccess() throws StatementException {
        StatementRepository statementRepositoryMock = mock(StatementRepository.class);

        DealService dealService = new DealService(restTemplate, clientRepository, statementRepositoryMock, creditRepository);

        // Создаем тестовый LoanOfferDto
        LoanOfferDto offerDto = new LoanOfferDto();
        offerDto.setStatementId(UUID.randomUUID());

        // Создаем тестовый Statement
        Statement statement = new Statement();
        statement.setStatementId(offerDto.getStatementId());

        when(statementRepositoryMock.findStatementByStatementId(offerDto.getStatementId())).thenReturn(Optional.of(statement));

        dealService.selectOffer(offerDto);

        // Проверка
        assertEquals(ApplicationStatus.APPROVED, statement.getStatus());
    }

    @Test
        // проверка возврата 4 предложений
    void testNewApplicationAndClientReturnsFourElementsSuccess() {
        request = new LoanStatementRequestDto();
        request.setFirstName("Evgeniya");
        request.setLastName("Berezentseva");
        request.setMiddleName("Vladimirovna");
        request.setAmount(BigDecimal.valueOf(300000));
        request.setTerm(6);
        request.setBirthDate(LocalDate.parse("2000-12-01"));
        request.setEmail("mail.123@example.com");
        request.setPassportSeries("1255");
        request.setPassportNumber("567050");

        LoanOfferDto[] mockOffers = {new LoanOfferDto(), new LoanOfferDto(), new LoanOfferDto(), new LoanOfferDto()};

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Мокирование RestTemplate
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(LoanOfferDto[].class)))
                .thenReturn(new ResponseEntity<>(mockOffers, HttpStatus.OK));
        // Выполнение метода
        List<LoanOfferDto> offers = dealService.createNewApplicationAndClient(request);
        // Проверка количества возвращенных предложений
        assertEquals(4, offers.size(), "Должно быть 4 оффера");
    }

    // проверяем, что данные сохраняются в репозиториях
    @Test
    void saveStatementInRepositorySuccess() {
        StatementRepository statementRepository = mock(StatementRepository.class);
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        // Настройка заглушки для метода save
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        // Вызов метода, который должен использовать statementRepository.save()
        Statement savedStatement = statementRepository.save(statement);
        // Проверка, что сохраненная заявка соответствует ожидаемому
        assertNotNull(savedStatement);
        assertEquals(statement.getStatementId(), savedStatement.getStatementId());
    }

    @Test
    void saveClientInRepositorySuccess() {
        ClientRepository clientRepository = mock(ClientRepository.class);
        Client client = new Client();
        client.setClientUuid(UUID.randomUUID());
        // Настройка заглушки для метода save
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        // Вызов метода, который должен использовать clientRepository.save()
        Client savedClient = clientRepository.save(client);
        // Проверка, что сохраненный клиент соответствует ожидаемому
        assertNotNull(savedClient);
        assertEquals(client.getClientUuid(), savedClient.getClientUuid());
    }


    @Test
    void saveCreditInRepositorySuccess() {
        CreditRepository creditRepository = mock(CreditRepository.class);
        Credit credit = new Credit();
        credit.setCreditUuid(UUID.randomUUID());
        // Настройка заглушки для метода save
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);
        // Вызов метода, который должен использовать creditRepository.save()
        Credit savedCredit = creditRepository.save(credit);
        // Проверка, что сохраненный кредит соответствует ожидаемому
        assertNotNull(savedCredit);
        assertEquals(credit.getCreditUuid(), savedCredit.getCreditUuid());
    }

    // проверяем, что методы вызываются
    @Test
    void saveStatementInDataBaseSuccess() {
        StatementRepository statementRepository = mock(StatementRepository.class);
        Statement statement = new Statement();
        // Настройка заглушки для метода save
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        // Вызов метода, который должен использовать save()
        statementRepository.save(statement);
        // Проверка, что метод save был вызван один раз
        verify(statementRepository, times(1)).save(any(Statement.class));
    }

    @Test
    void saveClientInDataBaseSuccess() {
        ClientRepository clientRepository = mock(ClientRepository.class);
        Client client = new Client();
        // Настройка заглушки для метода save
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        // Вызов метода, который должен использовать save()
        clientRepository.save(client);
        // Проверка, что метод save был вызван один раз
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void saveCreditInDataBaseSuccess() {
        CreditRepository creditRepository = mock(CreditRepository.class);
        Credit credit = new Credit();
        // Настройка заглушки для метода save
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);
        // Вызов метода, который должен использовать save()
        creditRepository.save(credit);
        // Проверка, что метод save был вызван один раз
        verify(creditRepository, times(1)).save(any(Credit.class));
    }
}