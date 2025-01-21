package ru.berezentseva.dossier;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Микросервис Досье Клиента",
                description = "Обработка сообщений из Кафки от МС-deal на каждом шаге, " +
                        "который требует отправки письма на почту Клиенту ",
                version = "v1"
        )
)
@SpringBootApplication
public class DossierApplication {

    public static void main(String[] args) {
        SpringApplication.run(DossierApplication.class, args);
    }

}
