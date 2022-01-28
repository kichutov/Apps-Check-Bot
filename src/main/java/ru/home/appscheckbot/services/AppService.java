package ru.home.appscheckbot.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.appscheckbot.DAO.AppDAO;
import ru.home.appscheckbot.cache.AppCache;
import ru.home.appscheckbot.models.App;
import ru.home.appscheckbot.models.BotUser;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@Service
public class AppService {

    private final AppCache appCache;
    private final TextService textService;
    private final SendMessageService sendMessageService;
    private final MenuService menuService;
    private final AppDAO appDAO;

    Resource userAgentsResource = new ClassPathResource("userAgents.txt");
    int countOfUserAgents;

    public AppService(AppCache appCache,
                      TextService textService,
                      SendMessageService sendMessageService,
                      MenuService menuService,
                      AppDAO appDAO) {
        this.appCache = appCache;
        this.textService = textService;
        this.sendMessageService = sendMessageService;
        this.menuService = menuService;
        this.appDAO = appDAO;
    }

    // repeat every 5 minutes
    @Scheduled(fixedDelay = 300000)
    private void updateDataFromGooglePlay() {
        this.countOfUserAgents = getCountOfUserAgents(); // read the number of UserAgents in the file
        List<App> appCashList = appCache.getAppCashList(); // get all apps from Cash
        for (App appFromCash : appCashList) {
            Boolean isNotifyInstallsCount = appFromCash.getNotifyInstallsCount();
            Boolean isNotifyRating = appFromCash.getNotifyRating();
            Boolean NotifyNumberOfRatings = appFromCash.getNotifyNumberOfRatings();

            // old application data
            String currentStatus = appFromCash.getStatus();
            String currentTitle = appFromCash.getTitle();
            String currentInstallsCount = appFromCash.getInstallsCount();
            String currentRating = appFromCash.getRating();
            String currentNumberOfRatings = appFromCash.getNumberOfRatings();

            // get updated application data
            App updatedApp = fetchDataFromGooglePlay(appFromCash);

            List<String> notificationList = new ArrayList<>();

            // if the appFromCash is no longer available on Google Play
            if (updatedApp.getStatus().equals(textService.getText("appStatus.died")) &&
                    currentStatus.equals(textService.getText("appStatus.tracked"))) {
                notificationList.add(textService.getText("notification.appHasBeenRemoved"));
            }
            // if the appFromCash is available on Google Play
            else if (updatedApp.getStatus().equals(textService.getText("appStatus.tracked"))) {
                // if the application has passed moderation
                if (currentStatus.equals(textService.getText("appStatus.moderated"))) {
                    notificationList.add(textService.getText("notification.appHasPassedModeration"));
                }
                // if the appFromCash has become available on Google Play again
                else if (currentStatus.equals(textService.getText("appStatus.died"))) {
                    notificationList.add(textService.getText("notification.appIsAvailableAgain"));
                }
                // updating the data
                else {
                    // the Installations Count has changed
                    if (!currentInstallsCount.equals(updatedApp.getInstallsCount()) && isNotifyInstallsCount) {
                        notificationList.add(textService.getText("notification.installsHasIncreased",
                                currentInstallsCount,
                                updatedApp.getInstallsCount()));
                    }
                    // the Rating has changed
                    if (!currentRating.equals(updatedApp.getRating()) && isNotifyRating) {
                        notificationList.add(textService.getText("notification.ratingHasChanged",
                                currentRating,
                                updatedApp.getRating()));
                    }
                    // the Number Of Ratings has changed
                    if (!(currentNumberOfRatings.equals(updatedApp.getNumberOfRatings())) && NotifyNumberOfRatings) {
                        notificationList.add(textService.getText("notification.numberOfRatingsHasChanged",
                                currentNumberOfRatings,
                                updatedApp.getNumberOfRatings()));
                    }
                    // the Title has changed
                    if (!currentTitle.equals(updatedApp.getTitle())) {
                        notificationList.add(textService.getText("notification.appNameHasChanged",
                                currentTitle,
                                updatedApp.getTitle()));
                    }
                }
            }

            if (!notificationList.isEmpty()) {
                StringBuilder message = new StringBuilder();
                for (String notification : notificationList) {
                    message.append(notification).append("\n");
                }
                message.append("\n");
                message.append(textService.getText("bot.appMainMenu",
                        appFromCash.getTitle() == null || appFromCash.getTitle().equals("") ? appFromCash.getBundle() : appFromCash.getTitle(),
                        appFromCash.getUrl(),
                        appFromCash.getStatus(),
                        appFromCash.getBundle(),
                        appFromCash.getInstallsCount() == null || appFromCash.getInstallsCount().equals("") ? "-" : appFromCash.getInstallsCount(),
                        appFromCash.getRating() == null || appFromCash.getRating().equals("") ? "-" : appFromCash.getRating(),
                        appFromCash.getNumberOfRatings() == null ? "-" : appFromCash.getNumberOfRatings()));
                sendMessageService.SendMessageWithKeyboard(appFromCash.getUserId().toString(),
                        message.toString(),
                        menuService.getAppMainKeyboard(appFromCash));
            }

            appFromCash.updateData(updatedApp);
            appDAO.saveApp(updatedApp);
        }
    }

