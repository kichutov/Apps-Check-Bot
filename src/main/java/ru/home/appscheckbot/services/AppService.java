package ru.home.appscheckbot.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.TelegramBot;
import ru.home.appscheckbot.appConfig.ApplicationContextProvider;
import ru.home.appscheckbot.cache.AppCash;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.models.BotUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@EnableScheduling
@Service
public class AppService {

    private final AppDAO appDAO;
    private final AppCash appCash;
    private final App app;
    private final LocaleMessageService localeMessageService;


    public AppService(AppDAO appDAO, AppCash appCash, App app, LocaleMessageService localeMessageService) {
        this.appDAO = appDAO;
        this.appCash = appCash;
        this.app = app;
        this.localeMessageService = localeMessageService;
    }

    // Выполняется каждые 5 минут
    //@Scheduled(fixedDelay = 300000)
    @Scheduled(fixedDelay = 5000)
    private void updateDataFromGooglePlay() {

        // Создаём список всех приложений из AppCash
        List<App> appCashList = appCash.getAppCashList();
        // Проходимся по каждому приложению
        for (App app : appCashList) {

            // Подготовка к отправке сообщения
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setParseMode("html");
            sendMessage.setChatId(app.getUserId().toString());

            TelegramBot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(TelegramBot.class);

            String oldInstallsCount = app.getInstallsCount();
            String oldRating = app.getRating();
            String oldTitle = app.getTitle();
            String oldStatus = app.getStatus();

            List<String> messageList = new ArrayList<>();
            String message = "";

            App updatedApp = fetchDataFromGooglePlay(app);

            if (updatedApp.getStatus().equals(localeMessageService.getMessage("appStatus.tracked"))) {
                if (oldStatus.equals(localeMessageService.getMessage("appStatus.moderated"))) {
                    messageList.add("Приложение прошло модерацию и доступно в Google Play");
                } else if (oldStatus.equals(localeMessageService.getMessage("appStatus.died"))) {
                    messageList.add("Приложение снова доступно в Google Play");
                } else {
                    if (!oldInstallsCount.equals(updatedApp.getInstallsCount())) {
                        messageList.add("Увеличилось количество скачиваний");
                    }
                    if (!oldRating.equals(updatedApp.getRating())) {
                        messageList.add("Изменился рейтинг приложений");
                    }
                    if (!oldTitle.equals(updatedApp.getTitle())) {
                        messageList.add("Изменилось название приложения");
                    }
                }

                for (int i = 0; i < messageList.size(); i++) {
                    message = message + messageList.get(i) + "\n";
                }

                // Временная строка для разработки
                if (message.equals("")) {
                    message = "Ничего не изменилось\n";
                }
                message = message + "\n";


                // Пытаемся отправить сообщение

                sendMessage.setText(message + localeMessageService.getMessage("fromBot.appMainMenu",
                        app.getTitle() == null ? app.getBundle() : app.getTitle(),
                        app.getUrl(),
                        app.getStatus(),
                        app.getBundle(),
                        app.getInstallsCount() == null ? "-" : app.getInstallsCount(),
                        app.getRating() == null ? "-" : app.getRating()));


            } else if (updatedApp.getStatus().equals(localeMessageService.getMessage("appStatus.died"))) {
                message = "❌ Приложение было удалено из Google Play!\n\n";
            }

            // Отправляем сообщение
            try {
                telegramBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }

    private App fetchDataFromGooglePlay(App app) {

        // Пробуем спарсить страницу в Google Play
        Document document;
        try {

            document = Jsoup.connect(app.getUrl())
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6") // НУЖНО ПОТОМ ВСТАВИТЬ РАНДОМНУЮ ПОДСТАНОВКУ User-agent
                    .referrer("https://www.google.com")
                    .get();
            // Если удалось считать данные и у приложения был статус !tracked, то устанавливаем статус tracker
            if (!app.getStatus().equals(localeMessageService.getMessage("appStatus.tracked"))) {
                app.setStatus(localeMessageService.getMessage("appStatus.tracked"));
            }
            app.setStatus(localeMessageService.getMessage("appStatus.tracked"));

        } catch (IOException e) {

            // Если у app был статус tracked и не удалось получить данные, то установить статус died
            if (app.getStatus().equals(localeMessageService.getMessage("appStatus.tracked"))) {
                app.setStatus(localeMessageService.getMessage("appStatus.died"));
            }
            return app;

        }

        // Считываем название приложения
        Elements title = document.select("h1 > span");
        app.setTitle(title.text());
        // Устанавливаем рейтинг
        Elements rating = document.select("div[aria-label*=Rated]");
        app.setRating(rating.text());
        // Устанавливаем количество установок
        Elements installsCount = document.select("div.hAyfc:nth-child(3) > span");
        app.setInstallsCount(installsCount.text());

        return app;

    }

    public App createAppByUrl(Message message) {
        App app = new App();
        BotUser botUser = new BotUser(message.getFrom());
        String url = message.getText();
        app.setBotUser(botUser);
        app.setUrl(url);
        // Устанавливаем userId
        app.setUserId(botUser.getUserId());
        // Устанавливаем bundle
        app.setBundle(app.getUrl().substring(46));

        // Пробуем спарсить страницу в Google Play
        Document document;
        try {
            document = Jsoup.connect(app.getUrl())
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6") // НУЖНО ПОТОМ ВСТАВИТЬ РАНДОМНУЮ ПОДСТАНОВКУ User-agent
                    .referrer("https://www.google.com")
                    .get();
            app.setStatus(localeMessageService.getMessage("appStatus.tracked"));
        } catch (IOException e) {
            log.info("По ссылке url выдаёт 404 ошибку (приложение не опубликовано)");
            app.setStatus(localeMessageService.getMessage("appStatus.moderated"));
            return app;
        }

        // Считываем название приложения
        Elements title = document.select("h1 > span");
        app.setTitle(title.text());
        // Устанавливаем рейтинг
        Elements rating = document.select("div[aria-label*=Rated]");
        app.setRating(rating.text());
        // Устанавливаем количество установок
        Elements installsCount = document.select("div.hAyfc:nth-child(3) > span");
        app.setInstallsCount(installsCount.text());

        log.info("Считал данные по url (приложение опубликовано)");

        return app;
    }

}
