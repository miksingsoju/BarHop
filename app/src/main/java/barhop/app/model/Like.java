package barhop.app.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Like extends RealmObject {
    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();;
    private User user;
    private Bar bar;
    public Like() { }

    public String getUuid() { return uuid; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Bar getBar() {
        return bar;
    }
    public void setBar(Bar bar) { this.bar = bar; }
}
