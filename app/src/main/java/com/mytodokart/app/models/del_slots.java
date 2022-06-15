package com.mytodokart.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class del_slots {

    @SerializedName("slot_id")
    @Expose
    public String slot_id;
    @SerializedName("slot_label")
    @Expose
    public String slot_label;
}
