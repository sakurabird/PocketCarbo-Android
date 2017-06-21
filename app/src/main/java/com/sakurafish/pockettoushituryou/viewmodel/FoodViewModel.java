package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.like.LikeButton;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.model.Foods;
import com.sakurafish.pockettoushituryou.repository.FavoriteFoodsRepository;

import timber.log.Timber;

public class FoodViewModel extends BaseObservable {
    final static String TAG = FoodViewModel.class.getSimpleName();

    private Context context;
    private View.OnClickListener onClickListener;
    private FavoriteFoodsRepository favoriteFoodsRepository;

    private Foods foods;
    private String name;
    private String carbohydrate_per_100g;
    private String expanded_title;
    private String carbohydrate_per_weight;
    private String calory;
    private String protein;
    private String fat;
    private String sodium;
    private boolean expanded = false;
    private boolean favState = false;
    @ColorRes
    private int carboRatedColorResId = R.color.black_alpha_87;

    FoodViewModel(@NonNull Context context, @NonNull FavoriteFoodsRepository favoriteFoodsRepository, @NonNull Foods foods) {
        this.context = context;
        this.favoriteFoodsRepository = favoriteFoodsRepository;
        setViewValues(foods);
    }

    private void setViewValues(@NonNull Foods foods) {
        this.foods = foods;
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
        setFabState();
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

    public void setFabState() {
//        if (favoriteFoodsRepository.isFavorite(foods.id)) {
//            Timber.tag(TAG).d("setFabState :" + favoriteFoodsRepository.toString(foods.id));
//        }
        this.favState = favoriteFoodsRepository.isFavorite(foods.id);
    }

    public void setFabButtonState(LikeButton likeButton) {
        likeButton.setLiked(this.favState);
    }

    public void onClickFab() {
        if (favoriteFoodsRepository.isFavorite(foods.id)) {
            favoriteFoodsRepository.delete(foods)
                    .subscribe((result) -> Timber.tag(TAG).d("Deleted favorite food"),
                            throwable -> Timber.tag(TAG).e(throwable, "Failed to delete favorite food id:" + foods.id));
            this.favState = false;
        } else {
            favoriteFoodsRepository.save(foods)
                    .subscribe(() -> Timber.tag(TAG).d("Saved favorite food"),
                            throwable -> Timber.tag(TAG).e(throwable, "Failed to save favorite food"));
            this.favState = true;
        }
    }

    public boolean onLongClickExpandButton(View view) {
        //長押しされた場合クリップボードに内容をコピーする
        StringBuilder builder = new StringBuilder();
        builder.append(getName().trim());
        builder.append("100gあたりの糖質量");
        builder.append(":");
        builder.append(getCarbohydrate_per_100g());
        builder.append(" , ");
        builder.append(expanded_title);
        builder.append(":");
        builder.append(carbohydrate_per_weight);
        builder.append(", カロリー:");
        builder.append(calory);
        builder.append(", たんばく質:");
        builder.append(protein);
        builder.append(", 脂質:");
        builder.append(fat);
        builder.append(", 塩分:");
        builder.append(sodium);

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.carbohydrate_amount), builder.toString());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, context.getString(R.string.text_clipped) + "\n" + builder.toString(), Toast.LENGTH_SHORT).show();

        return true;
    }
}
