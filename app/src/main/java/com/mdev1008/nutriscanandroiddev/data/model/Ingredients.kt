package com.mdev1008.nutriscanandroiddev.data.model

import com.google.gson.annotations.SerializedName

data class Ingredients(
    @SerializedName("ciqual_food_code")
    val ciqualFoodCode: String? = null,
    @SerializedName("ciqual_proxy_food_code")
    val ciqualProxyFoodCode: String? = null,
    @SerializedName("from_palm_oil")
    val fromPalmOil: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("ingredients")
    val ingredients: List<Ingredients?>? = null,
    @SerializedName("is_in_taxonomy")
    val isInTaxonomy: Int? = null,
    @SerializedName("percent")
    val percent: Double? = null,
    @SerializedName("percent_estimate")
    val percentEstimate: Double? = null,
    @SerializedName("percent_max")
    val percentMax: Double? = null,
    @SerializedName("percent_min")
    val percentMin: Double? = null,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("vegan")
    val vegan: String? = null,
    @SerializedName("vegetarian")
    val vegetarian: String? = null
)