package com.smarthome.smart_home.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TelegramService extends TelegramLongPollingBot{
    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    @Override
    public String getBotToken() {
        return botToken;
    }
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    @Override
    public void onUpdateReceived(Update update) {
        // Здесь можно обрабатывать команды из чата (например, /status)
        // Но пока оставим пустым, так как нам нужны только логи "из системы в ТГ"
    }

    public void sendLog(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("🔔 *Smart Home Log:*\n" + message);
        sendMessage.setParseMode("Markdown");

        try {
            execute(sendMessage);
            log.info("Log sent to Telegram");
        } catch (TelegramApiException e) {
            log.error("Failed to send telegram log: {}", e.getMessage());
        }
    }
}
