package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SlotData {

    @SerializedName("success")
    @Expose
    public String success;
    @SerializedName("del_slots")
    @Expose
    public ArrayList<del_slots> del_slots;
    @SerializedName("message")
    @Expose
    public String message;
}
