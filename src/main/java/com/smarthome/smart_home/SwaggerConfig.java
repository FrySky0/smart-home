package com.smarthome.smart_home;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Home API")
                        .version("1.0")
                        .description("<h3>🏠 Smart Home Management System</h3>" +
                                "<p>REST API для управления умным домом с поддержкой:</p>" +
                                "<ul>" +
                                "<li><b>Управление устройствами</b> (освещение, термостаты, кондиционеры, ТВ)</li>" +
                                "<li><b>Мониторинг сенсоров</b> (температура, движение, освещенность, влажность)</li>" +
                                "<li><b>Автоматизация</b> (правила с триггерами по времени/значениям сенсоров)</li>" +
                                "<li><b>Управление комнатами</b> и этажами</li>" +
                                "<li><b>Импорт/экспорт</b> конфигурации в JSON</li>" +
                                "<li><b>Аутентификация</b> JWT с ролями пользователь/администратор</li>" +
                                "</ul>" +
                                "<p>Технологии: Spring Boot 3, PostgreSQL, JWT, Swagger/OpenAPI</p>"));
    }
}
