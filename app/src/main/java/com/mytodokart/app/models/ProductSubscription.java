package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductSubscription {

    @SerializedName("orders_products_id")
    @Expose
    public String orders_products_id;
    @SerializedName("orders_id")
    @Expose
    public String orders_id;
    @SerializedName("products_id")
    @Expose
    public String products_id;
    @SerializedName("products_model")
    @Expose
    public String products_model;
    @SerializedName("products_name")
    @Expose
    public String products_name;
    @SerializedName("products_price")
    @Expose
    public String products_price;
    @SerializedName("final_price")
    @Expose
    public String final_price;
    @SerializedName("products_tax")
    @Expose
    public String products_tax;
    @SerializedName("products_quantity")
    @Expose
    public String products_quantity;
    @SerializedName("products_subscription")
    @Expose
    public String products_subscription;
    @SerializedName("subs_freq")
    @Expose
    public String subs_freq;
    @SerializedName("stripe_cus_id")
    @Expose
    public String stripe_cus_id;
    @SerializedName("subscription_status")
    @Expose
    public String subscription_status;
    @SerializedName("last_paid_on")
    @Expose
    public String last_paid_on;
    @SerializedName("date_purchased")
    @Expose
    public String date_purchased;
}
