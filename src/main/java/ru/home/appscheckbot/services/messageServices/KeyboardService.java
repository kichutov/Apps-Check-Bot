package ru.home.appscheckbot.services.messageServices;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
            keyboardRowForAppsList.add(new KeyboardButton(appsList.get(i).getTitle()));
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



}

//    // Создаём строку кнопок 1
//    List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
//            keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Кнопка 1-1")
//                    .setCallbackData("CallFi4a")); // Добавляем кнопку 1
//            keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Кнопка 1-2")
//                    .setCallbackData("CallFi4a")); // Добавляем кнопку 2
//
//    // Создаём строку кнопок 2
//    List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
//            keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Кнопка 2-1")
//                    .setCallbackData("CallFi4a")); // Добавляем кнопку 3
//            keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Кнопка 2-2")
//                    .setCallbackData("CallFi4a")); // Добавляем кнопку 4
//
//
//    // Добавляем строки в массив строк
//    List<List<InlineKeyboardButton>> rowList= new ArrayList<>();
//            rowList.add(keyboardButtonsRow1);
//            rowList.add(keyboardButtonsRow2);
//
//    // Создаём клавиатуру
//    InlineKeyboardMarkup inlineKeyboardMarkup =new InlineKeyboardMarkup();
//
//    // Собираем финальную клавиатуру
//            inlineKeyboardMarkup.setKeyboard(rowList);

