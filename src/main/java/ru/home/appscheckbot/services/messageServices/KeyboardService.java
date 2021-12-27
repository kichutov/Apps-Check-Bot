package ru.home.appscheckbot.services.messageServices;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.services.databaseServices.AppService;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardService {

    private final AppService appService;

    public KeyboardService(AppService appService) {
        this.appService = appService;
    }

    // Создание клавиатуры для MainMenu
    public ReplyKeyboardMarkup makeKeyboardMainMenu() {

        // Создаём финальную клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        // Задаём настройки клавиатуры
        replyKeyboardMarkup.setResizeKeyboard(true);
        // Создаём список строк
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Создаём строки
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        // В каждую строку добавляем 1 кнопку
        keyboardRow1.add(new KeyboardButton("Мои приложения"));
        keyboardRow2.add(new KeyboardButton("Написать разработчику"));
        // Добавляем строки в список строк
        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);
        // Устанавливаем разметку в финальную клавиатуру
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    // Создание клавиатуры для WriteToDeveloper
    public ReplyKeyboardMarkup makeKeyboardWriteToDeveloper() {
        // Создаём финальную клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        // Задаём настройки клавиатуры
        replyKeyboardMarkup.setResizeKeyboard(true); // изменяем размер, делаем меньше
        // Создаём список строк
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Создаём строки
        KeyboardRow keyboardRow1 = new KeyboardRow();
        // В каждую строку добавляем 1 кнопку
        keyboardRow1.add(new KeyboardButton("Назад"));
        // Добавляем строки в список строк
        keyboard.add(keyboardRow1);
        // Устанавливаем разметку в финальную клавиатуру
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    // Создание клавиатуры для MyApps
    public ReplyKeyboardMarkup makeKeyboardMyApps(int userId) {
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
        keyboardRow1.add(new KeyboardButton("Назад"));
        keyboardRow2.add(new KeyboardButton("Добавить приложение"));
        // Добавляем строку назад в список строк
        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);

        // Из базы данных достаём список приложений пользователя
        List<App> appsList = appService.findAllAppsByUserId(userId);
        // Вставляем кнопки для каждого приложения из списка
        for (int i = 0; i < appsList.size(); i++) {
            KeyboardRow keyboardRowForAppsList = new KeyboardRow();
            // Формируем надпись на кнопках
            String title = String.format("%s (%s) %s",
                    appsList.get(i).getTitle(),
                    appsList.get(i).getBundle(),
                    appsList.get(i).getInstallsCount());
            keyboardRowForAppsList.add(new KeyboardButton(title));
            keyboard.add(keyboardRowForAppsList);
        }

        // Устанавливаем разметку в финальную клавиатуру
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    // Создание клавиатуры для AddNewApp
    public ReplyKeyboardMarkup makeKeyboardAddNewApp() {
        // Создаём финальную клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        // Задаём настройки клавиатуры
        replyKeyboardMarkup.setResizeKeyboard(true); // изменяем размер, делаем меньше
        // Создаём список строк
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Создаём строки
        KeyboardRow keyboardRow1 = new KeyboardRow();
        // В каждую строку добавляем 1 кнопку
        keyboardRow1.add(new KeyboardButton("Назад"));
        // Добавляем строки в список строк
        keyboard.add(keyboardRow1);
        // Устанавливаем разметку в финальную клавиатуру
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    // Создание клавиатуры для NotifyUser
    public ReplyKeyboardMarkup makeKeyboardNotifyUser() {

        // Создаём финальную клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        // Задаём настройки клавиатуры
        replyKeyboardMarkup.setResizeKeyboard(true); // изменяем размер, делаем меньше
        // Создаём список строк
        List<KeyboardRow> keyboard = new ArrayList<>();


        // Создаём строки
        KeyboardRow keyboardRow1 = new KeyboardRow();
        // В каждую строку добавляем 1 кнопку
        keyboardRow1.add(new KeyboardButton("Ок"));
        // Добавляем строки в список строк
        keyboard.add(keyboardRow1);
        // Устанавливаем разметку в финальную клавиатуру
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    // Создание клавиатуры для AppMenu
    public InlineKeyboardMarkup makeKeyboardAppMainMenu(App app) {

        // Создаём финальную клавиатуру для сообщения
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        // Создаём список строк
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаём строки
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        // В каждую строку добавляем 1 кнопку
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Уведомления").setCallbackData("notifications:" + app.getBundle()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Удалить").setCallbackData("delete:" + app.getBundle()));
        // Добавляем строки в список строк
        keyboard.add(keyboardButtonsRow1);
        keyboard.add(keyboardButtonsRow2);

        // Устанавливаем разметку в финальную клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup makeKeyboardAppNotificationsMenu(App app) {

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

        // В каждую строку добавляем 1 кнопку
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText(notifyInstallsCount + " - Кол-во установок").setCallbackData("notifyInstallsCount:" + app.getBundle()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText(notifyRating + " - Изменение рейтинга").setCallbackData("notifyRating:" + app.getBundle()));
        keyboardButtonsRow3.add(new InlineKeyboardButton().setText("Назад").setCallbackData("goAppMainMenu:" + app.getBundle()));
        // Добавляем строки в список строк
        keyboard.add(keyboardButtonsRow1);
        keyboard.add(keyboardButtonsRow2);
        keyboard.add(keyboardButtonsRow3);

        // Устанавливаем разметку в финальную клавиатуру
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }




}
