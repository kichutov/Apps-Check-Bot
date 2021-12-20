package ru.home.appscheckbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.home.appscheckbot.models.MessageForDeveloper;

public interface MessageForDeveloperRepository extends JpaRepository<MessageForDeveloper, Integer> {
}
