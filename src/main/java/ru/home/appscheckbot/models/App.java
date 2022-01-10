package ru.home.appscheckbot.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "apps")
@Component
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userid", insertable = false, updatable = false)
    private Integer userId;
    private String url;
    private String bundle;
    @Column(name = "installscount")
    private String installsCount;
    private String rating;
    @Column(name = "number_of_ratings")
    private String numberOfRatings;
    private String status;
    private String title;
    @Column(name = "dateofcreation")
    private Date dateOfCreation;
    @Column(name = "notify_installs_count")
    private Boolean notifyInstallsCount;
    @Column(name = "notify_rating")
    private Boolean notifyRating;
    @Column(name = "notify_number_of_ratings")
    private Boolean notifyNumberOfRatings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private BotUser botUser;

    public App() {
        this.dateOfCreation = new Date();
        this.notifyInstallsCount = true;
        this.notifyRating = true;
        this.notifyNumberOfRatings = true;
    }

    public void changeNotifyInstallsCount() {
        this.notifyInstallsCount = !this.notifyInstallsCount;
    }
    public void changeNotifyRating() {
        this.notifyRating = !this.notifyRating;
    }
    public void changeNotifyNumberOfRatings() {
        this.notifyNumberOfRatings = !this.notifyNumberOfRatings;
    }

    // don't delete
    public String toString() {
        return "App(id=" + this.getId() +
                ", userId=" + this.getUserId() +
                ", url=" + this.getUrl() +
                ", bundle=" + this.getBundle() +
                ", installsCount=" + this.getInstallsCount() +
                ", rating=" + this.getRating() +
                ", numberOfRatings=" + this.getNumberOfRatings() +
                ", status=" + this.getStatus() +
                ", title=" + this.getTitle() +
                ", dateOfCreation=" + this.getDateOfCreation() +
                ", notifyInstallsCount=" + this.getNotifyInstallsCount() +
                ", notifyRating=" + this.getNotifyRating() +
                ", notifyNumberOfRatings=" + this.getNotifyNumberOfRatings() + ")";
    }
}
