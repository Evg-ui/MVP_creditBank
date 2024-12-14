package ru.berezentseva.calculator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Микросервис Кредитный калькулятор",
                description = "Прескоринг и скоринг кредита",
                version = "v1"
        )
)
public class CalculatorApplication {

    public static void main(String[] args) {
       SpringApplication.run(CalculatorApplication.class, args);
    }
}




