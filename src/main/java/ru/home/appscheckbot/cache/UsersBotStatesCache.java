package ru.home.appscheckbot.cache;

import org.springframework.stereotype.Component;
import ru.home.appscheckbot.botApi.BotState;

import java.util.HashMap;
import java.util.Map;

@Component
public class UsersBotStatesCache {
    // HashMap для состояний бота у каждого пользователя
    private Map<Integer, BotState> usersBotStates = new HashMap<>();

    // Метод устанавливаем состояние бота для определённого юзера
    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    // Метод возвращает состояние бота для определённого юзера
    public BotState getUsersCurrentBotState(int userId) {
        return usersBotStates.get(userId);
    }
}
