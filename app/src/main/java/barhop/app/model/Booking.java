package barhop.app.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;
import java.util.UUID;

public class Booking extends RealmObject {

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();

    private User user;       // the customer
    private Table table;     // the table being booked
    private Bar bar;         // redundancy (optional, if not inferred from Table)
    private Date bookingTime;
    private Date createdAt;  // when the booking was made
    private int durationMinutes;  // optional duration field

    public Booking() {
        this.createdAt = new Date(); // default timestamp when booking is created
    }

    public String getUuid() { return uuid; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Table getTable() { return table; }
    public void setTable(Table table) { this.table = table; }

    public Bar getBar() { return bar; }
    public void setBar(Bar bar) { this.bar = bar; }

    public Date getBookingTime() { return bookingTime; }
    public void setBookingTime(Date bookingTime) { this.bookingTime = bookingTime; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}
