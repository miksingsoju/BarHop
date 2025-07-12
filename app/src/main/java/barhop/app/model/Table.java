package barhop.app.model;

import io.realm.RealmObject;
import java.util.Date;

import io.realm.annotations.PrimaryKey;
import java.util.UUID;
public class Table extends RealmObject {
    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();

    private String name, status;
    private int capacity;
    private double price;
    private User customer;
    private Date timestamp;
    private Bar bar;

    public Table(){
        // CHOICES: ["AVAILABLE", "OCCUPIED", "RESERVED", "OCCUPIED", "CLEANING", "OUT_OF_SERVICE"
        status = "AVAILABLE"; // default when creating a table
    }

    public enum TableStatus {
        AVAILABLE,
        RESERVED,
        OCCUPIED,
        CLEANING,
        OUT_OF_SERVICE
    }

    public TableStatus getStatus(){ return TableStatus.valueOf(status);}
    public void setStatus(TableStatus status){this.status = status.name();}

    public String getUuid(){return uuid;}
    public String getName(){ return name;} // NAME OF THE TABLE NOT THE USER
    public void setName(String name){ this.name = name;}

    public int getCapacity(){return capacity;}
    public void setCapacity(int capacity){this.capacity = capacity;}

    public double getPrice(){ return price;}
    public void setPrice(double price){ this.price = price;}

    public User getCustomer(){ return customer;}
    public void setCustomer(User customer){ this.customer = customer;}

    public Date getTimestamp(){ return timestamp;}
    public void setTimestamp(Date timestamp){this.timestamp = timestamp;}

    public Bar getBar() { return bar; }
    public void setBar(Bar bar) { this.bar = bar; }




}
