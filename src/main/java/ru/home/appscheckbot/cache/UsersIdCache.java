package ru.home.appscheckbot.cache;

import org.springframework.stereotype.Component;
import ru.home.appscheckbot.DAO.BotUserDAO;

import java.util.Set;

@Component
public class UsersIdCache {

    private Set<Integer> usersIdCashSet;

    public UsersIdCache(BotUserDAO botUserDAO) {
        this.usersIdCashSet = botUserDAO.getUsersIdSet();
    }

    public void addUserIdToCash(int userId) {
        usersIdCashSet.add(userId);
    }

    public Set<Integer> getUsersIdCashSet() {
        return this.usersIdCashSet;
    }
}
