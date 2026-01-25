package com.smarthome.smart_home.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class TelegramService implements LongPollingSingleThreadUpdateConsumer {

    private final String adminChatId;
    private final TelegramClient telegramClient;
    private final ReportService reportService;

    public TelegramService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.chat-id}") String adminChatId,
            @Lazy ReportService reportService) {
        
        this.adminChatId = adminChatId;
        this.reportService = reportService;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            if (!chatId.equals(adminChatId)){
                log.warn("Unauthorized access attempt from chat id: {}",chatId);
                return;
            }
            if (messageText.equals("/start")){
                try {
                    telegramClient.execute(new SendMessage(adminChatId,"Привет! Можешь написать мне /report и я отправлю тебе отчёт умного дома."));
                } catch (TelegramApiException e) {
                    log.error("Failed to send telegram message: {}", e.getMessage());
                }
            } else if (messageText.equals("/report")){
                sendPdfReport(adminChatId);
            }
        }
    }


    private void sendPdfReport(String chatId) {
        try {
            byte[] pdfBytes = reportService.generateActivityReportPDF();
            InputStream inputStream = new ByteArrayInputStream(pdfBytes);
            InputFile inputFile = new InputFile(inputStream, "SmartHome_Report.pdf");

            SendDocument sendDocument = SendDocument.builder()
                    .chatId(chatId)
                    .document(inputFile)
                    .caption("*Отчет готов!*")
                    .parseMode("Markdown")
                    .build();

            telegramClient.execute(sendDocument);
            log.info("PDF report sent in Telegram");
        } catch (TelegramApiException e) {
            log.error("Failed to send PDF: {}", e.getMessage());
            sendSimpleMessage(chatId, "Ошибка при генерации PDF.");
        }
    }

    private void sendSimpleMessage(String chatID, String msg){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatID)
                .text(msg)
                .parseMode("Markdown")
                .build();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to send telegram message: {}", e.getMessage());
        }
    }

    public void sendLog(String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(adminChatId)
                .text("*Smart Home Log:*\n" + message)
                .parseMode("Markdown")
                .build();
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to send telegram message: {}", e.getMessage());
        }
    }
}
