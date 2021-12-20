package ru.home.appscheckbot.services.databaseServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.repository.AppRepository;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class AppService {

    private final AppRepository appRepository;

    @Autowired
    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public App saveApp(App app) {
        return appRepository.save(app);
    }

    public boolean existsAppByUrl(String url, int userId) {
        App app = new App();
        app.setUrl(url);
        app.setUserId(userId);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("url", exact()).withMatcher("userId", exact());
        Example example = Example.of(app, matcher);
        return appRepository.exists(example);
    }

    public List<App> findAllAppsByUserId(int userId) {
        App app = new App();
        app.setUserId(userId);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("userId", exact());
        Example example = Example.of(app, matcher);
        return appRepository.findAll(example);
    }
}
