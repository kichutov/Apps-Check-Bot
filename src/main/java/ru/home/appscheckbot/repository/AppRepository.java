package ru.home.appscheckbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.home.appscheckbot.models.App;

public interface AppRepository extends JpaRepository<App, Integer> {
}
