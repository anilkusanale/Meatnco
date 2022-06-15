package com.mytodokart.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mytodokart.app.R;
import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflter;
    ArrayList<String> plans;

    public SpinnerAdapter(Context applicationContext, ArrayList<String> plans) {
        this.context = applicationContext;
        this.plans = plans;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return plans.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item, null); // inflate the layout
        TextView itemTv = (TextView) view.findViewById(R.id.itemTv); // get the reference of ImageView
       itemTv.setText(plans.get(i));
        return view;
    }
}
