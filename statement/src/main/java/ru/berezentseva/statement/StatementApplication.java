package ru.berezentseva.statement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Микросервис Заявка",
				description = "Расчет прескоринга и выбор предложения на основании заявки потенциального клиента",
				version = "v1"
		)
)

@SpringBootApplication
public class StatementApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatementApplication.class, args);
	}

}
