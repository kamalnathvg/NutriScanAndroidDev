package com.mdev1008.nutriscanandroiddev.utils

import java.text.DecimalFormat

class AppResources {
    companion object{

        const val BASE_URL = "https://world.openfoodfacts.org/api/v2/"
        const val UNKNOWN_HOST_EXCEPTION_MESSAGE ="Unable to Establish Connection"
        const val PRODUCT_NOT_FOUND_MESSAGE = "Product Not Found In the Database"
        const val UNKNOWN_ERROR_MESSAGE = "Unknown Error Occurred"
        const val CONNECTION_TIMEOUT_MESSAGE = "Connection Timed Out"
        const val LOGGER_TAG = "logger"

        private val PRODUCT_DETAILS_FIELDS = listOf(
            "code",
            "product_name",
            "image_url",
            "nutriments",
            "ingredients",
            "nutriscore_data",
            "brands",
            "nutriscore_grade",
            "categories_hierarchy",
            "allergens_hierarchy",
            "ingredients_analysis_tags",
            )
        private val ALLERGEN_FREE = listOf(
            "en:none" ,
            "en:allergens-free",
            "en:without allergens",
            "en:without",
            "en:n",
            "en:no",
            "en:no allergens",
            "en:0"
        )
        val GLUTEN_ALLERGENS = listOf(
            "en:gluten",
            "en:cereals containing gluten",
            "en:other cereals containing gluten",
            "en:barley",
            "en:barley malt flour",
            "en:malted barley",
            "en:malted barley extract",
            "en:malted barley flour",
            "en:kamut",
            "en:rye",
            "en:rye flour",
            "en:spelt",
            "en:speltflour",
            "en:wheat",
            "en:wheat flour",
            "en:wheatflour",
            "en:wheat semolina",
            "en:oats",
            "en:oat fiber",
        )
        val CRUSTACEANS_ALLERGENS = listOf(
            "en:crustaceans",
            "en:crab",
            "en:lobster",
            "en:crayfish",
            "en:prawn",
            "en:shrimp",
        )
        val EGG_ALLERGENS = listOf(
            "en:eggs",
            "en:egg",
            "en:barn egg",
            "en:egg whites",
            "en:egg white",
            "en:egg yolks",
            "en:egg yolk",
            "en:whole eggs",
            "en:whole egg"
        )
        val FISH_ALLERGENS = listOf(
            "en:fish",
            "en:fishes",
            "en:cod",
            "en:mackerel",
            "en:flounder",
            "en:halibut",
            "en:turbot",
            "en:haddock",
            "en:salmon",
            "en:sole",
            "en:trout",
            "en:tuna",
            "en:sardine",
            "en:sardines"
        )
        val PEANUT_ALLERGENS = listOf(
            "en:peanuts",
            "en:peanut",
            "en:arachis hypogaea",
        )
        val SOY_ALLERGENS = listOf(
            "en:soybeans",
            "en:soya",
            "en:soja",
            "en:soia",
            "en:soy",
            "en:soya bean",
            "en:soy flour",
            "en:soya flour",
            "en:soy lecithin",
            "en:soy lecithins",
            "en:soya lecithin",
            "en:soya lecithins",
            "en:soy lecithines",
            "en:soy protein isolate",
            "en:soya products"
        )
        val MILK_ALLERGENS = listOf(
            "en:milk",
            "en:lactose",
            "en:whey",
            "en:dairy",
            "en:butter",
            "en:buttermilk",
            "en:cream",
            "en:yogurt",
            "en:cheese",
            "en:yoghurt",
            "en:parmigiano reggiano",
            "en:grana padano",
            "en:milk chocolate coating",
            "en:milk powder",
            "en:milk protein",
        )
        val NUTS_ALLERGENS = listOf(
            "en:nuts",
            "en:almonds",
            "en:hazelnuts",
            "en:walnuts",
            "en:cashews",
            "en:cashew",
            "en:pecan nuts",
            "en:pecan",
            "en:Brazil nuts",
            "en:pistachio nuts",
            "en:pistachio",
            "en:macadamia",
            "en:Macadamia nuts",
            "en:Queensland nuts",
            "en:tree nuts",
            "en:treenuts",
            "en:other nuts",
            "en:other tree nuts"
        )
        val CELERY_ALLERGENS = listOf(
            "en:celery",
            "en:celeriac"
        )
        val MUSTARD_ALLERGENS = listOf(
            "en:mustard",
            "en:brassica"
        )
        val SESAME_ALLERGENS = listOf(
            "en:sesame seeds",
            "en:sesame"
        )
        val SULPHUR_DIOXIDE_AND_SULPHIDE_ALLERGENS = listOf(
            "en:sulphur dioxide and sulphites",
            "en:sulphur dioxide",
            "en:sulphites",
            "en:sulfites"
        )
        val LUPIN_ALLERGENS = listOf(
            "en:lupin",
            "en:lupine",
        )
        val MOLLUSCS_ALLERGENS = listOf(
            "en:molluscs",
            "en:mollusc",
            "en:mollusks",
            "en:mollusk",
            "en:squid",
            "en:cuttlefish",
            "en:oysters",
            "en:oyster",
            "en:mussels",
            "en:mussel",
            "en:clams",
            "en:clam",
            "en:scallops",
            "en:scallop",
        )
        val RED_CAVIAR_ALLERGENS = listOf(
            "en:red caviar"
        )
        val ORANGE_ALLERGENS = listOf(
            "en:orange"
        )
        val KIWI_ALLERGENS = listOf(
            "en:kiwi"
        )
        val BANANA_ALLERGENS = listOf(
            "en:banana"
        )
        val PEACH_ALLERGENS = listOf(
            "en:peach"
        )
        val APPLE_ALLERGENS = listOf(
            "en:apple"
        )
        val BEEF_ALLERGENS = listOf(
            "en:beef"
        )
        val PORK_ALLERGENS = listOf(
            "en:pork"
        )
        val CHICKEN_ALLERGENS = listOf(
            "en:chicken"
        )
        val GELATIN_ALLERGENS = listOf(
            "en:gelatin"
        )
        val YAMAIMO_ALLERGENS = listOf(
            "en:yamaimo"
        )
        val MATSUTAKE_ALLERGENS = listOf(
            "en:matsutake"
        )
        fun getProductDetailsFields(): String{
            return PRODUCT_DETAILS_FIELDS.joinToString(",")
        }
    }
}

object DemoItems{
    private const val DEMO_ITEM_ID = "3017620422003"
    private const val BHUJJIA = "011433157933"
    private const val SEASONED_PEANUTS = "059966050932"
    private const val ALOO_LACHHA = "8904004402858"
    private const val COCA_COLA_CAN = "5449000256805"

    private val items = listOf(
        DEMO_ITEM_ID,
        BHUJJIA,
        SEASONED_PEANUTS,
        ALOO_LACHHA,
        COCA_COLA_CAN,
        "20724696",
        "3046920028004",
        "3168930009078",
        "87157277",
        "7622300489434",
    )

    fun getRandomItem(): String{
        return items.random()
    }
}

fun Double.formatForView(): Double{
    val decimalFormat = DecimalFormat("#.0")
    return decimalFormat.format(this).toDouble()
}