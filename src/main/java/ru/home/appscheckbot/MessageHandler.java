package ru.home.appscheckbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.DAO.BotUserDAO;
import ru.home.appscheckbot.DAO.MessageForDeveloperDAO;
import ru.home.appscheckbot.botApi.BotState;
import ru.home.appscheckbot.cache.BotStateCash;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.services.AppService;
import ru.home.appscheckbot.services.LocaleMessageService;
import ru.home.appscheckbot.services.MenuService;

import java.util.List;
@Slf4j
@Component
public class MessageHandler {

    private final AppDAO appDAO;
    private final BotUserDAO botUserDAO;
    private final MessageForDeveloperDAO messageForDeveloperDAO;
    private final MenuService menuService;
    private final EventHandler eventHandler;
    private final BotStateCash botStateCash;
    private final LocaleMessageService localeMessageService;
    private final AppService appService;

    public MessageHandler(AppDAO appDAO,
                          BotUserDAO botUserDAO,
                          MessageForDeveloperDAO messageForDeveloperDAO,
                          MenuService menuService,
                          EventHandler eventHandler,
                          BotStateCash botStateCash, LocaleMessageService localeMessageService, AppService appService) {
        this.appDAO = appDAO;
        this.botUserDAO = botUserDAO;
        this.messageForDeveloperDAO = messageForDeveloperDAO;
        this.menuService = menuService;
        this.eventHandler = eventHandler;
        this.botStateCash = botStateCash;
        this.localeMessageService = localeMessageService;
        this.appService = appService;
    }

    public BotApiMethod<?> handle(Message message, BotState botState) {
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        // если пользователя нет в базе
        if (!botUserDAO.existsUserByUserId(userId)) {
            eventHandler.saveNewBotUser(message);
        }

        // сохраняем состояние бота в cash
        botStateCash.saveBotState(userId, botState);


        //if state =...
        switch (botState.name()) {
            case ("START"):
                return menuService.getMainMenu(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.mainMenu"), userId);
            case ("MAIN_MENU"):
                return menuService.getMainMenu(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.mainMenu"), userId);
            case ("WRITE_TO_DEVELOPER"):
                if (message.getText().equals("Написать разработчику")) {
                    return menuService.getWriteToDeveloper(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.writeToDeveloper"), userId);
                } else if (message.getText().equals("Назад в главное меню")) {
                    return menuService.getMainMenu(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.mainMenu"), userId);
                } else {
                    eventHandler.saveMessageForDeveloper(message);
                    return menuService.getWriteToDeveloper(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.successSendMessageForDeveloper"), userId);
                }
            case ("MY_APPS"):
                List<App> appList = appDAO.findAllAppsByUserId(Math.toIntExact(message.getChatId()));
                for (App app : appList) {
                    if (message.getText().contains("(" + app.getBundle() + ")")) {
                        return menuService.getAppMainMenu(message, app,
                                localeMessageService.getMessage("fromBot.appMainMenu",
                                        app.getTitle() == null ? app.getBundle() : app.getTitle(),
                                        app.getUrl(),
                                        app.getStatus(),
                                        app.getBundle(),
                                        app.getInstallsCount() == null ? "-" : app.getInstallsCount(),
                                        app.getRating() == null ? "-" : app.getRating()));
                    }
                }

                if (message.getText().equals("Назад в главное меню")) {
                    return menuService.getMainMenu(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.mainMenu"), userId);
                } else {
                    int trackedAppsCount = 0;
                    int moderatedAppsCount = 0;
                    int diedAppsCount = 0;
                    for (App app : appList) {
                        if (app.getStatus().equals(localeMessageService.getMessage("appStatus.tracked"))) {
                            trackedAppsCount++;
                        } else if (app.getStatus().equals(localeMessageService.getMessage("appStatus.moderated"))) {
                            moderatedAppsCount++;
                        } else if (app.getStatus().equals(localeMessageService.getMessage("appStatus.died"))) {
                            diedAppsCount++;
                        }
                    }
                    return menuService.getMyAppsMenu(Math.toIntExact(message.getChatId()),
                            localeMessageService.getMessage("fromBot.myApps", trackedAppsCount, moderatedAppsCount, diedAppsCount),
                            userId);
                }

            case ("ADD_NEW_APP"):
                if (message.getText().equals("Добавить приложение")) {
                    return menuService.getAddNewApp(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.addApp"), userId);
                } else if (message.getText().equals("Назад к списку приложений")) {
                    return menuService.getMyAppsMenu(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.myApps"), userId);
                } else {

                    if (!message.getText().startsWith("https://play.google.com/store/apps/details?id=")) {
                        return menuService.getAddNewApp(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.enterCorrectLink"), userId);
                    }

                    // Здесь получаем объект app со статусом tracked или moderated
                    App app = appService.createAppByUrl(message);
                    String resultMessage = eventHandler.saveApp(app);


                    if (resultMessage.equals("Вы уже отслеживаете данное приложение")) {
                        return menuService.getAddNewApp(Math.toIntExact(message.getChatId()),localeMessageService.getMessage("fromBot.appAlreadyTracked"), userId);
                    } else {
                        return menuService.getAppMainMenu(message, app,
                                localeMessageService.getMessage("fromBot.addedAppMainMenu",
                                        app.getTitle() == null ? app.getBundle() : app.getTitle(),
                                        app.getUrl(),
                                        app.getStatus(),
                                        app.getBundle(),
                                        app.getInstallsCount() == null ? "-" : app.getInstallsCount(),
                                        app.getRating() == null ? "-" : app.getRating()));

                    }
                }
            default:
                return null;
        }
    }
}
