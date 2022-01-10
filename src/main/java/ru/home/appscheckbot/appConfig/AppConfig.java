package ru.home.appscheckbot.appConfig;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.home.appscheckbot.TelegramBot;
import ru.home.appscheckbot.TelegramFacade;

@Configuration
public class AppConfig {

    private final TelegramBotConfig telegramBotConfig;

    public AppConfig(TelegramBotConfig telegramBotConfig) {
        this.telegramBotConfig = telegramBotConfig;
    }


    @Bean
    public TelegramBot telegramBot(TelegramFacade telegramFacade) {
        TelegramBot telegramBot = new TelegramBot(telegramFacade);
        telegramBot.setBotToken(telegramBotConfig.getBotToken());
        telegramBot.setBotUsername(telegramBotConfig.getUserName());
        telegramBot.setBotPath(telegramBotConfig.getWebHookPath());
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
