package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plans {
    @SerializedName("plan_name")
    @Expose
    public String plan_name;
    @SerializedName("plan_label")
    @Expose
    public String plan_label;
}