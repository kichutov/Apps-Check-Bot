package ru.home.appscheckbot.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.repository.AppRepository;

import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
public class AppDAO {

    private final AppRepository appRepository;

    @Autowired
    public AppDAO(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public App saveApp(App app) {
        return appRepository.save(app);
    }

    public List<App> getAllApps() {
        List<App> appsList = appRepository.findAll();
        return appsList;
    }

    public boolean existsAppByUrlAndUserId(String url, Integer userId) {
        App app = new App();
        app.setUrl(url);
        app.setUserId(userId);
        app.setStatus(null);
        app.setDateOfCreation(null);
        app.setNotifyInstallsCount(null);
        app.setNotifyRating(null);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("url", exact()).withMatcher("userId", exact());
        Example example = Example.of(app, matcher);
        return appRepository.exists(example);
    }

    public List<App> findAllAppsByUserId(Integer userId) {
        App app = new App();
        app.setUserId(userId);
        app.setStatus(null);
        app.setDateOfCreation(null);
        app.setNotifyInstallsCount(null);
        app.setNotifyRating(null);
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
        app.setNotifyInstallsCount(null);
        app.setNotifyRating(null);
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

    public boolean existsAppByUserIdAndBundle(Integer userId, String bundle) {
        App app = new App();
        app.setBundle(bundle);
        app.setUserId(userId);
        app.setStatus(null);
        app.setDateOfCreation(null);
        app.setNotifyInstallsCount(null);
        app.setNotifyRating(null);
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("bundle", exact()).withMatcher("userId", exact());
        Example example = Example.of(app, matcher);
        return appRepository.exists(example);
    }


    public void changeNotifyInstallsCountByUserIdAndBundle(Integer userId, String bundle) {
        App app = findAppByUserIdAndBundle(userId, bundle);
        app.setNotifyInstallsCount(!app.getNotifyInstallsCount());
        appRepository.save(app);
    }

    public void changeNotifyRatingByUserIdAndBundle(Integer userId, String bundle) {
        App app = findAppByUserIdAndBundle(userId, bundle);
        app.setNotifyRating(!app.getNotifyRating());
        appRepository.save(app);
    }


}
