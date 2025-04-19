package com.digital.classes.models;

public class Chapter {
    public String getName() {
        return name;
    }

    String name;
    String et;
    String eq;
    String ht;
    String hq;

    public Chapter(String name, String et, String eq, String ht, String hq) {
        this.name = name;
        this.et = et;
        this.eq = eq;
        this.ht = ht;
        this.hq = hq;
    }

    public String getEt() {
        return et;
    }

    public String getEq() {
        return eq;
    }

    public String getHt() {
        return ht;
    }

    public String getHq() {
        return hq;
    }
}
