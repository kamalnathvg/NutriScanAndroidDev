package com.mdev1008.nutriscanandroiddev.presentation.product_details_page

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.data.model.Allergen
import com.mdev1008.nutriscanandroiddev.data.model.DietaryRestriction
import com.mdev1008.nutriscanandroiddev.data.model.NutrientCategory
import com.mdev1008.nutriscanandroiddev.databinding.FragmentProductDetailsPageBinding
import com.mdev1008.nutriscanandroiddev.domain.model.MainDetailsForView
import com.mdev1008.nutriscanandroiddev.domain.model.NutrientForView
import com.mdev1008.nutriscanandroiddev.domain.model.ProductDetailsForView
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.getIcon
import com.mdev1008.nutriscanandroiddev.utils.getIconAndBg
import com.mdev1008.nutriscanandroiddev.utils.infoLogger
import com.mdev1008.nutriscanandroiddev.utils.loadFromUrlOrGone
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.launch

class ProductDetailsPage : Fragment() {

    private val viewModel by activityViewModels<ProductDetailsViewModel> {
        ProductDetailsViewModel.Factory
    }
    private lateinit var viewBinding: FragmentProductDetailsPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProductDetailsPageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productId = arguments?.getString("productId")
        Log.d("logger","$productId")
        viewModel.onEvent(ProductDetailsPageEvent.GetUserDetails)
        NavigationUI.setupWithNavController(viewBinding.mtbProductDetailsPage,findNavController())
        viewBinding.mtbProductDetailsPage.title = getString(R.string.product_details)
        viewBinding.mtbProductDetailsPage.setTitleTextColor(Color.WHITE)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{ state ->
                    when(state.userDetailsFetchState){
                        Status.LOADING -> {
                            Log.d("logger","Loading user details")

                        }
                        Status.SUCCESS -> {
                            Log.d("logger","fetch product with id : $productId")
                            Log.d("logger", "${state.userProfileDetails?.userAllergen}.toString()")

                            productId?.let {
                                viewModel.onEvent(ProductDetailsPageEvent.GetProductDetailsById(productId))
                            }
                        }
                        Status.FAILURE -> {

                            Log.d("logger","failure user details")
                        }
                        Status.IDLE -> {
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{ state ->
                    when(state.productDetailsFetchState){
                        Status.LOADING -> {
                            infoLogger("Loading product details")
                        }
                        Status.SUCCESS -> {
                            state.productDetailsForView?.let {
                                val userAllergen = state.userProfileDetails?.userAllergen?.map { it.allergen } ?: emptyList()
                                buildProductDetailsUi(it, userAllergen)
                            }
                        }
                        Status.FAILURE -> {
                            view.showSnackBar(state.errorMessage.toString())
                        }
                        Status.IDLE -> {}
                    }
                }
            }
        }
    }

    private fun buildProductDetailsUi(product: ProductDetailsForView, userAllergens: List<Allergen>) {
        Log.d("logger", "${product.mainDetailsForView.allergens}, ${userAllergens}")
        buildMainHeaderView(product.mainDetailsForView)
        buildAllergensView(product.mainDetailsForView.allergens, userAllergens)
        product.nutrients.let { nutrientsForView ->
            val negativeNutrients =  nutrientsForView.filter {
                it.nutrientCategory == NutrientCategory.NEGATIVE
            }
            if (negativeNutrients.isNotEmpty()){
                buildNutrientsView(negativeNutrients, NutrientCategory.NEGATIVE)
            }

            val positiveNutrients = nutrientsForView.filter {
                it.nutrientCategory == NutrientCategory.POSITIVE
            }
            if (positiveNutrients.isNotEmpty()){
                buildNutrientsView(positiveNutrients, NutrientCategory.POSITIVE)
            }

            if (positiveNutrients.isEmpty() && negativeNutrients.isEmpty()){
                buildNoDetailsAvailableView()
            }
        }
    }

