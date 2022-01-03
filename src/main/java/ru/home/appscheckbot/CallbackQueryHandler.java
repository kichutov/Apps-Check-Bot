package ru.home.appscheckbot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.cache.BotStateCash;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.services.LocaleMessageService;
import ru.home.appscheckbot.services.MenuService;

@Component
//processes incoming callback's
public class CallbackQueryHandler {

    private final BotStateCash botStateCash;
    private final MenuService menuService;
    private final EventHandler eventHandler;
    private final AppDAO appDAO;
    private final LocaleMessageService localeMessageService;

    public CallbackQueryHandler(BotStateCash botStateCash,
                                MenuService menuService,
                                EventHandler eventHandler,
                                AppDAO appDAO, LocaleMessageService localeMessageService) {
        this.botStateCash = botStateCash;
        this.menuService = menuService;
        this.eventHandler = eventHandler;
        this.appDAO = appDAO;
        this.localeMessageService = localeMessageService;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final long userId = buttonQuery.getFrom().getId();

        AnswerCallbackQuery callBackAnswer = null;
        App app = null;
        EditMessageText newMessage = null;

        String callbackData = buttonQuery.getData();
        String command = callbackData.substring(0, callbackData.indexOf(':'));
        String bundle = callbackData.substring(callbackData.indexOf(':') + 1);

        if (!appDAO.existsAppByUserIdAndBundle((int) userId, bundle)) {
            callBackAnswer = new AnswerCallbackQuery();
            callBackAnswer.setCallbackQueryId(buttonQuery.getId());
            callBackAnswer.setShowAlert(true);
            callBackAnswer.setText(localeMessageService.getMessage("fromBot.appIsNotTracked"));

            return callBackAnswer;
        }

        switch (command) {
            case ("delete"):
                appDAO.deleteAppByUserIdAndBundle((int) userId, bundle);
                callBackAnswer = new AnswerCallbackQuery();
                callBackAnswer.setCallbackQueryId(buttonQuery.getId());
                callBackAnswer.setShowAlert(true);
                callBackAnswer.setText(localeMessageService.getMessage("fromBot.stoppedTrackingApp"));
                return callBackAnswer;
            case ("notifications"):
                app = appDAO.findAppByUserIdAndBundle((int) userId, bundle);
                newMessage = new EditMessageText()
                        .setChatId(chatId)
                        .setParseMode("html")
                        .setMessageId(buttonQuery.getMessage().getMessageId())
                        .setReplyMarkup(menuService.getAppNotificationKeyboard(app))
                        .setText(localeMessageService.getMessage("fromBot.appMainMenu",
                                app.getTitle(),
                                app.getUrl(),
                                app.getBundle(),
                                app.getInstallsCount(),
                                app.getRating(),
                                app.getStatus().equals("died") ? localeMessageService.getMessage("fromBot.yes") : localeMessageService.getMessage("fromBot.no")));

                return newMessage;
            case ("notifyInstallsCount"):
                appDAO.changeNotifyInstallsCountByUserIdAndBundle((int) userId, bundle);
                app = appDAO.findAppByUserIdAndBundle((int) userId, bundle);
                newMessage = new EditMessageText()
                        .setChatId(chatId)
                        .setParseMode("html")
                        .setMessageId(buttonQuery.getMessage().getMessageId())
                        .setReplyMarkup(menuService.getAppNotificationKeyboard(app))
                        .setText(localeMessageService.getMessage("fromBot.appMainMenu",
                                app.getTitle(),
                                app.getUrl(),
                                app.getBundle(),
                                app.getInstallsCount(),
                                app.getRating(),
                                app.getStatus().equals("died") ? localeMessageService.getMessage("fromBot.yes") : localeMessageService.getMessage("fromBot.no")));
                return newMessage;
            case ("notifyRating"):
                appDAO.changeNotifyRatingByUserIdAndBundle((int) userId, bundle);
                app = appDAO.findAppByUserIdAndBundle((int) userId, bundle);
                newMessage = new EditMessageText()
                        .setChatId(chatId)
                        .setParseMode("html")
                        .setMessageId(buttonQuery.getMessage().getMessageId())
                        .setReplyMarkup(menuService.getAppNotificationKeyboard(app))
                        .setText(localeMessageService.getMessage("fromBot.appMainMenu",
                                app.getTitle(),
                                app.getUrl(),
                                app.getBundle(),
                                app.getInstallsCount(),
                                app.getRating(),
                                app.getStatus().equals("died") ? localeMessageService.getMessage("fromBot.yes") : localeMessageService.getMessage("fromBot.no")));
                return newMessage;
            case ("goAppMainMenu"):
                app = appDAO.findAppByUserIdAndBundle((int) userId, bundle);
                newMessage = new EditMessageText()
                        .setChatId(chatId)
                        .setParseMode("html")
                        .setMessageId(buttonQuery.getMessage().getMessageId())
                        .setReplyMarkup(menuService.getAppMainKeyboard(app))
                        .setText(localeMessageService.getMessage("fromBot.appMainMenu",
                                app.getTitle(),
                                app.getUrl(),
                                app.getBundle(),
                                app.getInstallsCount(),
                                app.getRating(),
                                app.getStatus().equals("died") ? localeMessageService.getMessage("fromBot.yes") : localeMessageService.getMessage("fromBot.no")));
                return newMessage;
        }
        return callBackAnswer;
    }
}
