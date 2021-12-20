package ru.home.appscheckbot.models;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bot_users")
public class BotUser {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;

    @Id
    @Column(name = "userid")
    private Integer userId;
    @Column(name = "username")
    private String userName;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "isbot")
    private Boolean isBot;
    @Column(name = "languagecode")
    private String languageCode;
    @Column(name = "dateofcreation")
    private Date dateOfCreation;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<App> apps;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageForDeveloper> messages_for_developer;

    // пустой конструктор
    public BotUser() {}
    // конструктор из User
    public BotUser(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.isBot = user.getBot();
        this.languageCode = user.getLanguageCode();

        this.dateOfCreation = new Date();
    }

    public String toString() {
        return
//                "BotUser(id=" + this.getId()
//                +
                        ", userId=" + this.getUserId()
                + ", userName=" + this.getUserName()
                + ", firstName=" + this.getFirstName()
                + ", lastName=" + this.getLastName()
                + ", isBot=" + this.getIsBot()
                + ", languageCode=" + this.getLanguageCode()
                + ", dateOfCreation=" + this.getDateOfCreation()
                + ")";
    }
}
