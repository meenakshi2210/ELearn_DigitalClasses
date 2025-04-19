package com.digital.classes.models;

public class Wallet {
    long balance;

    Wallet(){ }

    public Wallet(long i) {
        balance = i;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
