package ru.home.appscheckbot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.appscheckbot.TelegramBot;
import ru.home.appscheckbot.appConfig.ApplicationContextProvider;

@Service
public class SendMessageService {

    public void SendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setParseMode("html")
                .setChatId(chatId)
                .setText(text);
        TelegramBot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void SendMessageWithKeyboard(String chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setParseMode("html")
                .setChatId(chatId)
                .setText(text);
        if (keyboard != null) {
            sendMessage.setReplyMarkup(keyboard);
        }
        TelegramBot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
