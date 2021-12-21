package ru.home.appscheckbot.controllers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.home.appscheckbot.TelegramBot;
import ru.home.appscheckbot.botApi.BotState;
import ru.home.appscheckbot.cache.UsersBotStatesCache;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.models.BotUser;
import ru.home.appscheckbot.models.MessageForDeveloper;
import ru.home.appscheckbot.services.databaseServices.AppService;
import ru.home.appscheckbot.services.databaseServices.BotUserService;
import ru.home.appscheckbot.services.databaseServices.MessageForDeveloperService;
import ru.home.appscheckbot.services.messageServices.LocaleMessageService;

import java.io.IOException;

@Slf4j
@RestController
public class WebHookController {

    private final TelegramBot telegramBot;
    private final BotUserService botUserService;
    private final AppService appService;
    private final UsersBotStatesCache usersBotStatesCache;
    private final LocaleMessageService localeMessageService;
    private final MessageForDeveloperService messageForDeveloperService;

    public WebHookController(TelegramBot telegramBot,
                             BotUserService botUserService,
                             AppService appService,
                             UsersBotStatesCache usersBotStatesCache,
                             LocaleMessageService localeMessageService,
                             MessageForDeveloperService messageForDeveloperService) {
        this.telegramBot = telegramBot;
        this.botUserService = botUserService;
        this.appService = appService;
        this.usersBotStatesCache = usersBotStatesCache;
        this.localeMessageService = localeMessageService;
        this.messageForDeveloperService = messageForDeveloperService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateRecived(@RequestBody Update update) {

        // Если в update нет message и text, то ничего не делаем
        if (!(update.getMessage() != null && update.getMessage().hasText())) {
            return telegramBot.doNothing(); // Ничего не меняем на frontend
        }

        // Создаём объекты из update для работы
        User user = update.getMessage().getFrom();
        String messageText = update.getMessage().getText();

        log.info("New update from {}, Text: {}", user, messageText);

        // Если в update текст /start, то устанавливаем BotState = MAIN_MENU и заносим пользователя в базу
        if (messageText.equals(localeMessageService.getMessage("fromUser.start"))) {

            usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MAIN_MENU);
            log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));

