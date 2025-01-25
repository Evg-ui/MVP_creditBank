package ru.berezentseva.gateway;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.berezentseva.calculator.CalculatorService;
import ru.berezentseva.calculator.DTO.LoanOfferDto;
import ru.berezentseva.calculator.DTO.LoanStatementRequestDto;
import ru.berezentseva.calculator.exception.ScoreException;
import ru.berezentseva.deal.DTO.FinishRegistrationRequestDto;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class GatewayService {

    private final RestTemplate restTemplate;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, LoanStatementRequestDto request) {
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            log.info("Ошибка вызова от {}: {}", url, ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, LoanOfferDto request) {
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            log.info("Ошибка вызова от {}: {}", url, ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, FinishRegistrationRequestDto request, UUID statementId ) {

        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(request, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            log.info("Ошибка вызова от {}: {}", url, ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }

    public @NotNull ResponseEntity<?> getResponseEntity(String url, UUID statementId ) {

        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(statementId, new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Ответ от {}: {}", url, ResponseEntity.ok(responseEntity.getBody()));
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            log.info("Ошибка вызова от {}: {}", url, ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }
}
