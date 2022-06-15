package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelSubscriptionData {

    @SerializedName("customers_id")
    @Expose
    public String customers_id;
    @SerializedName("subscription_id")
    @Expose
    public String subscription_id;
    @SerializedName("cancel_reason")
    @Expose
    public String cancel_reason;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("fromDate")
    @Expose
    public String fromDate;
    @SerializedName("toDate")
    @Expose
    public String toDate;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomers_id() {
        return customers_id;
    }

    public void setCustomers_id(String customers_id) {
        this.customers_id = customers_id;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }
}
