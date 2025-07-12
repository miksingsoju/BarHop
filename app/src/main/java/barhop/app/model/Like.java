package barhop.app.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Like extends RealmObject {
    @PrimaryKey
    private String userUUID;

    private User user;
    private Bar bar;

    public Like(){}

    public void setUser(User user){
        this.user = user;
        this.userUUID = user.getUuid();
    }
    public User getUser(){ return user;}
    public String getUserUUID(){return userUUID; }
    public Bar getBar(){ return bar;}
    public void setBar(Bar bar){ this.bar = bar;}

}
