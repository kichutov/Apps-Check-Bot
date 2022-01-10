package ru.home.appscheckbot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.cache.AppCash;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.services.MenuService;
import ru.home.appscheckbot.services.TextService;

@Component
public class CallbackQueryHandler {

    private final MenuService menuService;
    private final AppDAO appDAO;
    private final TextService textService;
    private final AppCash appCash;

    public CallbackQueryHandler(MenuService menuService,
                                AppDAO appDAO,
                                TextService textService,
                                AppCash appCash) {
        this.menuService = menuService;
        this.appDAO = appDAO;
        this.textService = textService;
        this.appCash = appCash;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {

        int userId = buttonQuery.getFrom().getId();

        AnswerCallbackQuery callBackAnswer = new AnswerCallbackQuery();
        callBackAnswer.setCallbackQueryId(buttonQuery.getId());
        callBackAnswer.setShowAlert(true);

        String callbackData = buttonQuery.getData(); // get data from Callback
        String task = callbackData.substring(0, callbackData.indexOf(':')); // get command from Callback
        String bundle = callbackData.substring(callbackData.indexOf(':') + 1); // get app bundle from Callback

        if (!appCash.existsAppByUserIdAndBundle(userId, bundle)) {
            callBackAnswer.setText(textService.getText("bot.appIsNotTracked"));
            return callBackAnswer;
        }

        App app = appCash.findAppByUserIdAndBundle(userId, bundle);
        EditMessageText newMessage = new EditMessageText()
                .setChatId(String.valueOf(userId))
                .setParseMode("html")
                .setMessageId(buttonQuery.getMessage().getMessageId())
                .setText(textService.getText("bot.appMainMenu",
                        app.getTitle(),
                        app.getUrl(),
                        app.getStatus(),
                        app.getBundle(),
                        app.getInstallsCount(),
                        app.getRating(),
                        app.getNumberOfRatings()));

        switch (task) {

            case ("delete"):
                appCash.removeAppFromCash(app);
                appDAO.deleteApp(app);
                callBackAnswer.setText(textService.getText("bot.stoppedTrackingApp"));
                return callBackAnswer;

            case ("notifications"):
                newMessage.setReplyMarkup(menuService.getAppNotificationKeyboard(app));
                return newMessage;

            case ("notifyInstallsCount"):
                app.changeNotifyInstallsCount();
                appDAO.saveApp(app);
                newMessage.setReplyMarkup(menuService.getAppNotificationKeyboard(app));
                return newMessage;

            case ("notifyRating"):
                app.changeNotifyRating();
                appDAO.saveApp(app);
                newMessage.setReplyMarkup(menuService.getAppNotificationKeyboard(app));
                return newMessage;

            case ("notifyNumberOfRatings"):
                app.changeNotifyNumberOfRatings();
                appDAO.saveApp(app);
                newMessage.setReplyMarkup(menuService.getAppNotificationKeyboard(app));
                return newMessage;

            case ("goAppMainMenu"):
                newMessage.setReplyMarkup(menuService.getAppMainKeyboard(app));
                return newMessage;
        }
        return callBackAnswer;
    }
}
