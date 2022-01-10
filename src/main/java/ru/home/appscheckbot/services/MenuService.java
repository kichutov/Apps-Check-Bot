package ru.home.appscheckbot.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.DAO.BotUserDAO;
import ru.home.appscheckbot.cache.AppCash;
import ru.home.appscheckbot.models.App;

import java.util.*;

@Service
@Getter
@Setter
public class MenuService {

    private final BotUserDAO botUserDAO;
    private final AppDAO appDAO;
    private final TextService textService;
    private final AppCash appCash;

    public MenuService(BotUserDAO botUserDAO,
                       AppDAO appDAO,
                       TextService textService,
                       AppCash appCash) {
        this.botUserDAO = botUserDAO;
        this.appDAO = appDAO;
        this.textService = textService;
        this.appCash = appCash;
    }

    public SendMessage getMainMenu(int userId) {
        List<String> buttons = new ArrayList<>();
        buttons.add(textService.getText("user.myApps"));
        buttons.add(textService.getText("user.writeToDeveloper"));
        ReplyKeyboardMarkup replyKeyboardMarkup = buildReplyKeyboardMarkup(buttons);
        return createMessageWithKeyboard(userId, textService.getText("bot.mainMenu"), replyKeyboardMarkup);
    }

    public SendMessage getWriteToDeveloper(int userId, String textMessage) {
        List<String> buttons = new ArrayList<>();
        buttons.add(textService.getText("user.goBackToMainMenu"));
        ReplyKeyboardMarkup replyKeyboardMarkup = buildReplyKeyboardMarkup(buttons);
        return createMessageWithKeyboard(userId, textMessage, replyKeyboardMarkup);
    }

    public SendMessage getAddNewApp(int userId, String textMessage) {
        List<String> buttons = new ArrayList<>();
        buttons.add(textService.getText("user.goBackToMyApps"));
        ReplyKeyboardMarkup replyKeyboardMarkup = buildReplyKeyboardMarkup(buttons);
        return createMessageWithKeyboard(userId, textMessage, replyKeyboardMarkup);
    }

    public SendMessage getMyAppsMenu(int userId, String textMessage) {
        List<App> appsList = appCash.findAllAppsByUserId(userId);
        List<String> appButtons = new ArrayList<>();
        // creating buttons for each app from the list
        for (App app : appsList) {
            String appTitle, appBundle, appInstallsCount;
            // if the app has not been published on Google Play yet
            if (app.getStatus().equals(textService.getText("appStatus.moderated"))) {
                appTitle = "";
                appBundle = app.getBundle();
                appInstallsCount = "";
            } else {
                appTitle = app.getTitle();
                appBundle = app.getBundle();
                appInstallsCount = app.getInstallsCount();
            }
            String title = String.format("%s (%s) %s %s", appTitle, appBundle, appInstallsCount, app.getStatus());
            appButtons.add(title);
        }
        List<String> allButtonsList = new ArrayList<>();
        allButtonsList.add(textService.getText("user.goBackToMainMenu"));
        allButtonsList.add(textService.getText("user.addApp"));
        allButtonsList.addAll(appButtons);
        ReplyKeyboardMarkup replyKeyboardMarkup = buildReplyKeyboardMarkup(allButtonsList);
        return createMessageWithKeyboard(userId, textMessage, replyKeyboardMarkup);
    }

    public SendMessage getAppMainMenu(Message message, App app) {
        String textMessage = textService.getText("bot.appMainMenu",
                app.getTitle() == null || app.getTitle().equals("") ? app.getBundle() : app.getTitle(),
                app.getUrl(),
                app.getStatus(),
                app.getBundle(),
                app.getInstallsCount() == null || app.getInstallsCount().equals("") ? "-" : app.getInstallsCount(),
                app.getRating() == null || app.getRating().equals("") ? "-" : app.getRating(),
                app.getNumberOfRatings() == null ? "-" : app.getNumberOfRatings());
        InlineKeyboardMarkup inlineKeyboardMarkup = getAppMainKeyboard(app);
        return createMessageWithKeyboard(message.getFrom().getId(), textMessage, inlineKeyboardMarkup);
    }

    public InlineKeyboardMarkup getAppMainKeyboard(App app) {
        Map<String, String> callbackButtonsMap = new LinkedHashMap<>();
        callbackButtonsMap.put(textService.getText("user.notifications"), "notifications:" + app.getBundle());
        callbackButtonsMap.put(textService.getText("user.stopTracking"), "delete:" + app.getBundle());
        return buildInlineKeyboardMarkup(callbackButtonsMap);
    }

    public InlineKeyboardMarkup getAppNotificationKeyboard(App app) {
        Boolean notifyInstallsCount = app.getNotifyInstallsCount();
        Boolean notifyRating = app.getNotifyRating();
        Boolean notifyNumberOfRatings = app.getNotifyNumberOfRatings();
        String checkEmoji = textService.getText("emoji.check");

        Map<String, String> callbackButtonsMap = new LinkedHashMap<>();
        callbackButtonsMap.put(textService.getText("user.goBack"), "goAppMainMenu:" + app.getBundle());
        callbackButtonsMap.put((notifyInstallsCount ? checkEmoji + " " : "") + textService.getText("user.changingInstallsCount"),
                "notifyInstallsCount:" + app.getBundle());
        callbackButtonsMap.put((notifyRating ? checkEmoji + " " : "") + textService.getText("user.changingRating"),
                "notifyRating:" + app.getBundle());
        callbackButtonsMap.put((notifyNumberOfRatings ? checkEmoji + " " : "") + textService.getText("user.changingNumberOfRatings"),
                "notifyNumberOfRatings:" + app.getBundle());
        return buildInlineKeyboardMarkup(callbackButtonsMap);
    }

    private SendMessage createMessageWithKeyboard(int userId, String textMessage, ReplyKeyboard replyKeyboard) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setParseMode("html");
        sendMessage.setChatId(String.valueOf(userId));
        sendMessage.setText(textMessage);
        if (replyKeyboard != null) {
            sendMessage.setReplyMarkup(replyKeyboard);
        }
        return sendMessage;
    }

    private ReplyKeyboardMarkup buildReplyKeyboardMarkup(List<String> buttons) {
        List<KeyboardRow> keyboard = new ArrayList<>(buttons.size());
        for (String buttonText : buttons) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(buttonText));
            keyboard.add(row);
        }
        return new ReplyKeyboardMarkup()
                .setSelective(true)
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(false)
                .setKeyboard(keyboard);
    }

    private InlineKeyboardMarkup buildInlineKeyboardMarkup(Map<String, String> callbackButtonsMap) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(callbackButtonsMap.size());
        for (Map.Entry<String, String> callbackButton : callbackButtonsMap.entrySet()) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton()
                    .setText(callbackButton.getKey())
                    .setCallbackData(callbackButton.getValue());
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);
            keyboard.add(keyboardButtonsRow);
        }
        return new InlineKeyboardMarkup().setKeyboard(keyboard);

    }


}
