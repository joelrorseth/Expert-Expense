package com.rorsethj.expertexpense;

import java.util.HashMap;
import java.util.Map;

public class Transaction {

    private String account;
    private String payee;
    private String type;
    private String category;
    private String notes;
    private String date;
    private String status;
    private double amount;


    public Transaction() {}

    public Transaction(String account, String payee, String type, String category, String notes,
                       String date, String status, double amount) {

        this.account = account;
        this.payee = payee;
        this.type = type;
        this.category = category;
        this.notes = notes;
        this.date = date;
        this.status = status;
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public String getPayee() {
        return payee;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }


    public void setAccount(String account) {
        this.account = account;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
