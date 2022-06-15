package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class Data1 {

    @SerializedName("data")
    @Expose
    public ArrayList<ProductSubscription> data;
}