            // Если пользователя нет в базе данных, то заносим его в базу
            log.info(String.valueOf(botUserService.existsUserByUserId(user.getId())));
            if (!botUserService.existsUserByUserId(user.getId())) {
                botUserService.saveUser(new BotUser(user));
            }
            return telegramBot.mainMenu(update);
        }

        // Если статус MAIN_MENU и в update текст "Написать разработчику", то устанавливаем статус WRITE_TO_DEVELOPER
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.MAIN_MENU
                && messageText.equals(localeMessageService.getMessage("fromUser.writeToDeveloper"))) {
            usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.WRITE_TO_DEVELOPER);
            log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
            return telegramBot.writeToDeveloper(update);
        }

        // Если статус WRITE_TO_DEVELOPER, то...
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.WRITE_TO_DEVELOPER) {
            if (messageText.equals(localeMessageService.getMessage("fromUser.goBack"))) { // Если "Назад", то возвращаемся в главное меню
                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MAIN_MENU);
                log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
                return telegramBot.mainMenu(update);
            } else {
                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.NOTIFICATION);
                log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));

                // отправить сообщение разработчику в базу данных
                log.info("Перед отправкой сообщения");
                BotUser botUser = botUserService.getUserByUserId(user.getId());
                log.info("Нашёл ботюзера при отправке сообщения {}", botUser.toString());
                messageForDeveloperService.saveMessageForDeveloper(new MessageForDeveloper(botUser, messageText));

                // Формируем уведомление, вызываем метод уведомления пользователя
                String notification = localeMessageService.getMessage("fromBot.successSendMessageForDeveloper");
                return telegramBot.notifyUser(update, notification);
            }
        }

        // Если в update текст "Ок" и статус BotState.NOTIFICATION, то устанавливаем статус MAIN_MENU
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.NOTIFICATION
                && messageText.equals(localeMessageService.getMessage("fromUser.Ok"))) {
            usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MAIN_MENU);
            log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
            return telegramBot.mainMenu(update); // переходим в главное меню
        }

        // Если статус MAIN_MENU и в update текст "Мои приложения", то устанавливаем статус MY_APPS
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.MAIN_MENU
                && messageText.equals(localeMessageService.getMessage("fromUser.myApps"))) {
            usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MY_APPS);
            log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
            return telegramBot.myApps(update);
        }

        // Если статус MY_APPS
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.MY_APPS) {
            // в update текст "Назад"
            if (messageText.equals(localeMessageService.getMessage("fromUser.goBack"))) {
                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MAIN_MENU);
                log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
                return telegramBot.mainMenu(update); // переходим в главное меню
            }
            // в update текст "Добавить приложение"
            if (messageText.equals(localeMessageService.getMessage("fromUser.addApp"))) {
                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.ADD_NEW_APP);
                log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
                return telegramBot.addNewApp(update, "fromBot.addApp");
            }

            // РЕАЛИЗОВАТЬ КЛИК ПО КНОПКЕ С НАЗВАНИЕМ ПРИЛОЖЕНИЯ
        }



        // Если статус ADD_NEW_APP
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.ADD_NEW_APP) {

            // в update текст "Назад"
            if (messageText.equals(localeMessageService.getMessage("fromUser.goBack"))) {
                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MY_APPS);
                log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
                return telegramBot.myApps(update);

            } else if (messageText.startsWith("https://play.google.com/store/apps/details?id=")) {

                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.NOTIFICATION);
                log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));

                // Создаём объект с информацией о приложении
                App app = new App();
                app.setUrl(messageText); // Заносим ссылку в Google Play
                // Проверяем, есть ли приложение в базе
                log.info(messageText);
                log.info(user.getId().toString());
                log.info(String.valueOf(appService.existsAppByUrlAndUserId(messageText, user.getId())));
                if (!(appService.existsAppByUrlAndUserId(messageText, user.getId()))) {
                    // Создаём переменную для страницу Google Play
                    Document document;
                    // Пробуем спарсить страницу в Google Play
                    try {
                        document = Jsoup.connect(app.getUrl())
                                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6") // НУЖНО ПОТОМ ВСТАВИТЬ РАНДОМНУЮ ПОДСТАНОВКУ User-agent
                                .referrer("https://www.google.com")
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    // Считываем название приложения
                    Elements title = document.select("h1 > span");
                    app.setTitle(title.text());

                    // Устанавливаем bundle
                    app.setBundle(app.getUrl().substring(46));

                    // Устанавливаем рейтинг
                    Elements rating = document.select("div[aria-label*=Rated]");
                    app.setRating(rating.text());

                    // Устанавливаем количество установок
                    Elements installsCount = document.select("div.hAyfc:nth-child(3) > span");
                    app.setInstallsCount(installsCount.text());

                    log.info("Перед добавлением приложения");
                    BotUser botUser = botUserService.getUserByUserId(user.getId());
                    log.info("Нашёл ботюзера при добавлении приложения {}", botUser.toString());
                    app.setBotUser(botUser);
                    log.info("Внедрил ботЮзера в app");

                    app.setUserId(botUser.getUserId());
                    log.info("{}", app.toString());

                    appService.saveApp(app);
                    // уведомление о том, что приложение добавлено
                    return telegramBot.notifyUser(update, "Приложение было добавлено");
                } else {
                    // уведомление о том, что приложение уже есть в базе
                    return telegramBot.notifyUser(update, "Приложение уже отслеживается");
                }

            } else {
                return telegramBot.addNewApp(update, "fromBot.enterCorrectLink");
            }

        }







        // Если статус MY_APPS, то...
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.MY_APPS) {
            if (messageText.equals(localeMessageService.getMessage("fromUser.goBack"))) { // Если "Назад", то...
                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MAIN_MENU); // Просто возвращаемся в главное меню
            } else if (messageText.equals(localeMessageService.getMessage("fromUser.addApp"))) { // Если "Добавить приложение", то...
                usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.ADD_NEW_APP); // Устанавливаем статус ADD_NEW_APP
            }

            // ТУТ НУЖНО ДОПИСАТЬ ЛОГИКУ ДЛЯ ОБРАБОТКИ КЛИКА ПО КНОПКАМ С НАЗВАНИЯМИ ПРИЛОЖЕНИЙ

            log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
            return telegramBot.onWebhookUpdateReceived(update);
        }











        return telegramBot.onWebhookUpdateReceived(update);
    }
}




