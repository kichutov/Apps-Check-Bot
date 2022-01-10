package ru.home.appscheckbot.DAO;

import org.springframework.stereotype.Service;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.repository.AppRepository;

import java.util.List;

@Service
public class AppDAO {

    private final AppRepository appRepository;

    public AppDAO(AppRepository appRepository) {
        this.appRepository = appRepository;
    }


    public void saveApp(App app) {
        appRepository.save(app);
    }

    public List<App> getAllApps() {
        return appRepository.findAll();
    }

    public void deleteApp(App app) {
        appRepository.delete(app);
    }


}
