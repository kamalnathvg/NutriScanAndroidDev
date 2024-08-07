package com.mdev1008.nutriscanandroiddev.pages.productdetailspage

import Product
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.style.TextAlign
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentProductDetailsPageBinding
import com.mdev1008.nutriscanandroiddev.models.data.MainDetailsForView
import com.mdev1008.nutriscanandroiddev.models.data.NutrientCategory
import com.mdev1008.nutriscanandroiddev.models.data.NutrientForView
import com.mdev1008.nutriscanandroiddev.pages.homepage.HomePageViewModel
import com.mdev1008.nutriscanandroiddev.pages.homepage.HomePageViewModelFactory
import com.mdev1008.nutriscanandroiddev.utils.getIcon
import com.mdev1008.nutriscanandroiddev.utils.getIconAndBg
import com.mdev1008.nutriscanandroiddev.utils.loadFromUrlOrGone
import getMainDetailsForView
import com.mdev1008.nutriscanandroiddev.utils.logger
import com.mdev1008.nutriscanandroiddev.utils.show
import getNutrientsForView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsPage : Fragment() {

    private val viewModel by activityViewModels<HomePageViewModel> {
        val nutriScanApplication = requireActivity().application as NutriScanApplication
        HomePageViewModelFactory(
            apiRepository = nutriScanApplication.apiRepository,
            dbRepository = nutriScanApplication.dbRepository
        )
    }
    private lateinit var viewBindingLayout: FragmentProductDetailsPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBindingLayout = FragmentProductDetailsPageBinding.inflate(inflater, container, false)
        return viewBindingLayout.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavigationUI.setupWithNavController(viewBindingLayout.mtbProductDetailsPage,findNavController())
        viewBindingLayout.mtbProductDetailsPage.title = getString(R.string.product_details)
        viewBindingLayout.mtbProductDetailsPage.setTitleTextColor(Color.WHITE)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.uiState.collect{state ->
                state.product?.let { product ->
                    logger(product.productName)
                    buildProductDetailsUi(product)
                }
            }
        }
    }

    private fun buildProductDetailsUi(product: Product) {
        buildMainHeaderView(product.getMainDetailsForView())
        product.getNutrientsForView()?.let { nutrientsForView ->
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

    private fun buildNoDetailsAvailableView() {
        val itemView = LayoutInflater.from(requireContext()).inflate(R.layout.component_no_details_text_view, viewBindingLayout.llProductDetailsLayout, false)
        itemView.findViewById<TextView>(R.id.tv_no_details).text = getString(R.string.no_nutrient_details_available_for_the_product)
        viewBindingLayout.llProductDetailsLayout.addView(itemView)
    }

    private fun buildNutrientsView(nutrients: List<NutrientForView>, nutrientCategory: NutrientCategory) {
        val headerView = LayoutInflater.from(requireContext())
            .inflate(R.layout.component_pdp_nutrients_view_header, viewBindingLayout.llProductDetailsLayout, false)
        headerView.findViewById<TextView>(R.id.tv_nutrients_header).text = nutrientCategory.header
        headerView.findViewById<TextView>(R.id.serving_quantity).text = getString(R.string.serving_per_100gm)
        viewBindingLayout.llProductDetailsLayout.addView(headerView)

        nutrients.forEach {nutrient ->
            val nutrientItemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.component_pdp_nutrient_item, viewBindingLayout.llProductDetailsLayout, false)
            nutrientItemView.findViewById<ImageView>(R.id.nutrient_icon).setImageDrawable(nutrient.nutrientType.getIcon(requireContext()))
            nutrientItemView.findViewById<TextView>(R.id.tv_nutrient_name).text = nutrient.nutrientType.heading
            nutrientItemView.findViewById<TextView>(R.id.tv_nutrient_description).text = nutrient.description
            val (healthCategoryIcon, _) = nutrient.healthCategory.getIconAndBg(requireContext())
            nutrientItemView.findViewById<ImageView>(R.id.nutrient_category_icon).setImageResource(healthCategoryIcon)
            nutrientItemView.findViewById<TextView>(R.id.tv_per_hundred_gram).text = "${nutrient.contentPerHundredGrams} ${nutrient.servingUnit}"
            viewBindingLayout.llProductDetailsLayout.addView(nutrientItemView)
        }
    }

    private fun buildMainHeaderView(mainDetailsForView: MainDetailsForView) {
        val conclusions = viewModel.uiState.value.userPreferencesConclusion
        val mainHeaderView = LayoutInflater.from(requireContext())

            .inflate(R.layout.component_pdp_main_details_view, viewBindingLayout.llProductDetailsLayout, false)
        mainHeaderView.findViewById<ImageView>(R.id.iv_product_image).loadFromUrlOrGone(mainDetailsForView.imageUrl)
        mainHeaderView.findViewById<TextView>(R.id.tv_product_name).text = mainDetailsForView.productName
        mainHeaderView.findViewById<TextView>(R.id.tv_product_brand).text = mainDetailsForView.productBrand
        mainHeaderView.findViewById<TextView>(R.id.tv_product_health_grade).text = mainDetailsForView.healthCategory?.description
        mainHeaderView.findViewById<TextView>(R.id.tv_dietary_preference_conclusion).text = conclusions.userDietaryPreferenceConclusion
        mainHeaderView.findViewById<TextView>(R.id.tv_dietary_restriction_conclusion).text = conclusions.userDietaryRestrictionConclusion
        mainHeaderView.findViewById<TextView>(R.id.tv_allergen_conclusion).text = conclusions.userAllergenConclusion
        val (healthCategoryIcon, cardBgColor) = mainDetailsForView.healthCategory.getIconAndBg(requireContext())
        mainHeaderView.findViewById<CardView>(R.id.cv_product_health).setCardBackgroundColor(cardBgColor)
        mainHeaderView.findViewById<ImageView>(R.id.iv_product_health_icon).setImageResource(healthCategoryIcon)
        viewBindingLayout.llProductDetailsLayout.addView(mainHeaderView)
    }
}