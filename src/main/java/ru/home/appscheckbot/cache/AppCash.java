package ru.home.appscheckbot.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.models.App;

import java.util.List;

@Setter
@Getter
@ToString
// used to save entered event data per session
public class AppCash {

    private final AppDAO appDAO;
    private List<App> appCashList;

    public AppCash(AppDAO appDAO) {
        this.appDAO = appDAO;
        this.appCashList = appDAO.getAllApps();
    }

    public void addAppToCash(App app) {
        appCashList.add(app);
    }
}
