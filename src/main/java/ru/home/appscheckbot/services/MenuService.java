package ru.home.appscheckbot.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.DAO.BotUserDAO;
import ru.home.appscheckbot.models.App;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class MenuService {

    private final BotUserDAO botUserDAO;
    private final AppDAO appDAO;
    private final LocaleMessageService localeMessageService;

    public MenuService(BotUserDAO botUserDAO,
                       AppDAO appDAO, LocaleMessageService localeMessageService) {
        this.botUserDAO = botUserDAO;
        this.appDAO = appDAO;
        this.localeMessageService = localeMessageService;
    }

    public SendMessage getMainMenu(final int chatId, final String textMessage, final int userId) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton(localeMessageService.getMessage("fromUser.myApps")));
        row2.add(new KeyboardButton(localeMessageService.getMessage("fromUser.writeToDeveloper")));
        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    public SendMessage getWriteToDeveloper(final int chatId, final String textMessage, final int userId) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(localeMessageService.getMessage("fromUser.goBackToMainMenu")));
        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    public SendMessage getAddNewApp(final int chatId, final String textMessage, final int userId) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(localeMessageService.getMessage("fromUser.goBackToMyApps")));
        keyboard.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    public SendMessage getMyAppsMenu(final int chatId, final String textMessage, final int userId) {
        // Создаём финальную клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        // Задаём настройки клавиатуры
        replyKeyboardMarkup.setResizeKeyboard(true); // изменяем размер, делаем меньше
        // Создаём список строк
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Создаём строки для кнопки Назад и Добавить приложение
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        // Добавляем кнопки Назад и Добавить приложение в строки
        keyboardRow1.add(new KeyboardButton(localeMessageService.getMessage("fromUser.goBackToMainMenu")));
        keyboardRow2.add(new KeyboardButton(localeMessageService.getMessage("fromUser.addApp")));
        // Добавляем строку назад в список строк
        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);

        // Из базы данных достаём список приложений пользователя
        List<App> appsList = appDAO.findAllAppsByUserId(userId);
        // Вставляем кнопки для каждого приложения из списка
        for (int i = 0; i < appsList.size(); i++) {
            KeyboardRow keyboardRowForAppsList = new KeyboardRow();
            // Формируем надпись на кнопках
            String appTitle;
            String appBundle;
            String appInstallsCount;

            // Обрабатываем ситуацию, когда приложение ещё не опубликовано в Google Play
            if (appsList.get(i).getStatus().equals(localeMessageService.getMessage("appStatus.moderated"))) {
                appTitle = "";
                appBundle = appsList.get(i).getBundle();
                appInstallsCount = "";
            } else {
                appTitle = appsList.get(i).getTitle();
                appBundle = appsList.get(i).getBundle();
                appInstallsCount = appsList.get(i).getInstallsCount();
            }

            String title = String.format("%s (%s) %s %s",
                    appTitle,
                    appBundle,
                    appInstallsCount,
                    appsList.get(i).getStatus());
            keyboardRowForAppsList.add(new KeyboardButton(title));
            keyboard.add(keyboardRowForAppsList);
        }

        // Устанавливаем разметку в финальную клавиатуру
        replyKeyboardMarkup.setKeyboard(keyboard);

        return createMessageWithKeyboard(chatId, textMessage, replyKeyboardMarkup);
    }

    public SendMessage getAppMainMenu(Message message, App app, String textMessage) {

        // Создаём финальную клавиатуру для сообщения
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаём список строк
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаём строки
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        // В каждую строку добавляем 1 кнопку
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText(localeMessageService.getMessage("fromUser.notifications")).setCallbackData("notifications:" + app.getBundle()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText(localeMessageService.getMessage("fromUser.stopTracking")).setCallbackData("delete:" + app.getBundle()));
        // Добавляем строки в список строк
        keyboard.add(keyboardButtonsRow1);
        keyboard.add(keyboardButtonsRow2);

        // Устанавливаем разметку в финальную клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return createMessageWithInlineKeyboard(message.getChatId(), textMessage, inlineKeyboardMarkup);
    }

    public InlineKeyboardMarkup getAppMainKeyboard(App app) {

        // Создаём финальную клавиатуру для сообщения
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаём список строк
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаём строки
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        // В каждую строку добавляем 1 кнопку
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText(localeMessageService.getMessage("fromUser.notifications")).setCallbackData("notifications:" + app.getBundle()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText(localeMessageService.getMessage("fromUser.stopTracking")).setCallbackData("delete:" + app.getBundle()));
        // Добавляем строки в список строк
        keyboard.add(keyboardButtonsRow1);
        keyboard.add(keyboardButtonsRow2);

        // Устанавливаем разметку в финальную клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getAppNotificationKeyboard(App app) {

        // Создаём финальную клавиатуру для сообщения
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаём список строк
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаём строки
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();

        // Получаем данные из Базы об уже установленных состояниях Notification
        Boolean notifyInstallsCount = app.getNotifyInstallsCount();
        Boolean notifyRating = app.getNotifyRating();
        String checkEmoji = localeMessageService.getMessage("emoji.check");

        // В каждую строку добавляем 1 кнопку
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText((notifyInstallsCount == true ? checkEmoji : "") + " " + localeMessageService.getMessage("fromUser.changingInstallsCount")).setCallbackData("notifyInstallsCount:" + app.getBundle()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText((notifyRating == true ? checkEmoji : "") + " " + localeMessageService.getMessage("fromUser.changingRating")).setCallbackData("notifyRating:" + app.getBundle()));
        keyboardButtonsRow3.add(new InlineKeyboardButton().setText(localeMessageService.getMessage("fromUser.goBack")).setCallbackData("goAppMainMenu:" + app.getBundle()));
        // Добавляем строки в список строк
        keyboard.add(keyboardButtonsRow1);
        keyboard.add(keyboardButtonsRow2);
        keyboard.add(keyboardButtonsRow3);

        // Устанавливаем разметку в финальную клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    private SendMessage createMessageWithInlineKeyboard(final long chatId,
                                                  String textMessage,
                                                  final InlineKeyboardMarkup inlineKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setParseMode("html");
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);
        if (inlineKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        return sendMessage;
    }

    private SendMessage createMessageWithKeyboard(final long chatId,
                                                  String textMessage,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setParseMode("html");
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }


}
