package com.mdev1008.nutriscanandroiddev.pages.profilepage

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.ComponentPpAllergenBinding
import com.mdev1008.nutriscanandroiddev.databinding.ComponentPpDietaryPreferenceBinding
import com.mdev1008.nutriscanandroiddev.databinding.ComponentPpDietaryRestrictionBinding
import com.mdev1008.nutriscanandroiddev.databinding.FragmentProfilePageBinding
import com.mdev1008.nutriscanandroiddev.models.data.NutrientType
import com.mdev1008.nutriscanandroiddev.models.remote.Allergen
import com.mdev1008.nutriscanandroiddev.models.remote.DietaryRestriction
import com.mdev1008.nutriscanandroiddev.models.remote.NutrientPreference
import com.mdev1008.nutriscanandroiddev.models.remote.UserAllergen
import com.mdev1008.nutriscanandroiddev.models.remote.UserDietaryPreference
import com.mdev1008.nutriscanandroiddev.models.remote.UserDietaryRestriction
import com.mdev1008.nutriscanandroiddev.models.remote.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.models.remote.containsAllergen
import com.mdev1008.nutriscanandroiddev.models.remote.containsRestriction
import com.mdev1008.nutriscanandroiddev.models.remote.dietaryRestrictionForProfilePage
import com.mdev1008.nutriscanandroiddev.models.remote.getPreference
import com.mdev1008.nutriscanandroiddev.models.remote.removeAllergen
import com.mdev1008.nutriscanandroiddev.models.remote.removeRestriction
import com.mdev1008.nutriscanandroiddev.models.remote.upsertPreference
import com.mdev1008.nutriscanandroiddev.pages.homepage.HomePageEvent
import com.mdev1008.nutriscanandroiddev.pages.homepage.HomePageViewModel
import com.mdev1008.nutriscanandroiddev.pages.homepage.HomePageViewModelFactory
import com.mdev1008.nutriscanandroiddev.utils.hide
import com.mdev1008.nutriscanandroiddev.utils.isValidUserName
import com.mdev1008.nutriscanandroiddev.utils.logger
import com.mdev1008.nutriscanandroiddev.utils.show
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

class ProfilePage : Fragment() {


