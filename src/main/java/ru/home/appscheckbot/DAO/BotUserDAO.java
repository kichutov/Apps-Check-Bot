package ru.home.appscheckbot.DAO;

import org.springframework.stereotype.Service;
import ru.home.appscheckbot.models.BotUser;
import ru.home.appscheckbot.repository.BotUserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BotUserDAO {

    private final BotUserRepository botUserRepository;

    public BotUserDAO(BotUserRepository botUserRepository) {
        this.botUserRepository = botUserRepository;
    }


    public Set<Integer> getUsersIdSet() {
        return botUserRepository.findAll().stream().map(BotUser::getUserId).collect(Collectors.toSet());
    }

    public void saveBotUser(BotUser botUser) {
        botUserRepository.save(botUser);
    }

}
