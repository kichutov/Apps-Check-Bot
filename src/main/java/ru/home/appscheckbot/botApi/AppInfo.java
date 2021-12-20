package ru.home.appscheckbot.botApi;

import lombok.*;

@Data
public class AppInfo {

    private String url;
    private String title;
    private String bundle;
    private String rating;
    private String installsCount;

    public String toString() {
        return "Название: " + this.getTitle() + "\n" +
               "Бандл: " + this.getBundle() + "\n" +
               "Количество установок: " + this.getInstallsCount() + "\n" +
               "Рейтинг: " + this.getRating();
    }

}
