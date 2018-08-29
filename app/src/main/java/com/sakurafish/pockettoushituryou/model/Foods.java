package com.sakurafish.pockettoushituryou.model;

import android.support.annotation.Nullable;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;
import com.google.gson.annotations.SerializedName;

@Table
public class Foods {

    @PrimaryKey(auto = false)
    @Column(indexed = true)
    @SerializedName("id")
    public int id;

    @Column(indexed = true)
    @SerializedName("name")
    public String name;

    @Column
    @SerializedName("weight")
    public int weight;

    @Column
    @Nullable
    @SerializedName("weight_hint")
    public String weightHint;

    @Column(indexed = true)
    @SerializedName("carbohydrate_per_100g")
    public float carbohydratePer100g;

    @Column
    @SerializedName("carbohydrate_per_weight")
    public float carbohydratePerWeight;

    @Column
    @SerializedName("calory")
    public float calory;

    @Column
    @SerializedName("protein")
    public float protein;

    @Column
    @SerializedName("fat")
    public float fat;

    @Column
    @SerializedName("sodium")
    public float sodium;

    @Column(indexed = true)
    @Nullable
    @SerializedName("search_word")
    public String searchWord;

    @Column(indexed = true)
    @SerializedName("type_id")
    public int typeId;

    @Column(indexed = true)
    @SerializedName("kind_id")
    public int kindId;

    @Override
    public boolean equals(Object o) {
        return o instanceof Foods && ((Foods) o).id == id || super.equals(o);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
