package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OfferData {
    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("data")
    @Expose
    private List<OfferDetails> data = new ArrayList<OfferDetails>();
    @SerializedName("message")
    @Expose
    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<OfferDetails> getData() {
        return data;
    }

    public void setData(List<OfferDetails> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
