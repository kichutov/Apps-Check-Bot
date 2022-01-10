package ru.home.appscheckbot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TextService {
    private final Locale locale;
    private final MessageSource messageSource;

    public TextService(@Value("ru-RU") String localeTag, MessageSource messageSource) {
        this.locale = Locale.forLanguageTag(localeTag);
        this.messageSource = messageSource;
    }

    public String getText(String message) {
        return messageSource.getMessage(message, null, locale);
    }

    public String getText(String message, Object... args) {
        return messageSource.getMessage(message, args, locale);
    }
}
