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

    public boolean existsAppByUrlAndUserId(String url, Integer userId) {
        App app = new App();
        app.setUrl(url);
        app.setUserId(userId);
        app.setStatus(null);
        app.setDateOfCreation(null);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("url", exact()).withMatcher("userId", exact());
        Example example = Example.of(app, matcher);
        return appRepository.exists(example);
    }

    public List<App> findAllAppsByUserId(Integer userId) {
        App app = new App();
        app.setUserId(userId);
        app.setStatus(null);
        app.setDateOfCreation(null);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("userId", exact());
        Example example = Example.of(app, matcher);
        return appRepository.findAll(example);
    }

    public App findAppByUserIdAndBundle(Integer userId, String bundle) {
        App app = new App();
        app.setUserId(userId);
        app.setBundle(bundle);
        app.setStatus(null);
        app.setDateOfCreation(null);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("bundle", exact()).withMatcher("userId", exact());
        Example example = Example.of(app, matcher);
        return (App) appRepository.findOne(example).orElse(null);
    }

    public boolean deleteAppByUserIdAndBundle(Integer userId, String bundle) {
        App app = findAppByUserIdAndBundle(userId, bundle);
        if (app == null) {
            return false;
        } else {
            appRepository.delete(app);
            return true;
        }
    }


}
