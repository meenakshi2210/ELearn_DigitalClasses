package com.digital.classes.models;

public class Book {
    public Book(String name, String book_id, long price,boolean purchased) {
        this.name = name;
        id = book_id;
        this.price = price;
        this.purchased = purchased;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
    long price;
    String id;
    boolean purchased = false;

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
}
