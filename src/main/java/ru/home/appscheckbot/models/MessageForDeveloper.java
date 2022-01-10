package ru.home.appscheckbot.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "messages_for_developer")
public class MessageForDeveloper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "userid", insertable = false, updatable = false)
    private Integer userId;
    @Column(name = "message")
    private String message;
    @Column(name = "date")
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private BotUser botUser;


    public MessageForDeveloper() {}

    public MessageForDeveloper(BotUser botUser, String message) {
        this.userId = botUser.getUserId();
        this.message = message;
        this.date = new Date();
        this.botUser = botUser;
    }
}
