package com.sakurafish.pockettoushituryou.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.data.db.entity.FoodSortOrder

class SortSpinnerAdapter(private val context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return FoodSortOrder.values().size
    }

    override fun getItem(position: Int): Any {
        return FoodSortOrder.values()[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_sort_spinner, null)
        }
        return view!!
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?, parent: ViewGroup
    ): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.item_kind_spinner_dropdown, null)
        }
        val tv = view?.findViewById<View>(R.id.name) as TextView
        tv.text = FoodSortOrder.values()[position].title
        return view
    }
}