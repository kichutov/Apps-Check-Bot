package ru.home.appscheckbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.DAO.BotUserDAO;
import ru.home.appscheckbot.DAO.MessageForDeveloperDAO;
import ru.home.appscheckbot.botApi.BotState;
import ru.home.appscheckbot.cache.AppCash;
import ru.home.appscheckbot.cache.BotStateCash;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.models.BotUser;
import ru.home.appscheckbot.models.MessageForDeveloper;
import ru.home.appscheckbot.services.MenuService;

@Slf4j
@Component
public class EventHandler {

    private final BotStateCash botStateCash;
    private final BotUserDAO botUserDAO;
    private final AppDAO appDAO;
    private final MessageForDeveloperDAO messageForDeveloperDAO;
    private final MenuService menuService;
    private final AppCash appCash;


    public EventHandler(BotStateCash botStateCash,
                        BotUserDAO botUserDAO,
                        AppDAO appDAO,
                        MessageForDeveloperDAO messageForDeveloperDAO,
                        MenuService menuService, AppCash appCash) {
        this.botStateCash = botStateCash;
        this.botUserDAO = botUserDAO;
        this.appDAO = appDAO;
        this.messageForDeveloperDAO = messageForDeveloperDAO;
        this.menuService = menuService;
        this.appCash = appCash;
    }

    // создаёт новго юзера в базе
    public void saveNewBotUser(Message message) {
        User user = message.getFrom();
        BotUser botUser = new BotUser(user);
        botUserDAO.saveBotUser(botUser);
    }

    public void saveMessageForDeveloper(Message message) {
        BotUser botUSer = new BotUser(message.getFrom());
        MessageForDeveloper messageForDeveloper = new MessageForDeveloper(botUSer, message.getText());
        messageForDeveloperDAO.saveMessageForDeveloper(messageForDeveloper);
    }

    public String saveApp(App app) {

            // Проверяем, существет ли данное приложение в базе у данного пользователя
            if (appDAO.existsAppByUrlAndUserId(app.getUrl(), app.getUserId())) {
                return "Вы уже отслеживаете данное приложение";
            } else {
                appDAO.saveApp(app);
                appCash.addAppToCash(app);
                return app.toString();
            }



    }



    //changing the state of the mailing
    public BotApiMethod<?> onEvent(Message message) {
        //  BotUser botUser = botUserDAO.findByUserId(message.getFrom().getId());


        botStateCash.saveBotState(message.getFrom().getId(), BotState.START);
        return menuService.getMainMenu(Math.toIntExact(message.getChatId()),
                "Изменения сохранены", message.getFrom().getId());
    }
}