    private fun buildAllergensView(productAllergen: List<Allergen>, userAllergens: List<Allergen>) {
        Log.d("logger", userAllergens.toString())
        if (productAllergen.isEmpty()){
            return
        }
        val allergensView = LayoutInflater.from(requireContext()).inflate(R.layout.component_food_considerations, viewBinding.llProductDetailsLayout, false)
        val clAllergensLayout = allergensView.findViewById<ConstraintLayout>(R.id.cl_allergens_layout)
        val flAllergensLayout = allergensView.findViewById<Flow>(R.id.fl_allergens_layout)
        productAllergen.forEach { allergen ->
            val button = ToggleButton(requireContext())
            button.apply {
                background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.allergen_selector
                )
                isAllCaps = false
                textOn = allergen.heading
                textOff = allergen.heading
                text = allergen.heading
                isChecked = allergen in userAllergens
                isClickable = false
                id = View.generateViewId()
            }
            button.setTextColor(
                resources.getColor(
                    if (button.isChecked) R.color.md_theme_onPrimary else R.color.md_theme_onBackground
                )
            )
            val buttonIds = flAllergensLayout.referencedIds.toMutableList()
            buttonIds.add(button.id)
            flAllergensLayout.referencedIds = buttonIds.toIntArray()
            clAllergensLayout.addView(button)
        }
        viewBinding.llProductDetailsLayout.addView(allergensView)
    }

    private fun buildNoDetailsAvailableView() {
        val itemView = LayoutInflater.from(requireContext()).inflate(R.layout.component_no_details_text_view, viewBinding.llProductDetailsLayout, false)
        itemView.findViewById<TextView>(R.id.tv_no_details).text = getString(R.string.no_nutrient_details_available_for_the_product)
        viewBinding.llProductDetailsLayout.addView(itemView)
    }

    private fun buildNutrientsView(nutrients: List<NutrientForView>, nutrientCategory: NutrientCategory) {
        val headerView = LayoutInflater.from(requireContext())
            .inflate(R.layout.component_pdp_nutrients_view_header, viewBinding.llProductDetailsLayout, false)
        headerView.findViewById<TextView>(R.id.tv_nutrients_header).text = nutrientCategory.header
        headerView.findViewById<TextView>(R.id.serving_quantity).text = getString(R.string.serving_per_100gm)
        viewBinding.llProductDetailsLayout.addView(headerView)

        nutrients.forEach {nutrient ->
            val nutrientItemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.component_pdp_nutrient_item, viewBinding.llProductDetailsLayout, false)
            nutrientItemView.findViewById<ImageView>(R.id.nutrient_icon).setImageDrawable(nutrient.nutrientType.getIcon(requireContext()))
            nutrientItemView.findViewById<TextView>(R.id.tv_nutrient_name).text = nutrient.nutrientType.heading
            nutrientItemView.findViewById<TextView>(R.id.tv_nutrient_description).text = nutrient.description
            val (healthCategoryIcon, _) = nutrient.healthCategory.getIconAndBg(requireContext())
            nutrientItemView.findViewById<ImageView>(R.id.nutrient_category_icon).setImageResource(healthCategoryIcon)
            val contentText = nutrient.contentPerHundredGrams.toString() + " " + nutrient.servingUnit
            nutrientItemView.findViewById<TextView>(R.id.tv_per_hundred_gram).text = contentText
            viewBinding.llProductDetailsLayout.addView(nutrientItemView)
        }
    }

    private fun buildMainHeaderView(mainDetailsForView: MainDetailsForView) {
//        val conclusions = viewModel.uiState.value.userPreferencesConclusion
        val mainHeaderView = LayoutInflater.from(requireContext())
            .inflate(R.layout.component_pdp_main_details_view, viewBinding.llProductDetailsLayout, false)
        mainHeaderView.findViewById<ImageView>(R.id.iv_product_image).loadFromUrlOrGone(mainDetailsForView.imageUrl)
        mainHeaderView.findViewById<TextView>(R.id.tv_product_name).text = mainDetailsForView.productName
        mainHeaderView.findViewById<TextView>(R.id.tv_product_brand).text = mainDetailsForView.productBrand
        mainHeaderView.findViewById<TextView>(R.id.tv_product_health_grade).text = mainDetailsForView.healthCategory.description
        mainHeaderView.findViewById<ImageView>(R.id.iv_pdp_palm_oil_status).setImageDrawable(
            mainDetailsForView.palmOilStatus.getIcon(
                requireContext(),
                mainDetailsForView.palmOilStatus != DietaryRestriction.PALM_OIL_STATUS_UNKNOWN
            )
        )
        mainHeaderView.findViewById<ImageView>(R.id.iv_pdp_vegan_status).setImageDrawable(
            mainDetailsForView.veganStatus.getIcon(
                requireContext(),
                mainDetailsForView.veganStatus != DietaryRestriction.VEGAN_STATUS_UNKNOWN
            )
        )
        mainHeaderView.findViewById<ImageView>(R.id.iv_pdp_vegetarian_status).setImageDrawable(
            mainDetailsForView.vegetarianStatus.getIcon(
                requireContext(),
                mainDetailsForView.vegetarianStatus != DietaryRestriction.VEGETARIAN_STATUS_UNKNOWN
            )
        )
        val (healthCategoryIcon, cardBgColor) = mainDetailsForView.healthCategory.getIconAndBg(requireContext())
        mainHeaderView.findViewById<CardView>(R.id.cv_product_health).setCardBackgroundColor(cardBgColor)
        mainHeaderView.findViewById<ImageView>(R.id.iv_product_health_icon).setImageResource(healthCategoryIcon)
        viewBinding.llProductDetailsLayout.addView(mainHeaderView)
    }
}