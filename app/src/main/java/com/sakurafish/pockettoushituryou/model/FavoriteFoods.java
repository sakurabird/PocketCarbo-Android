package com.sakurafish.pockettoushituryou.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

import java.util.Date;

@Table
public class FavoriteFoods {

    @PrimaryKey
    @Column(indexed = true)
    public int id;

    @Column(indexed = true, unique = true)
    public Foods foods;

    @Column
    @Nullable
    public Date createdAt;

    public FavoriteFoods() {
    }

    public FavoriteFoods(@NonNull Foods foods, @NonNull Date createdAt) {
        this.foods = foods;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FavoriteFoods && ((FavoriteFoods) o).id == id || super.equals(o);
    }

    @Override
    public String toString() {
        return "foods_id:" + this.foods.id + " name:" + this.foods.name + " created date:" + this.createdAt.toString();
    }

    @Override
    public int hashCode() {
        return id;
    }
}
