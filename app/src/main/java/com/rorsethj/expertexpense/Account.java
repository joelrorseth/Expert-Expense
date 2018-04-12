package com.rorsethj.expertexpense;

import java.util.HashMap;
import java.util.Map;

public class Account {

    private String accountName;
    private String description;
    private String currency;
    private String icon;
    private double balance;


    public Account() {}

    public Account(String name, String desc, String curr, String ic, double bal) {

        accountName = name;
        description = desc;
        currency = curr;
        icon = ic;
        balance = bal;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrency() {
        return currency;
    }

    public String getIcon() {
        return icon;
    }

    public double getBalance() {
        return balance;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
