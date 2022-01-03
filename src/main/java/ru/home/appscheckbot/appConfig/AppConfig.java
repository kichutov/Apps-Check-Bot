package ru.home.appscheckbot.appConfig;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.TelegramBot;
import ru.home.appscheckbot.TelegramFacade;
import ru.home.appscheckbot.cache.AppCash;
import ru.home.appscheckbot.models.App;

@Setter
@Getter
@Configuration
public class AppConfig {

    private final TelegramBotConfig telegramBotConfig;
    private final AppDAO appDAO;

    public AppConfig(TelegramBotConfig telegramBotConfig, AppDAO appDAO) {
        this.telegramBotConfig = telegramBotConfig;
        this.appDAO = appDAO;
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

    @Bean
    public App app() {
        App app = new App();
        return app;
    }

    @Bean
    public AppCash appCash() {
        AppCash appCash = new AppCash(appDAO);
        return appCash;
    }

}
