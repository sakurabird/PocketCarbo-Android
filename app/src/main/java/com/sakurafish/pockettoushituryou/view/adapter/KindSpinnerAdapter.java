package com.sakurafish.pockettoushituryou.view.adapter;


import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.data.db.entity.Kinds;

import java.util.List;

public class KindSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<Kinds> kindsList;

    public KindSpinnerAdapter(Context context) {
        super();
        this.context = context;
        kindsList = null;
    }

    public void setData(List<Kinds> data) {
        kindsList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (kindsList == null) return 0;
        return kindsList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return kindsList.get(position);
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
            convertView = inflater.
                    inflate(R.layout.item_kind_spinner, null);
        }
        setNameString(convertView, position);
        return convertView;
    }

    @Override
    public View getDropDownView(int position,
                                View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_kind_spinner_dropdown, null);
        }
        setNameString(convertView, position);
        return convertView;
    }

    private void setNameString(@NonNull View convertView, int position) {
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        if (tv == null) return;
        if (position == 0) {
            tv.setText(context.getString(R.string.kind_all));
        } else {
            int listPos = position - 1;
            if (listPos < 0 || listPos > kindsList.size() - 1) {
                return;
            }
            Kinds kinds = (Kinds) getItem(listPos);
            if (kinds != null) {
                tv.setText(kinds.name);
            }
        }
    }
}