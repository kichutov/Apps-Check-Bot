package ru.home.appscheckbot.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.home.appscheckbot.botApi.BotState;

import java.util.HashMap;
import java.util.Map;

@Service
@Setter
@Getter
public class BotStateCash {
    // HashMap для состояний бота у каждого пользователя
    private Map<Integer, BotState> botStateMap = new HashMap<>();

    // Метод устанавливаем состояние бота для определённого юзера
    public void saveBotState(int userId, BotState botState) {
        botStateMap.put(userId, botState);
    }

}
