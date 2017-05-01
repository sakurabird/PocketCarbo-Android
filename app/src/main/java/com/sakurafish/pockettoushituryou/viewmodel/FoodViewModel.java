package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.model.FoodsData;

public class FoodViewModel extends BaseObservable {

    public String name;
    public String carbohydrate_per_100g;
    public String expanded_title;
    public String carbohydrate_per_weight;
    public String calory;
    public String protein;
    public String fat;
    public String sodium;

    private Context context;
    private View.OnClickListener onClickListener;
    private boolean expanded = false;

    FoodViewModel(@NonNull Context context, @NonNull FoodsData.Foods foods) {
        this.context = context;
        setViewValues(foods);
    }

    private void setViewValues(@NonNull FoodsData.Foods foods) {
        this.name = foods.name;
        this.carbohydrate_per_100g = String.valueOf(foods.carbohydrate_per_100g) + " g";
        if (TextUtils.isEmpty(foods.weight_hint)) {
            this.expanded_title = this.context.getString(R.string.expanded_title, String.valueOf(foods.weight) + " g");
        } else {
            String str = foods.weight + " g" + "(" + foods.weight_hint + ")";
            this.expanded_title = this.context.getString(R.string.expanded_title, str);
        }
        this.carbohydrate_per_weight = String.valueOf(foods.carbohydrate_per_weight) + " g";
        this.calory = String.valueOf(foods.calory) + " kcal";
        this.protein = String.valueOf(foods.protein) + " g";
        this.fat = String.valueOf(foods.fat) + " g";
        this.sodium = String.valueOf(foods.sodium) + " g";
    }

    public void onClickExpandButton(View view) {
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    public void setOnClickListener(@NonNull View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return this.expanded;
    }
}
