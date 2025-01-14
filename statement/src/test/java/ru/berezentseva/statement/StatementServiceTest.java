package ru.berezentseva.statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.statement.exception.StatementException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {
    @InjectMocks
    private StatementService statementService;

    @Mock
    private RestTemplate restTemplate;

    @Test
        // проверка возврата 4 предложений
    void testReturnOffersAfterPrescoringReturnsFourElementsSuccess() throws ScoreException {
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

        LoanOfferDto[] mockOffers = {new LoanOfferDto(), new LoanOfferDto(), new LoanOfferDto(), new LoanOfferDto()};

        // Мокирование RestTemplate
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(LoanOfferDto[].class)))
                .thenReturn(new ResponseEntity<>(mockOffers, HttpStatus.OK));
        // Выполнение метода
        List<LoanOfferDto> offers = statementService.returnOffersAfterPrescoring(request);
        // Проверка количества возвращенных предложений
        assertEquals(4, offers.size(), "Должно быть 4 оффера");
    }

    @Test
    void selectOfferFromDealReturnsSuccess() throws StatementException {
        LoanOfferDto  loanOfferDto = new LoanOfferDto();
        LoanOfferDto[] mockResponse = new LoanOfferDto[]{new LoanOfferDto()}; // ответ мока

        // Настройка поведения мок-объекта
        ResponseEntity<LoanOfferDto[]> responseEntity = ResponseEntity.ok(mockResponse);
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)))
                .thenReturn(responseEntity);

        // Вызов тестируемого метода
        statementService.selectOfferFromDeal(loanOfferDto);

        assertEquals("200 OK", responseEntity.getStatusCode().toString());
    }

    @Test
    public void testSelectOfferFromDealClientErrorSuccess() {
        LoanOfferDto offerDto = new LoanOfferDto();

        // Настройка поведения мок-объекта для генерации ошибки клиента
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Проверка, что метод выбрасывает исключение HttpClientErrorException
        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            statementService.selectOfferFromDeal(offerDto);
        });

        // Проверка статуса исключения
        assertEquals(HttpStatus.BAD_REQUEST, ((HttpClientErrorException) exception).getStatusCode());
    }

    @Test
    public void testSelectOfferFromDealServerErrorSuccess() {
        LoanOfferDto offerDto = new LoanOfferDto();

        // Настройка поведения мок-объекта для генерации ошибки сервера
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Проверка, что метод выбрасывает исключение HttpServerErrorException
        Exception exception = assertThrows(HttpServerErrorException.class, () -> {
            statementService.selectOfferFromDeal(offerDto);
        });

        // Проверка статуса исключения
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((HttpServerErrorException) exception).getStatusCode());
    }

    @Test
    public void testSelectOfferFromDealNotSuccessResponse() {
        LoanOfferDto offerDto = new LoanOfferDto();

        // Настройка поведения мок-объекта для генерации неуспешного ответа
        ResponseEntity<LoanOfferDto[]> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)))
                .thenReturn(responseEntity);

        // Проверка, что метод выбрасывает RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> {
            statementService.selectOfferFromDeal(offerDto);
        });

        // Проверка сообщения исключения
        assertEquals("Ошибка при вызове API deal/offer/select: 500 INTERNAL_SERVER_ERROR, тело ответа: null", exception.getMessage());
    }

    }

