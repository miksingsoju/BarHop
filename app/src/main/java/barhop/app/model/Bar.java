package barhop.app.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Bar extends RealmObject {

    // BAR MANAGEMENT

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String name, location, description;
    private User owner;
    public Bar(){}
    public String getUuid(){ return uuid;}
    public String getName(){ return name;}
    public void setName(String name){ this.name = name;}
    public String getLocation(){ return location;}
    public void setLocation(String location){ this.location = location;}
    public User getOwner(){ return owner;}
    public void setOwner(User owner){ this.owner = owner;}
    public String getDescription(){ return description;}
    public void setDescription(String description){ this.description = description;}

    // Relationship handling of Likes, Comments, [Bookings???] //

    // LIKES
        @LinkingObjects("bar")
        private final RealmResults<Like> likes = null;
        public RealmResults<Like> getLikes() { return likes; }

    // COMMENTS
        @LinkingObjects("bar")  // reverse of Comment.bar
        private final RealmResults<Comment> comments = null;
        public RealmResults<Comment> getComments() { return comments; }

}
