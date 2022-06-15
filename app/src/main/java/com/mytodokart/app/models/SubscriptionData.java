package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;


public class SubscriptionData  {

    @SerializedName("success")
    @Expose
    public String success;
    @SerializedName("plans_array")
    @Expose
    public ArrayList<Plans> plans_array;
    @SerializedName("message")
    @Expose
    public String message;
}


