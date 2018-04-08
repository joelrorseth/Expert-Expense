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

    // Return a key,value representation of the parameters
    public Map<String, Object> toMap() {

        Map<String, Object> accMap = new HashMap<>();
        accMap.put("name", accountName);
        accMap.put("description", description);
        accMap.put("currency", currency);
        accMap.put("icon", icon);
        accMap.put("balance", balance);

        return accMap;
    }
}
