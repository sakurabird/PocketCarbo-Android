package com.sakurafish.pockettoushituryou.viewmodel

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.data.db.entity.orma.Foods
import com.sakurafish.pockettoushituryou.repository.orma.FavoriteFoodsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

enum class HostClass {
    FOODS,
    FAVORITES,
    SEARCH
}

class FoodItemViewModel(
        private val context: Context,
        private val favoriteFoodsRepository: FavoriteFoodsRepository,
        val foods: Foods,
        private val hostClass: HostClass
) : ViewModel() {

    val carboRatedColorResId = liveData {
        val value: Int = when {
            foods.carbohydratePer100g < 5 -> // 糖質量が少ない
                R.color.colorCarboSafe
            foods./**/carbohydratePer100g >= 5 && foods.carbohydratePer100g < 15 -> // 糖質量がやや多い
                R.color.colorCarboWarning
            foods.carbohydratePer100g >= 15 && foods.carbohydratePer100g < 50 -> // 糖質量が多い
                R.color.colorCarboDanger
            else -> // 糖質量が非常に多い
                R.color.colorCarboDangerHigh
        }
        emit(value)
    }

    val name = liveData {
        emit(foods.name)
    }

    val kindName = liveData {
        emit(foods.kinds?.name)
    }

    val carbohydratePer100g = liveData {
        emit(foods.carbohydratePer100g.toString() + " g")
    }

    val cubeSugarPer100 = liveData {
        // 角砂糖換算(100gあたり)
        emit(createCubeSugarString(foods.carbohydratePer100g))
    }

    val fatPer100 = liveData {
        emit(foods.fatPer100g.toString() + " g")
    }

    val expandedTitle = liveData {
        val title = when {
            TextUtils.isEmpty(foods.weightHint) -> context.getString(R.string.expanded_title, foods.weight.toString() + " g")
            else -> {
                val str = foods.weight.toString() + " g" + "(" + foods.weightHint + ")"
                context.getString(R.string.expanded_title, str)
            }
        }
        emit(title)
    }

    val carbohydratePerWeight = liveData {
        emit(foods.carbohydratePerWeight.toString() + " g")
    }

    val calory = liveData {
        emit(foods.calory.toString() + " kcal")
    }

    val protein = liveData {
        emit(foods.protein.toString() + " g")
    }

    val fat = liveData {
        emit(foods.fat.toString() + " g")
    }

    val sodium = liveData {
        emit(foods.sodium.toString() + " g")
    }

    val notes = liveData {
        emit(foods.notes)
    }

    val showNotes = liveData {
        emit(!TextUtils.isEmpty(foods.notes))
    }

    val cubeSugarPerWeight = liveData {
        // 角砂糖換算
        val str = createCubeSugarString(foods.carbohydratePerWeight)
        emit(str)
    }

    private val _isFavState = MutableLiveData<Boolean>()
    val isFavState: LiveData<Boolean> = _isFavState

    private var onClickListener: View.OnClickListener? = null
    var isExpanded: Boolean = false

    private var favAnimatorSet: AnimatorSet? = null

    init {
        refreshFavoriteStatus()
    }

    private fun createCubeSugarString(carbohydrate: Float): String {
        // 1個4gで計算。 小数点第二位で四捨五入
        val cubeNum = carbohydrate / 4
        var cubeString = "0"
        if (cubeNum != 0f) {
            // TODO 1.8/4のようにdoubleで4で割ったときの数が0.449999…のような値のものは,
            // floatで4で割ったときに0.45となる数値でもString.formatで四捨五入すると0.5とならず0.4となってしまう。
            // これを防ぐため一旦10倍して四捨五入し10で割ることにした
            val cubeNumRound = Math.round(cubeNum * 10).toFloat()
            cubeString = String.format(Locale.getDefault(), "%.1f", cubeNumRound / 10)
        }
        return this.context.getString(R.string.conversion_cube_sugar, cubeString)
    }

    fun onClickExpandButton(view: View) {
        if (this.onClickListener != null) this.onClickListener?.onClick(view)
    }

    fun setExpandListener(onClickListener: View.OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun refreshFavoriteStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            _isFavState.postValue(favoriteFoodsRepository.isFavorite(foods.id))
        }
    }

    fun onClickFavButton(view: View) {
        viewModelScope.launch {
            _isFavState.value = updateFavoritesDB()
            animateFavButton(view)
        }
    }

    @WorkerThread
    private suspend fun updateFavoritesDB(): Boolean {
        val result = viewModelScope.async(Dispatchers.IO) {
            if (favoriteFoodsRepository.isFavorite(foods.id)) {
                favoriteFoodsRepository.delete(foods, hostClass)
                return@async false
            } else {
                favoriteFoodsRepository.save(foods, hostClass)
                return@async true
            }
        }
        return result.await()
    }

    private fun animateFavButton(view: View) {
        favAnimatorSet?.cancel()
        view.animate().cancel()
        view.scaleX = 0f
        view.scaleY = 0f

        favAnimatorSet = AnimatorSet()

        val starScaleYAnimator = ObjectAnimator.ofFloat(view, ImageView.SCALE_Y, 0.2f, 1f)
        starScaleYAnimator.duration = 350
        starScaleYAnimator.startDelay = 250
        starScaleYAnimator.interpolator = OVERSHOOT_INTERPOLATOR

        val starScaleXAnimator = ObjectAnimator.ofFloat(view, ImageView.SCALE_X, 0.2f, 1f)
        starScaleXAnimator.duration = 350
        starScaleXAnimator.startDelay = 250
        starScaleXAnimator.interpolator = OVERSHOOT_INTERPOLATOR

        favAnimatorSet?.playTogether(
                starScaleYAnimator,
                starScaleXAnimator
        )

        favAnimatorSet?.start()
    }

    fun onLongClickExpandButton(view: View): Boolean {
        //長押しされた場合クリップボードに内容をコピーする
        val rowString = createRowString()

        val clipboard = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(context.getString(R.string.carbohydrate_amount), rowString)
        clipboard.primaryClip = clip

        Toast.makeText(view.context, context.getString(R.string.text_clipped) + "\n" + rowString, Toast.LENGTH_SHORT).show()

        return true
    }

    private fun createRowString(): String {
        val builder = StringBuilder()
        builder.append("[")
        builder.append(kindName.value)
        builder.append("] ")
        builder.append(name.value?.trim())
        builder.append("100gあたりの糖質量")
        builder.append(":")
        builder.append(carbohydratePer100g.value?.replace(" ", ""))
        builder.append(", ")

        // 同じ100gをコピーしても仕方ないので
        if (foods.weight != 0 && foods.weight != 100) {
            builder.append(expandedTitle.value?.replace(" ", ""))
            builder.append(":")
            builder.append(carbohydratePerWeight.value?.replace(" ", ""))
        }
        builder.append(", カロリー:")
        builder.append(calory.value?.replace(" ", ""))
        builder.append(", たんばく質:")
        builder.append(protein.value?.replace(" ", ""))
        builder.append(", 脂質:")
        builder.append(fat.value?.replace(" ", ""))
        builder.append(", 塩分:")
        builder.append(sodium.value?.replace(" ", ""))
        if (!TextUtils.isEmpty(foods.notes)) {
            builder.append(", 備考:")
            builder.append(notes.value)
        }

        builder.append(" #ポケット糖質量")

        return builder.toString()
    }

    fun onClickShareButton(view: View) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, createRowString())
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        intent.type = "text/plain"
        view.context.startActivity(Intent.createChooser(intent, context.resources.getText(R.string.send_to)))
    }

    companion object {
        internal val TAG = FoodItemViewModel::class.java.simpleName

        private val OVERSHOOT_INTERPOLATOR = OvershootInterpolator(4f)
    }
}
