package barhop.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.UUID;

import java.util.Date;

import io.realm.annotations.LinkingObjects;
import io.realm.RealmResults;
import io.realm.RealmList;

public class User extends RealmObject {

    // USER MANAGEMENT //

        @PrimaryKey
        private String uuid = UUID.randomUUID().toString();
        private String displayName, password, displayPicture;
        private Date birthDate;
        private Boolean isAdmin;
        public User() {}

        public String getUuid() {return uuid;}
        public void setUuid(String uuid) {this.uuid = uuid;}
        public String getDisplayName() {return displayName;}
        public void setDisplayName(String displayName) {this.displayName = displayName;}
        public String getPassword(){ return password;}
        public void setPassword(String password){ this.password = password; }
        public String getDisplayPicture(){return displayPicture;}
        public void setDisplayPicture(String displayPicture){ this.displayPicture = displayPicture;}
        public Date getBirthDate(){return birthDate;}
        public void setBirthDate(Date birthDate){this.birthDate = birthDate;}
        public Boolean isAdmin(){return isAdmin;}
        public void setAdmin(Boolean isAdmin){this.isAdmin = isAdmin;}

    // Relationship handling of Bars, Likes, Comments //

    // BAR OWNERSHIP //
        @LinkingObjects("owner")  // reverse of Bar.owner
        private final RealmResults<Bar> barsOwned = null;
        public RealmResults<Bar> getBarsOwned() {return barsOwned;}

    // FAVOURITE BARS
        private RealmList<Bar> favoriteBars  = new RealmList<>();
        public RealmList<Bar> getFavoriteBars() { return favoriteBars;}
        public void addToFavourites(Bar bar) { favoriteBars.add(bar);}
        public void removeFromFavourites(Bar bar) { favoriteBars.remove(bar);}

    // COMMENT
        @LinkingObjects("commenter")
        private final RealmResults<Comment> comments = null;
        public RealmResults<Comment> getComments() { return comments;}

    // LIKE
        @LinkingObjects("user")
        private final RealmResults<Like> likes = null;
        public RealmResults<Like> getLikes() { return likes; }

    // BOOKING

    // FRIENDS ?








}



