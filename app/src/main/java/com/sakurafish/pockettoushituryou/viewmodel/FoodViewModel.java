package com.sakurafish.pockettoushituryou.viewmodel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.like.LikeButton;
import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.model.Foods;
import com.sakurafish.pockettoushituryou.repository.FavoriteFoodsRepository;

import java.util.Locale;

import timber.log.Timber;

public class FoodViewModel extends BaseObservable {
    final static String TAG = FoodViewModel.class.getSimpleName();

    private Context context;
    private AppCompatActivity activity;
    private View.OnClickListener onClickListener;
    private FavoriteFoodsRepository favoriteFoodsRepository;

    private Foods foods;
    private String name;
    private String kindName;
    private String carbohydratePer100g;
    private String cubeSugarPer100;
    private String expandedTitle;
    private String carbohydratePerWeight;
    private String calory;
    private String protein;
    private String fat;
    private String sodium;
    private String notes;
    private int notesVisibility;
    private String cubeSugarPerWeight;

    private boolean expanded = false;
    private boolean favState = false;
    @ColorRes
    private int carboRatedColorResId = R.color.black_alpha_87;

    FoodViewModel(@NonNull Context context,
                  @NonNull AppCompatActivity activity,
                  @NonNull FavoriteFoodsRepository favoriteFoodsRepository,
                  @NonNull Foods foods,
                  @NonNull String kindName) {
        this.context = context;
        this.activity = activity;
        this.favoriteFoodsRepository = favoriteFoodsRepository;
        this.kindName = kindName;

        setViewValues(foods);
    }

    private void setViewValues(@NonNull Foods foods) {
        this.foods = foods;

        this.name = foods.name;
        this.carbohydratePer100g = String.valueOf(foods.carbohydratePer100g) + " g";
        // 角砂糖換算(100gあたり)
        this.cubeSugarPer100 = createCubeSugarString(foods.carbohydratePer100g);

        setExpanded(false);
        if (TextUtils.isEmpty(foods.weightHint)) {
            this.expandedTitle = this.context.getString(R.string.expanded_title, String.valueOf(foods.weight) + " g");
        } else {
            String str = foods.weight + " g" + "(" + foods.weightHint + ")";
            this.expandedTitle = this.context.getString(R.string.expanded_title, str);
        }

        this.carbohydratePerWeight = String.valueOf(foods.carbohydratePerWeight) + " g";
        this.calory = String.valueOf(foods.calory) + " kcal";
        this.protein = String.valueOf(foods.protein) + " g";
        this.fat = String.valueOf(foods.fat) + " g";
        this.sodium = String.valueOf(foods.sodium) + " g";
        this.notes = foods.notes;
        setNotesVisibility(TextUtils.isEmpty(foods.notes) ? View.GONE : View.VISIBLE);

        // 角砂糖換算
        this.cubeSugarPerWeight = createCubeSugarString(foods.carbohydratePerWeight);

        if (foods.carbohydratePer100g < 5) {
            // 糖質量が少ない
            this.carboRatedColorResId = R.color.colorCarboSafe;
        } else if (foods.carbohydratePer100g >= 5 && foods.carbohydratePer100g < 15) {
            // 糖質量がやや多い
            this.carboRatedColorResId = R.color.colorCarboWarning;
        } else if (foods.carbohydratePer100g >= 15 && foods.carbohydratePer100g < 50) {
            // 糖質量が多い
            this.carboRatedColorResId = R.color.colorCarboDanger;
        } else {
            // 糖質量が非常に多い
            this.carboRatedColorResId = R.color.colorCarboDangerHigh;
        }
        setFabState();
    }

    private String createCubeSugarString(float carbohydrate) {
        // 1個4gで計算。 小数点第二位で四捨五入
        float cubeNum = carbohydrate / 4;
        String cubeString = "0";
        if (cubeNum != 0) {
            // TODO 1.8/4のようにdoubleで4で割ったときの数が0.449999…のような値のものは,
            // floatで4で割ったときに0.45となる数値でもString.formatで四捨五入すると0.5とならず0.4となってしまう。
            // これを防ぐため一旦10倍して四捨五入し10で割ることにした
            float cubeNumRound = Math.round(cubeNum * 10);
            cubeString = String.format(Locale.getDefault(), "%.1f", cubeNumRound / 10);
        }
        return this.context.getString(R.string.conversion_cube_sugar, cubeString);
    }

    public String getName() {
        return name;
    }

    public String getKindName() {
        return kindName;
    }

    public String getCarbohydratePer100g() {
        return carbohydratePer100g;
    }

    public String getCubeSugarPer100() {
        return cubeSugarPer100;
    }

    public String getExpandedTitle() {
        return expandedTitle;
    }

    public String getCarbohydratePerWeight() {
        return carbohydratePerWeight;
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

    public String getNotes() {
        return notes;
    }

    public int getNotesVisibility() {
        return notesVisibility;
    }

    public void setNotesVisibility(int visibility) {
        this.notesVisibility = visibility;
    }

    public String getCubeSugarPerWeight() {
        return cubeSugarPerWeight;
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
        this.favState = favoriteFoodsRepository.isFavorite(foods.id);
    }

    public void setFabButtonState(LikeButton likeButton) {
        likeButton.setLiked(this.favState);
    }

    public void onClickFab() {
        if (favoriteFoodsRepository.isFavorite(foods.id)) {
            favoriteFoodsRepository.delete(foods)
                    .subscribe((result) -> Timber.tag(TAG).d("Deleted favorite food"),
                            throwable -> Timber.tag(TAG).e(throwable, "Failed to delete favorite food"));
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
        String rowString = createRowString();

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.carbohydrate_amount), rowString);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, context.getString(R.string.text_clipped) + "\n" + rowString, Toast.LENGTH_SHORT).show();

        return true;
    }

    @NonNull
    private String createRowString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(kindName);
        builder.append("] ");
        builder.append(getName().trim());
        builder.append("100gあたりの糖質量");
        builder.append(":");
        builder.append(getCarbohydratePer100g().replace(" ", ""));
        builder.append(", ");

        // 同じ100gをコピーしても仕方ないので
        if (foods.weight != 0 && foods.weight != 100) {
            builder.append(getExpandedTitle().replace(" ", ""));
            builder.append(":");
            builder.append(getCarbohydratePerWeight().replace(" ", ""));
        }
        builder.append(", カロリー:");
        builder.append(getCalory().replace(" ", ""));
        builder.append(", たんばく質:");
        builder.append(getProtein().replace(" ", ""));
        builder.append(", 脂質:");
        builder.append(getFat().replace(" ", ""));
        builder.append(", 塩分:");
        builder.append(getSodium().replace(" ", ""));
        builder.append(" #ポケット糖質量");

        return builder.toString();
    }

    public void onClickShareButton(View view) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, createRowString());
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.setType("text/plain");
        activity.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.send_to)));
    }
}
