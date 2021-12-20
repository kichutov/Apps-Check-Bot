package ru.home.appscheckbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.home.appscheckbot.models.BotUser;

public interface BotUserRepository extends JpaRepository<BotUser, Integer> {
}
