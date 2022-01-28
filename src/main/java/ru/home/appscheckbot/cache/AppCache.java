package ru.home.appscheckbot.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.models.App;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@Component
public class AppCache {

    private List<App> appCashList;

    public AppCache(AppDAO appDAO) {
        this.appCashList = appDAO.getAllApps();
    }


    public void addAppToCash(App app) {
        appCashList.add(app);
    }

    public void removeAppFromCash(App app) {
        appCashList.remove(app);
    }

    public List<App> findAllAppsByUserId(int userId) {
        return appCashList.stream()
                .filter(appFromCashList -> appFromCashList.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public boolean existsAppByUserIdAndBundle(int userId, String bundle) {
        return appCashList.stream()
                .anyMatch(appFromCashList -> appFromCashList.getUserId().equals(userId)
                        && appFromCashList.getBundle().equals(bundle));
    }

    public App findAppByUserIdAndBundle(int userId, String bundle) {
        return appCashList.stream().filter(appFromCashList -> appFromCashList.getUserId().equals(userId)
                && appFromCashList.getBundle().equals(bundle)).findFirst().orElse(null);
    }
}
