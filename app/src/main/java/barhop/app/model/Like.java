package barhop.app.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Like extends RealmObject {
    @PrimaryKey
    private String userUUID;

    private String barUUID;

    public Like() { }

    public String getUser() {
        return userUUID;
    }

    public void setUser(String user){
        this.userUUID = user;
    }

    public String getBar() {
        return barUUID;
    }

    public void setBar(String bar) { this.barUUID = bar; }
}
