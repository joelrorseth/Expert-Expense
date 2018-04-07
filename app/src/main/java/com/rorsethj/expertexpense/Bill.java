package com.rorsethj.expertexpense;

import java.util.HashMap;
import java.util.Map;

public class Bill {

    private String payee;
    private String type;
    private String currency;
    private String category;
    private String notes;
    private String dueDate;
    private double amount;


    public Bill(String payee, String type, String currency, String category,
                String notes, String dueDate, double amount) {

        this.payee = payee;
        this.type = type;
        this.currency = currency;
        this.category = category;
        this.notes = notes;
        this.dueDate = dueDate;
        this.amount = amount;
    }

    // Return a key,value representation of the parameters
    public Map<String, Object> toMap() {

        Map<String, Object> accMap = new HashMap<>();
        accMap.put("payee", payee);
        accMap.put("type", type);
        accMap.put("currency", currency);
        accMap.put("category", category);
        accMap.put("notes", notes);
        accMap.put("dueDate", dueDate);
        accMap.put("amount", amount);
        return accMap;
    }
}
