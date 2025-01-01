//package ru.berezentseva.statement;
//
//import liquibase.integration.spring.SpringLiquibase;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//public class LiquibaseUnitTest {
//    @Mock
//    private SpringLiquibase springLiquibase;
//
//    @InjectMocks
//    private TestConfig testConfig; // Класс конфигурации для тестирования
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testLiquibaseInitialization() throws Exception {
//        // Инициализируем конфигурацию, которая вызывает SpringLiquibase
//        testConfig.liquibase(); // Вызываем метод, который создает SpringLiquibase
//
//        // Проверяем, что метод afterPropertiesSet() был вызван
//        verify(springLiquibase, times(1)).afterPropertiesSet();
//    }
//
//    @Configuration
//    static class TestConfig {
//        @Bean
//        public SpringLiquibase liquibase(LiquibaseProperties liquibaseProperties, ApplicationContext applicationContext) {
//            SpringLiquibase liquibase = new SpringLiquibase();
//            liquibase.setDataSource(applicationContext.getBean(DataSource.class)); // Или другой способ получения DataSource
//            liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.yaml"); // Укажите путь к вашему changelog
//            liquibase.setShouldRun(true);
//            return liquibase;
//        }
//    }
//}
