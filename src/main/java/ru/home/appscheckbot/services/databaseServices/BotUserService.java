package ru.home.appscheckbot.services.databaseServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.home.appscheckbot.models.BotUser;
import ru.home.appscheckbot.repository.BotUserRepository;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class BotUserService {

    private final BotUserRepository botUserRepository;

    @Autowired
    public BotUserService(BotUserRepository botUserRepository) {
        this.botUserRepository = botUserRepository;
    }

    public BotUser getUserById(Integer id) {
        return botUserRepository.getOne(id);
    }

    public BotUser getUserByUserId(Integer userId) {
        BotUser botUser = new BotUser();
        botUser.setUserId(userId);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("userid", exact());
        Example<BotUser> example = Example.of(botUser, matcher);
        return botUserRepository.findOne(example).get();
    }

    public boolean existsUserByUserId(Integer userId) {
        BotUser botUser = new BotUser();
        botUser.setUserId(userId);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("userid", exact());
        Example example = Example.of(botUser, matcher);
        return botUserRepository.exists(example);
    }

    public List<BotUser> findAll() {
        return botUserRepository.findAll();
    }

    public BotUser saveUser(BotUser botUser) {
        return botUserRepository.save(botUser);
    }

    public void deleteById(Integer id) {
        botUserRepository.deleteById(id);
    }
}
