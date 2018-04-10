package com.rorsethj.expertexpense;

import java.util.HashMap;
import java.util.Map;

public class Bill {

    private String payee;
    private String type;
    private String currency;
    private String category;
    private String notes;
    private long dueDate;
    private double amount;


    public Bill() {}

    public Bill(String payee, String type, String currency, String category,
                String notes, long dueDate, double amount) {

        this.payee = payee;
        this.type = type;
        this.currency = currency;
        this.category = category;
        this.notes = notes;
        this.dueDate = dueDate;
        this.amount = amount;
    }

    public String getPayee() {
        return payee;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    public long getDueDate() {
        return dueDate;
    }

    public double getAmount() {
        return amount;
    }


    public void setPayee(String payee) {
        this.payee = payee;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