    private lateinit var mainViewBinding: FragmentProfilePageBinding
//    private lateinit var cvDietaryPreferenceViewBinding: ComponentPpDietaryPreferenceBinding
//    private lateinit var cvDietaryRestrictionViewBinding: ComponentPpDietaryRestrictionBinding
//    private lateinit var cvAllergenViewBinding: ComponentPpAllergenBinding
    private lateinit var dietaryPreference:  MutableList<UserDietaryPreference>
    private lateinit var dietaryRestriction:  MutableList<UserDietaryRestriction>
    private lateinit var allergens:  MutableList<UserAllergen>
    private val viewModel by activityViewModels<HomePageViewModel> {
        val nutriScanApplication = requireActivity().application as NutriScanApplication
        HomePageViewModelFactory(
            apiRepository = nutriScanApplication.apiRepository,
            dbRepository = nutriScanApplication.dbRepository
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val state = viewModel.uiState.value
        mainViewBinding = FragmentProfilePageBinding.inflate(inflater, container, false)
//        cvDietaryPreferenceViewBinding = ComponentPpDietaryPreferenceBinding.inflate(inflater,container,false)
//        cvDietaryRestrictionViewBinding = ComponentPpDietaryRestrictionBinding.inflate(inflater, container, false)
//        cvAllergenViewBinding = ComponentPpAllergenBinding.inflate(inflater, container, false)
        dietaryPreference = state.dietaryPreferences.toMutableList()
        dietaryRestriction = state.dietaryRestriction.toMutableList()
        allergens = state.allergens.toMutableList()
        return mainViewBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefillTextField()
        setUpToolbar()
        buildDietaryPreferenceView()
        buildDietaryRestrictionView()
        buildAllergenView()
        setupSaveButton()
    }

    private fun setUpToolbar() {
        val appCompatActivity = activity as AppCompatActivity
        mainViewBinding.mtbProfilePage.let { materialToolbar ->
            appCompatActivity.setSupportActionBar(materialToolbar)
            materialToolbar.title = getString(R.string.profile_page)
            materialToolbar.setTitleTextColor(Color.WHITE)
        }
        if (viewModel.uiState.value.user?.isProfileCompleted == false){
            appCompatActivity.addMenuProvider(object: MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.profile_page_menu,menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when(menuItem.itemId){
                     R.id.mi_skip_profile_page -> {
                         viewModel.emit(HomePageEvent.SkipProfilePage)
                         findNavController().popBackStack()
                         true
                     }
                        else -> false
                    }
                } },viewLifecycleOwner)
        }
    }

    private fun prefillTextField() {
        viewModel.uiState.value.user?.let {
            mainViewBinding.tiUsernameEditText.setText(it.userName)
        }
    }

    private fun setupSaveButton() {
        mainViewBinding.btnSaveUserDetails.setOnClickListener {
            logger("dietary Preference: $dietaryPreference")
            val userName = mainViewBinding.tiUsernameEditText.text.toString()
            val uid = viewModel.uiState.value.user?.id
            if (userName.isValidUserName().first && uid != null){
                viewModel.uiState.value.user?.let { user ->
                    val userProfileDetails = UserProfileDetails(
                        userDetails = user.copy(
                            userName = userName,
                            isProfileCompleted = true
                        ),
                        userPreferences = dietaryPreference,
                        userRestrictions = dietaryRestriction,
                        userAllergen = allergens
                    )
                    viewModel.emit(HomePageEvent.UpdateUserDetails(userProfileDetails))
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(1000)
                        findNavController().popBackStack()
                    }
                }
            }else{
                view?.showSnackBar(userName.isValidUserName().second)
            }
        }
    }

    private fun buildAllergenView() {
        mainViewBinding.apply {
            llAllergenHeaderLayout.setOnClickListener {
                logger("clicked allergen header")
                toggleLayout(clAllergenCollapsable, ivAllergenExpandCollapse)
            }
        }

        Allergen.entries.forEach { allergen ->
            val button = ToggleButton(requireContext())
            button.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.dietary_restriction_selector)
                textOn = allergen.heading
                textOff = allergen.heading
                text = allergen.heading
                isChecked = allergens.containsAllergen(allergen)
                transformationMethod = null
                if (isChecked){
                    setTextColor(ContextCompat.getColor(requireContext(),R.color.md_theme_onPrimary))
                }else{
                    setTextColor(ContextCompat.getColor(requireContext(),R.color.md_theme_onSurface))
                }

                setPadding(12,0,12,0)
                id = View.generateViewId()
                setOnCheckedChangeListener { buttonView,isChecked ->
                    if (isChecked){
                        viewModel.uiState.value.user?.id?.let {userId ->
                            allergens.add(
                                UserAllergen(
                                    userId = userId,
                                    allergen = allergen
                                )
                            )
                        }
                        buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary))

                    }else{
                        allergens = allergens.removeAllergen(allergen).toMutableList()
                        buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onSurface))
                    }
                }
            }
            val buttonIds =  mainViewBinding.flAllergenLayout.referencedIds.toMutableList()
            buttonIds.add(button.id)
            mainViewBinding.flAllergenLayout.referencedIds = buttonIds.toIntArray()

            Log.d("logger", "adding ${allergen.heading} button to layout")
            mainViewBinding.clAllergenCollapsable.addView(button)
        }
    }

    private fun buildDietaryRestrictionView() {
        mainViewBinding.apply {
            llDietaryRestrictionHeader.setOnClickListener {
                logger("clicked restriction header")
                toggleLayout(clCollapsableDietaryRestriction, ivDietaryRestrictionExpandCollapse)
            }
        }
        dietaryRestrictionForProfilePage().forEach { restriction ->
            val button = ToggleButton(requireContext())
            button.apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.dietary_restriction_selector)
                textOn = restriction.heading
                textOff = restriction.heading
                text = restriction.heading
                isChecked = dietaryRestriction.containsRestriction(restriction)
                transformationMethod = null
                if (isChecked){
                    setTextColor(ContextCompat.getColor(requireContext(),R.color.md_theme_onPrimary))
                }else{
                    setTextColor(ContextCompat.getColor(requireContext(),R.color.md_theme_onSurface))
                }
                setPadding(12,0,12,0)
                id = View.generateViewId()
                setOnCheckedChangeListener { buttonView,isChecked ->
                    if (isChecked){
                        buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary))
                        viewModel.uiState.value.user?.id?.let {userId ->
                            dietaryRestriction.add(
                                UserDietaryRestriction(
                                    userId = userId,
                                    dietaryRestriction = restriction
                                )
                            )
                        }

                    }else{

                        dietaryRestriction = dietaryRestriction.removeRestriction(restriction).toMutableList()
                        buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onSurface))
                    }
                }
