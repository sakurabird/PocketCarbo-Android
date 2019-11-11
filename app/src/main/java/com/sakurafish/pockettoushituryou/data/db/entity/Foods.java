package com.sakurafish.pockettoushituryou.data.db.entity;

import androidx.annotation.Nullable;

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

    @Column
    @Nullable
    @SerializedName("notes")
    public String notes;

    @Column(indexed = true)
    @SerializedName("type_id")
    public int typeId;

    @Column(indexed = true)
    @SerializedName("kind_id")
    public int kindId;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Foods id:");
        builder.append(id);
        builder.append(" name:");
        builder.append(name);
        builder.append(" weight:");
        builder.append(weight);
        builder.append(" weightHint:");
        builder.append(weightHint);
        builder.append(" carbohydratePer100g:");
        builder.append(carbohydratePer100g);
        builder.append(" carbohydratePerWeight:");
        builder.append(carbohydratePerWeight);
        builder.append(" calory:");
        builder.append(calory);
        builder.append(" protein:");
        builder.append(protein);
        builder.append(" fat:");
        builder.append(fat);
        builder.append(" sodium:");
        builder.append(sodium);
        builder.append(" searchWord:");
        builder.append(searchWord);
        builder.append(" notes:");
        builder.append(notes);
        builder.append(" typeId:");
        builder.append(typeId);
        builder.append(" kindId:");
        builder.append(kindId);

        return  builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Foods && ((Foods) o).id == id || super.equals(o);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
