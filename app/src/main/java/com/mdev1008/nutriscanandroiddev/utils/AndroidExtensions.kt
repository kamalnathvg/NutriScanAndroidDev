package com.mdev1008.nutriscanandroiddev.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.data.model.DietaryRestriction
import com.mdev1008.nutriscanandroiddev.data.model.DietaryRestriction.*
import com.mdev1008.nutriscanandroiddev.data.model.HealthCategory
import com.mdev1008.nutriscanandroiddev.data.model.NutrientType

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.hideKeyboard(){
    val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun NutrientType.getIcon(context: Context): Drawable? {
    return when(this){
        NutrientType.ENERGY -> ContextCompat.getDrawable(context,R.mipmap.calories)
        NutrientType.PROTEIN -> ContextCompat.getDrawable(context,R.mipmap.protein)
        NutrientType.SATURATES -> ContextCompat.getDrawable(context,R.mipmap.saturated_fat)
        NutrientType.SUGAR -> ContextCompat.getDrawable(context,R.mipmap.sugar)
        NutrientType.FIBRE -> ContextCompat.getDrawable(context,R.mipmap.fibre)
        NutrientType.SODIUM -> ContextCompat.getDrawable(context,R.mipmap.salt)
        NutrientType.FRUITS_VEGETABLES_AND_NUTS -> ContextCompat.getDrawable(context,R.mipmap.fruits_veggies)
    }
}

fun DietaryRestriction.getIcon(context: Context, isUserRestriction: Boolean): Drawable?{
    return when(this){
        VEGAN -> {
            if (isUserRestriction) {
                ContextCompat.getDrawable(context, R.mipmap.vegan_filled)
            }else{
                ContextCompat.getDrawable(context, R.mipmap.vegan_unfilled)
            }
        }
        NON_VEGAN -> {
            if (isUserRestriction){
                ContextCompat.getDrawable(context, R.mipmap.non_vegan_filled)
            }else{
                ContextCompat.getDrawable(context, R.mipmap.non_vegan_unfilled)
            }
        }
        VEGAN_STATUS_UNKNOWN -> {
                ContextCompat.getDrawable(context, R.mipmap.vegan_status_unknown)
        }
        PALM_OIL -> {
            if (isUserRestriction){
                ContextCompat.getDrawable(context, R.mipmap.contains_palm_oil_unfilled)
            }else{
                ContextCompat.getDrawable(context, R.mipmap.contains_palm_oil_filled)
            }
        }
        PALM_OIL_FREE -> {
            if (isUserRestriction){
                ContextCompat.getDrawable(context, R.mipmap.palm_oil_free_filled)
            }else{
                ContextCompat.getDrawable(context, R.mipmap.palm_oil_free_unfilled)
            }
        }
        PALM_OIL_STATUS_UNKNOWN -> {
            ContextCompat.getDrawable(context, R.mipmap.palm_oil_status_unknown)
        }
        VEGETARIAN -> {
            ContextCompat.getDrawable(context, R.mipmap.vegetarian)
        }
        NON_VEGETARIAN -> {
            ContextCompat.getDrawable(context, R.mipmap.non_vegetarian)
        }
        VEGETARIAN_STATUS_UNKNOWN -> {
            ContextCompat.getDrawable(context, R.mipmap.vegetarian_status_unknown)

        }
    }
}

fun HealthCategory.getIconAndBg(context: Context): Pair<Int,Int>{
    return when(this){
        HealthCategory.HEALTHY,
        HealthCategory.GOOD
        -> Pair(R.drawable.circle_good, ContextCompat.getColor(context, R.color.product_category_good_bg))
        HealthCategory.FAIR
        -> Pair(
            R.drawable.circle_moderate, ContextCompat.getColor(context,
                R.color.product_category_moderate_bg
            ))
        HealthCategory.POOR,
        HealthCategory.BAD
        -> Pair(
            R.drawable.circle_bad, ContextCompat.getColor(context,
                R.color.product_category_bad_bg
            ))
        HealthCategory.UNKNOWN -> Pair(
            R.drawable.circle_unknown, ContextCompat.getColor(context,
                R.color.md_theme_background
            ))
    }
}

fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
){
    val snackbar = Snackbar.make(this,message,duration)
    snackbar.show()
}

fun ImageView.loadFromUrlOrGone(imageUrl: String?, @DrawableRes errorImage: Int? = null){
    if (imageUrl == null && errorImage == null){
        this.visibility = View.GONE
        return
    }
    Glide.with(this)
        .load(imageUrl)
        .error(errorImage)
        .into(this)
}

