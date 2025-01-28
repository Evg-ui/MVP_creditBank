package ru.berezentseva.gateway;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;
import ru.berezentseva.deal.exception.StatementException;
import ru.berezentseva.deal.model.Statement;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class GatewayService {

    private final RestTemplate restTemplate;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, LoanStatementRequestDto request) throws ScoreException{
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
                return ResponseEntity.ok(responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            log.error("Ошибка 4xx клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
          //  throw e;
            throw new ScoreException("Ошибка клиента: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            log.error("Ошибка 5xx сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
          //  throw e;
            throw new ScoreException("Ошибка сервера: " + e.getMessage());
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
          //  throw e;
           throw new ScoreException("Общая ошибка при вызове API: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестная ошибка");
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, LoanOfferDto request) throws StatementException {
        ResponseEntity<?> responseEntity;
        try {
            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(request, new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
                return ResponseEntity.ok(responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента
            log.error("Ошибка клиента при вызове API {}: {}", url, e.getMessage());
            throw new StatementException(e.getMessage());
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера
            log.error("Ошибка сервера при вызове API {}: {}", url, e.getMessage());
            throw new StatementException(e.getMessage());
        } catch (RestClientException e) {
            // Общая обработка ошибок Rest клиента
            log.error("Общая ошибка при вызове API {}: {}", url, e.getMessage());
            throw new StatementException(e.getMessage());
        }
        // Если не удалось получить успешный ответ, возвращаем статус и тело ответа
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());

    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, FinishRegistrationRequestDto request) throws ScoreException {
        ResponseEntity<?> responseEntity;
        try {
        responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok(responseEntity.getBody());
        }
        }   catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            log.error("Ошибка 4xx клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            //  throw e;
            throw new ScoreException(e.getMessage());
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            log.error("Ошибка 5xx сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            //  throw e;
            throw new ScoreException(e.getMessage());
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            //  throw e;
            throw new ScoreException(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестная ошибка");
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, UUID statementId ) throws StatementException{
        ResponseEntity<?> responseEntity;
        try{
        responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(statementId, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok("Документы успешно отправлены!");
        }
        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            log.error("Ошибка 4xx клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
           //   throw e;
            throw new StatementException(e.getMessage());
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            log.error("Ошибка 5xx сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            //   throw e;
            throw new StatementException(e.getMessage());
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            //   throw e;
            throw new StatementException(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестная ошибка");
    }

    public @NotNull ResponseEntity<?> getAdminResponseEntity(String url, UUID statementId ) throws StatementException{
        ResponseEntity<?> responseEntity;
        try{
            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(statementId, new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
                return ResponseEntity.ok(responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            log.error("Ошибка 4xx клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            //   throw e;
            throw new StatementException(e.getMessage());
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            log.error("Ошибка 5xx сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            //   throw e;
            throw new StatementException(e.getMessage());
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            //   throw e;
            throw new StatementException(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестная ошибка");
    }

    public @NotNull ResponseEntity<?> getAdminResponseEntity(String url) throws StatementException{
        ResponseEntity<List<Statement>> responseEntity;
        try{
            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
                return ResponseEntity.ok(responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента (4xx)
            log.error("Ошибка 4xx клиента при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            //   throw e;
            throw new StatementException(e.getMessage());
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера (5xx)
            log.error("Ошибка 5xx сервера при вызове API: {}, статус: {}", e.getMessage(), e.getStatusCode());
            //   throw e;
            throw new StatementException(e.getMessage());
        } catch (RestClientException e) {
            //Обработка общих ошибок Rest клиента
            log.error("Ошибка при вызове API: ", e);
            //   throw e;
            throw new StatementException(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неизвестная ошибка");
    }
}
