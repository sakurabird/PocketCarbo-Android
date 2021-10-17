package com.sakurafish.pockettoushituryou.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.data.db.entity.Kind;

import java.util.List;

public class KindSpinnerAdapter extends BaseAdapter {

    private final Context context;
    private List<Kind> kinds;

    public KindSpinnerAdapter(Context context) {
        super();
        this.context = context;
        kinds = null;
    }

    public void setData(List<Kind> kinds) {
        this.kinds = kinds;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (kinds == null) return 0;
        return kinds.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return kinds.get(position);
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
        TextView tv = convertView.findViewById(R.id.name);
        if (tv == null) return;
        if (position == 0) {
            tv.setText(context.getString(R.string.kind_all));
        } else {
            int listPos = position - 1;
            if (listPos < 0 || listPos > kinds.size() - 1) {
                return;
            }
            Kind kinds = (Kind) getItem(listPos);
            if (kinds != null) {
                tv.setText(kinds.getName());
            }
        }
    }
}