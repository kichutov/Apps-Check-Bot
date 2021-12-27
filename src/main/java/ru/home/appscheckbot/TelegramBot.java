package ru.home.appscheckbot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.appscheckbot.botApi.BotState;
import ru.home.appscheckbot.cache.UsersBotStatesCache;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.services.messageServices.KeyboardService;
import ru.home.appscheckbot.services.messageServices.LocaleMessageService;
import ru.home.appscheckbot.services.messageServices.ReplyMessagesService;

@Slf4j
@Setter
@Getter
public class TelegramBot extends TelegramWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;

    private final LocaleMessageService localeMessageService;
    private final ReplyMessagesService replyMessagesService;
    private final UsersBotStatesCache usersBotStatesCache;
    private final KeyboardService keyboardService;

    public TelegramBot(LocaleMessageService localeMessageService,
                       ReplyMessagesService replyMessagesService,
                       UsersBotStatesCache usersBotStatesCache,
                       KeyboardService keyboardService) {
        this.localeMessageService = localeMessageService;
        this.replyMessagesService = replyMessagesService;
        this.usersBotStatesCache = usersBotStatesCache;
        this.keyboardService = keyboardService;
    }

    // Создаём объект сообщения
    SendMessage message = new SendMessage();

    // Ничего не меняем на frontend
    public BotApiMethod<?> doNothing() {
        log.info("Do nothing");
        return null;
    }

    // Отображаем главное меню
    public BotApiMethod<?> mainMenu(Update update) {
        message.setChatId(update.getMessage().getChatId().toString()); // устанавливаем chatId
        message.setText(localeMessageService.getMessage("fromBot.mainMenu"));
        message.setReplyMarkup(keyboardService.makeKeyboardMainMenu());
        log.info("Main menu");
        return message;
    }

    public BotApiMethod<?> notifyUser(Update update, String notification) {
            message.setChatId(update.getMessage().getChatId().toString());// устанавливаем chatId
            message.setText(notification);
            message.setReplyMarkup(keyboardService.makeKeyboardNotifyUser());
        log.info("Notify User");
            return message;
    }

    public BotApiMethod<?> popup(Update update, String notification) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(notification);
        log.info("Popup");
        try {
            // Показываем всплывающее сообщение
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return answerCallbackQuery;
    }

    // Отображаем страницу контакта с разработчиком
    public BotApiMethod<?> writeToDeveloper(Update update) {
        message.setChatId(update.getMessage().getChatId().toString()); // устанавливаем chatId
        message.setText(localeMessageService.getMessage("fromBot.writeToDeveloper"));
        message.setReplyMarkup(keyboardService.makeKeyboardWriteToDeveloper());
        log.info("Write to developer");
        return message;
    }

    public BotApiMethod<?> myApps(Update update) {
        User user = update.getMessage().getFrom();
        message.setChatId(update.getMessage().getChatId().toString()); // устанавливаем chatId
        message.setText(localeMessageService.getMessage("fromBot.myApps"));
        message.setReplyMarkup(keyboardService.makeKeyboardMyApps(user.getId()));
        log.info("My apps");
        return message;
    }

    public BotApiMethod<?> addNewApp(Update update, String messageText) {
        message.setChatId(update.getMessage().getChatId().toString()); // устанавливаем chatId
        message.setText(localeMessageService.getMessage(messageText));
        message.setReplyMarkup(keyboardService.makeKeyboardAddNewApp());
        log.info("Add new app");
        return message;
    }

    public BotApiMethod<?> appMenu(Update update, App app) {
        message.setChatId(update.getMessage().getChatId().toString()); // устанавливаем chatId
        message.setText(localeMessageService.getMessage("fromBot.appMenu") + " " + app.getBundle() );
        message.setReplyMarkup(keyboardService.makeKeyboardAppMenu(app.getBundle()));
        log.info("Write to developer");
        return message;
    }






    @SneakyThrows
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {



        // Создаём объекты из update для работы
        User user = update.getMessage().getFrom();
        String messageText = update.getMessage().getText();

        log.info("New update from {}, Text: {}", user, messageText);


        message.setChatId(update.getMessage().getChatId().toString()); // устанавливаем chatId


        // Если usersBotStatesCache = ADD_NEW_APP, отображаем список приложений
        if (usersBotStatesCache.getUsersCurrentBotState(user.getId()) == BotState.ADD_NEW_APP) {
            message.setText(localeMessageService.getMessage("fromBot.addApp"));
            message.setReplyMarkup(keyboardService.makeKeyboardAddNewApp());
            log.info("BotState: {}", usersBotStatesCache.getUsersCurrentBotState(user.getId()));
            return message;
        }





        // Если никакое правило не отработало, то возвращаеся в главное меню
        log.info("NO RULE HAS WORKED!!!");
        usersBotStatesCache.setUsersCurrentBotState(user.getId(), BotState.MAIN_MENU); // Устанавливаем статус MAIN_MENU
        message.setText(localeMessageService.getMessage("fromBot.mainMenu"));
        message.setReplyMarkup(keyboardService.makeKeyboardMainMenu());
        return message;
    }


}

