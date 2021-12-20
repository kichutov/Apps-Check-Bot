package ru.home.appscheckbot.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "apps")
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
    private float rating;
    private String status;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private BotUser botUser;

}
