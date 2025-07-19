package barhop.app.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;

public class Comment extends RealmObject {
    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();

    private String photo, text;
    private User commenter;
    private Bar bar;

    private Date timestamp;

    public String getUuid() { return uuid;}
    public String getPhoto(){ return photo;}
    public void setPhoto(String photo){ this.photo = photo; }
    public String getText(){return text;}
    public void setText(String text){ this.text = text;}

    public User getCommenter(){ return commenter;}
    public void setCommenter(User commenter){this.commenter = commenter; }
    public Bar getBar(){ return bar;}
    public void setBar(Bar bar){ this.bar = bar;}

    public void setTimestamp(Date timestamp){this.timestamp = timestamp;}
    public Date getTimestamp(){ return timestamp;}

}