//                background = ContextCompat.getDrawable(requireContext(),R.color.md_theme_primary)
            }
            val buttonIds = mainViewBinding.flDietaryRestrictionCollapsableLayout.referencedIds.toMutableList()
            buttonIds.add(button.id)
            mainViewBinding.flDietaryRestrictionCollapsableLayout.referencedIds = buttonIds.toIntArray()


            Log.d("logger", "adding ${restriction.heading} button to layout")
            mainViewBinding.clCollapsableDietaryRestriction.addView(button)
        }

    }

    private fun buildDietaryPreferenceView() {
        val userId = viewModel.uiState.value?.user?.id
        mainViewBinding.apply {
            llDietaryPreferenceHeader.setOnClickListener{
                logger("clicked preference header")
                toggleLayout(clDietaryPreferenceCollapsable, ivDietaryPreferenceExpandCollapse)
            }
        }
        NutrientType.entries.forEach{nutrient ->
            val itemView = LayoutInflater.from(requireContext()).inflate(R.layout.component_pp_nutrient_radio_group,mainViewBinding.nutrientsCollapsableLayout,false)
            val tvNutrientName = itemView.findViewById<TextView>(R.id.tv_rg_nutrient_name)
            val rgNutrientGroup = itemView.findViewById<RadioGroup>(R.id.rg_nutrient_preference)
            val rbNutrientLow = itemView.findViewById<RadioButton>(R.id.rb_nutrient_low)
            val rbNutrientModerate = itemView.findViewById<RadioButton>(R.id.rb_nutrient_moderate)
            val rbNutrientHigh = itemView.findViewById<RadioButton>(R.id.rb_nutrient_high)

            tvNutrientName.text = nutrient.heading
            rbNutrientLow.isChecked = dietaryPreference.getPreference(nutrient) == NutrientPreference.LOW
            rbNutrientModerate.isChecked = dietaryPreference.getPreference(nutrient)== NutrientPreference.MODERATE
            rbNutrientHigh.isChecked = dietaryPreference.getPreference(nutrient) == NutrientPreference.HIGH

            rbNutrientLow.setOnCheckedChangeListener{_, isChecked ->
                if (isChecked){
                    userId?.let {userId ->

                        dietaryPreference = dietaryPreference.upsertPreference(
                            userId = userId,
                            nutrientType = nutrient,
                            nutrientPreference = NutrientPreference.LOW)
                    }
                }
            }
            rbNutrientModerate.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    userId?.let {
                        dietaryPreference = dietaryPreference.upsertPreference(
                            userId= userId,
                            nutrientType = nutrient,
                            nutrientPreference = NutrientPreference.MODERATE
                        )
                    }
                }
            }
            rbNutrientHigh.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    userId?.let {userId ->
                        dietaryPreference = dietaryPreference.upsertPreference(
                            userId = userId,
                            nutrientType = nutrient,
                            nutrientPreference = NutrientPreference.HIGH
                        )
                    }
                }
            }

            tvNutrientName.setOnLongClickListener {
                rgNutrientGroup.clearCheck()
                userId?.let {userId ->

                    dietaryPreference = dietaryPreference.upsertPreference(
                        userId = userId,
                        nutrientType = nutrient,
                        nutrientPreference = null
                    )
                }
                true
            }

            mainViewBinding.nutrientsCollapsableLayout.addView(itemView)

        }
    }


    private fun toggleLayout(layout: ConstraintLayout, iconView: ImageView){
        logger("toggling $layout")
        val headers = mutableListOf(
            Pair(mainViewBinding.clDietaryPreferenceCollapsable,mainViewBinding.ivDietaryPreferenceExpandCollapse),
            Pair(mainViewBinding.clCollapsableDietaryRestriction,mainViewBinding.ivDietaryRestrictionExpandCollapse),
            Pair(mainViewBinding.clAllergenCollapsable,mainViewBinding.ivAllergenExpandCollapse)
        )

        if (layout.isVisible){
            layout.hide()
            iconView.setImageResource(R.mipmap.arrow_down)
            return
        }
        for ((header, imageView) in headers){
            if (header == layout){
                header.show()
                imageView.setImageResource(R.mipmap.arrow_up)
            }else{
                header.hide()
                imageView.setImageResource(R.mipmap.arrow_down)
            }
        }
    }
}

