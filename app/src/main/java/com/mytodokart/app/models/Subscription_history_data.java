package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subscription_history_data {

    @SerializedName("customers_id")
    @Expose
    public String customers_id;

    public String getCustomers_id() {
        return customers_id;
    }

    public void setCustomers_id(String customers_id) {
        this.customers_id = customers_id;
    }
}