    // get updated application data
    private App fetchDataFromGooglePlay(App app) {
        Document document;
        String userAgent = getRandomUserAgent();
        try {
            document = Jsoup.connect(app.getUrl() + "&gl=ru") // add "&gl=ru" for Russian geolocation
                    .userAgent(userAgent)
                    .referrer("https://www.google.com")
                    .get();
            app.setStatus(textService.getText("appStatus.tracked"));
        } catch (IOException e) {
            if (app.getStatus().equals(textService.getText("appStatus.tracked"))) {
                app.setStatus(textService.getText("appStatus.died"));
            }
            return app;
        }
        // fetch app Title
        Elements title = document.select("h1 > span");
        app.setTitle(title.text());
        // fetch app Rating
        Elements rating = document.select("div[aria-label*=Rated]");
        app.setRating(rating.text());
        // fetch app Number Of Ratings
        Elements numberOfRatings = document.select("span[aria-label*=ratings]");
        // FIX IT LATER!!!
        try {
            app.setNumberOfRatings(numberOfRatings.first().text());
        }
        catch (Exception e) {
            e.printStackTrace();
            app.setNumberOfRatings("-");
        }
        // fetch app Installs Count
        Elements installsCount = document.select("div.hAyfc:nth-child(3) > span");
        app.setInstallsCount(installsCount.text());
        return app;
    }

    public App createAppByUrl(Message message) {
        App app = new App();
        String url = message.getText();
        app.setBotUser(new BotUser(message.getFrom()));
        app.setUserId(message.getFrom().getId());
        app.setUrl(url);
        app.setBundle(app.getUrl().substring(46));
        app.setStatus(textService.getText("appStatus.moderated"));

        // get application data
        App updatedApp = fetchDataFromGooglePlay(app);
        // if successfully received the application data
        if (updatedApp.getStatus().equals(textService.getText("appStatus.tracked"))) {
            app.setStatus(textService.getText("appStatus.tracked"));
            app.setInstallsCount(updatedApp.getInstallsCount());
            app.setRating(updatedApp.getRating());
            app.setTitle(updatedApp.getTitle());
            app.setNumberOfRatings(updatedApp.getNumberOfRatings());
        }
        return app;
    }

    public String getRandomUserAgent() {
        if (countOfUserAgents == 0) {
            return "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
        }
        try {
            int randomLineNumberOfUserAgents = (int) Math.ceil(Math.random() * countOfUserAgents);
            return Files.readAllLines(userAgentsResource.getFile().toPath()).get(randomLineNumberOfUserAgents);
        } catch (IOException e) {
            e.printStackTrace();
            return "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
        }
    }

    public int getCountOfUserAgents() {
        try {
            return Files.readAllLines(userAgentsResource.getFile().toPath()).size();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
