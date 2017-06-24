package com.sakurafish.pockettoushituryou.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sakurafish.pockettoushituryou.R;

public class SortSpinnerAdapter extends BaseAdapter {

    public final static String[] texts = {"食品名順", "食品名逆順", "糖質量の少ない順", "糖質量の多い順"};
    private Context context;

    public SortSpinnerAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return texts.length;
    }

    @Override
    public Object getItem(int position) {
        return texts[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_sort_spinner, null);
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position,
                                View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_kind_spinner_dropdown, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        tv.setText(texts[position]);
        return convertView;
    }
}