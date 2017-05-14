package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.model.Foods;

public class FoodViewModel extends BaseObservable {

    private Context context;
    private View.OnClickListener onClickListener;

    private String name;
    private String carbohydrate_per_100g;
    private String expanded_title;
    private String carbohydrate_per_weight;
    private String calory;
    private String protein;
    private String fat;
    private String sodium;
    private boolean expanded = false;
    @ColorRes
    private int carboRatedColorResId = R.color.black_alpha_87;

    FoodViewModel(@NonNull Context context, @NonNull Foods foods) {
        this.context = context;
        setViewValues(foods);
    }

    private void setViewValues(@NonNull Foods foods) {
        this.name = foods.name;
        this.carbohydrate_per_100g = String.valueOf(foods.carbohydrate_per_100g) + " g";

        setExpanded(false);
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

        if (foods.carbohydrate_per_100g < 5) {
            // 糖質量が少ない
            this.carboRatedColorResId = R.color.colorCarboSafe;
        } else if (foods.carbohydrate_per_100g >= 5 && foods.carbohydrate_per_100g < 15) {
            // 糖質量がやや多い
            this.carboRatedColorResId = R.color.colorCarboWarning;
        } else if (foods.carbohydrate_per_100g >= 15 && foods.carbohydrate_per_100g < 50) {
            // 糖質量が多い
            this.carboRatedColorResId = R.color.colorCarboDanger;
        } else {
            // 糖質量が非常に多い
            this.carboRatedColorResId = R.color.colorCarboDangerHigh;
        }

    }

    public String getName() {
        return name;
    }

    public String getCarbohydrate_per_100g() {
        return carbohydrate_per_100g;
    }

    public String getExpanded_title() {
        return expanded_title;
    }

    public String getCarbohydrate_per_weight() {
        return carbohydrate_per_weight;
    }

    public String getCalory() {
        return calory;
    }

    public String getProtein() {
        return protein;
    }

    public String getFat() {
        return fat;
    }

    public String getSodium() {
        return sodium;
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

    public int getCarboRatedColorResId() {
        return carboRatedColorResId;
    }
}
