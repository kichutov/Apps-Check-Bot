package ru.home.appscheckbot.appConfig;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ru.home.appscheckbot.TelegramBot;
import ru.home.appscheckbot.cache.UsersBotStatesCache;
import ru.home.appscheckbot.services.messageServices.KeyboardService;
import ru.home.appscheckbot.services.messageServices.LocaleMessageService;
import ru.home.appscheckbot.services.messageServices.ReplyMessagesService;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {

    private String botPath;
    private String botUsername;
    private String botToken;

    private DefaultBotOptions.ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;

    @Bean
    public TelegramBot telegramBot(LocaleMessageService localeMessageService,
                                   ReplyMessagesService replyMessagesService,
                                   UsersBotStatesCache usersBotStatesCache,
                                   KeyboardService keyboardService) {
        TelegramBot telegramBot = new TelegramBot(localeMessageService, replyMessagesService, usersBotStatesCache, keyboardService);
        telegramBot.setBotUsername(botUsername);
        telegramBot.setBotToken(botToken);
        telegramBot.setBotPath(botPath);

        return telegramBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
