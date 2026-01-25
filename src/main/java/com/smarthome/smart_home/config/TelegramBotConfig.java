package com.smarthome.smart_home.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.smarthome.smart_home.service.TelegramService;

@Configuration
public class TelegramBotConfig {
    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApplication(TelegramService telegramService, 
                                                                     @Value("${telegram.bot.token}") String botToken) throws TelegramApiException {
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        botsApplication.registerBot(botToken, telegramService);
        return botsApplication;
    }
}