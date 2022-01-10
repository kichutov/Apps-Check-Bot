package ru.home.appscheckbot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.DAO.BotUserDAO;
import ru.home.appscheckbot.DAO.MessageForDeveloperDAO;
import ru.home.appscheckbot.botApi.BotState;
import ru.home.appscheckbot.cache.AppCash;
import ru.home.appscheckbot.cache.UsersIdCash;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.models.BotUser;
import ru.home.appscheckbot.models.MessageForDeveloper;
import ru.home.appscheckbot.services.AppService;
import ru.home.appscheckbot.services.MenuService;
import ru.home.appscheckbot.services.MessageForDeveloperService;
import ru.home.appscheckbot.services.TextService;

import java.util.List;
import java.util.Set;

@Component
public class MessageHandler {

    private final AppDAO appDAO;
    private final BotUserDAO botUserDAO;
    private final MessageForDeveloperDAO messageForDeveloperDAO;
    private final MenuService menuService;
    private final TextService textService;
    private final AppService appService;
    private final UsersIdCash usersIdCash;
    private final AppCash appCash;
    private final MessageForDeveloperService messageForDeveloperService;

    public MessageHandler(AppDAO appDAO,
                          BotUserDAO botUserDAO,
                          MessageForDeveloperDAO messageForDeveloperDAO,
                          MenuService menuService,
                          TextService textService,
                          AppService appService,
                          UsersIdCash usersIdCash,
                          AppCash appCash,
                          MessageForDeveloperService messageForDeveloperService) {
        this.appDAO = appDAO;
        this.botUserDAO = botUserDAO;
        this.messageForDeveloperDAO = messageForDeveloperDAO;
        this.menuService = menuService;
        this.textService = textService;
        this.appService = appService;
        this.usersIdCash = usersIdCash;
        this.appCash = appCash;
        this.messageForDeveloperService = messageForDeveloperService;
    }

    public BotApiMethod<?> handle(Message message, BotState botState) {

        int userId = message.getFrom().getId();
        BotUser botUser = new BotUser(message.getFrom());

        // if the user writes for the first time
        Set<Integer> usersIdCashSet = usersIdCash.getUsersIdCashSet();
        if (!usersIdCashSet.contains(userId)) {
            usersIdCash.addUserIdToCash(userId);
            botUserDAO.saveBotUser(botUser);
        }

        // display the text and the keyboard from the bot depending on botState
        switch (botState.name()) {

            case ("START"):
            case ("MAIN_MENU"):
                return menuService.getMainMenu(userId);

            case ("WRITE_TO_DEVELOPER"):
                if (message.getText().equals(textService.getText("user.writeToDeveloper"))) {
                    return menuService.getWriteToDeveloper(userId, textService.getText("bot.writeToDeveloper"));
                } else if (message.getText().equals(textService.getText("user.goBackToMainMenu"))) {
                    return menuService.getMainMenu(userId);
                } else {
                    messageForDeveloperDAO.saveMessageForDeveloper(new MessageForDeveloper(botUser,  message.getText()));
                    messageForDeveloperService.sendMessageForDeveloper(message);
                    return menuService.getWriteToDeveloper(userId, textService.getText("bot.developerReceivedMessage"));
                }

            case ("MY_APPS"):
                // if the user clicked on button with name of app
                List<App> appList = appCash.findAllAppsByUserId(userId);
                for (App app : appList) {
                    if (message.getText().contains(app.getBundle())) {
                        return menuService.getAppMainMenu(message, app);
                    }
                }

                if (message.getText().equals(textService.getText("user.goBackToMainMenu"))) {
                    return menuService.getMainMenu(userId);
                } else {

                    int trackedAppsCount = 0;
                    int moderatedAppsCount = 0;
                    int diedAppsCount = 0;

                    for (App app : appList) {
                        if (app.getStatus().equals(textService.getText("appStatus.tracked"))) {
                            trackedAppsCount++;
                        } else if (app.getStatus().equals(textService.getText("appStatus.moderated"))) {
                            moderatedAppsCount++;
                        } else if (app.getStatus().equals(textService.getText("appStatus.died"))) {
                            diedAppsCount++;
                        }
                    }
                    return menuService.getMyAppsMenu(userId,
                            textService.getText("bot.myApps",
                                    trackedAppsCount,
                                    moderatedAppsCount,
                                    diedAppsCount));
                }

            case ("ADD_NEW_APP"):
                if (message.getText().equals(textService.getText("user.addApp"))) {
                    return menuService.getAddNewApp(userId, textService.getText("bot.addApp"));
                } else if (message.getText().equals(textService.getText("user.goBackToMyApps"))) {
                    return menuService.getMyAppsMenu(userId, textService.getText("bot.myApps"));
                } else {
                    // if user entered an incorrect link to the application
                    if (!message.getText().startsWith("https://play.google.com/store/apps/details?id=")) {
                        return menuService.getAddNewApp(userId, textService.getText("bot.enterCorrectLink"));
                    } else {
                        App app = appService.createAppByUrl(message); // creating an App object based on the link
                        if (appCash.existsAppByUserIdAndBundle(app.getUserId(),app.getBundle())) {
                            return menuService.getAddNewApp(userId, textService.getText("bot.appAlreadyTracked"));
                        } else {
                            appCash.addAppToCash(app);
                            appDAO.saveApp(app);
                            return menuService.getAppMainMenu(message, app);
                        }
                    }
                }

            default:
                return null;
        }
    }
}
