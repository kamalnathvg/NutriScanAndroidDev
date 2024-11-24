package com.mdev1008.nutriscanandroiddev.presentation.profile_page

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentProfilePageBinding
import com.mdev1008.nutriscanandroiddev.data.model.NutrientType
import com.mdev1008.nutriscanandroiddev.data.model.Allergen
import com.mdev1008.nutriscanandroiddev.data.model.NutrientPreference
import com.mdev1008.nutriscanandroiddev.data.model.UserAllergen
import com.mdev1008.nutriscanandroiddev.data.model.UserDietaryPreference
import com.mdev1008.nutriscanandroiddev.data.model.UserDietaryRestriction
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.data.model.containsAllergen
import com.mdev1008.nutriscanandroiddev.data.model.containsRestriction
import com.mdev1008.nutriscanandroiddev.data.model.dietaryRestrictionForProfilePage
import com.mdev1008.nutriscanandroiddev.data.model.getPreference
import com.mdev1008.nutriscanandroiddev.data.model.removeAllergen
import com.mdev1008.nutriscanandroiddev.data.model.removeRestriction
import com.mdev1008.nutriscanandroiddev.data.model.upsertPreference
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.hide
import com.mdev1008.nutriscanandroiddev.utils.isValidUserName
import com.mdev1008.nutriscanandroiddev.utils.debugLogger
import com.mdev1008.nutriscanandroiddev.utils.infoLogger
import com.mdev1008.nutriscanandroiddev.utils.show
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfilePage : Fragment() {

    private lateinit var viewBinding: FragmentProfilePageBinding
    private lateinit var dietaryPreference:  MutableList<UserDietaryPreference>
    private lateinit var dietaryRestriction:  MutableList<UserDietaryRestriction>
    private lateinit var allergens:  MutableList<UserAllergen>
    private val viewModel by activityViewModels<ProfilePageViewModel> {
        ProfilePageViewModel.Factory
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProfilePageBinding.inflate(inflater, container, false)
       return viewBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onEvent(ProfilePageEvent.GetUserDetails)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{ state ->
                    when(state.userDetailsFetchState){
                        Status.LOADING -> {

                        }
                        Status.SUCCESS -> {
                            state.userProfileDetails.let { userProfileDetails ->
                                dietaryPreference = userProfileDetails?.userPreferences?.toMutableList() ?: mutableListOf()
                                dietaryRestriction = userProfileDetails?.userRestrictions?.toMutableList() ?: mutableListOf()
                                allergens = userProfileDetails?.userAllergen?.toMutableList() ?: mutableListOf()
                            }
                            prefillTextField()
                            buildDietaryPreferenceView()
                            buildDietaryRestrictionView()
                            buildAllergenView()
                            setupSaveButton()
                        }
                        Status.FAILURE -> {
                            view.showSnackBar(state.errorMessage.toString())
                        }
                        Status.IDLE -> {}
                    }
                }
            }
        }
        setUpToolbar()

    }

    private fun setUpToolbar() {
        val appCompatActivity = activity as AppCompatActivity
        viewBinding.mtbProfilePage.let { materialToolbar ->
            appCompatActivity.setSupportActionBar(materialToolbar)
            materialToolbar.title = getString(R.string.profile_page)
            materialToolbar.setTitleTextColor(Color.WHITE)
        }
        val userProfileDetails = viewModel.uiState.value.userProfileDetails
        if (userProfileDetails?.userDetails?.isProfileCompleted == false){
            appCompatActivity.addMenuProvider(object: MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.profile_page_menu,menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when(menuItem.itemId){
                     R.id.mi_skip_profile_page -> {
                         viewModel.onEvent(ProfilePageEvent.SkipProfileSetup)
                         findNavController().popBackStack()
                         true
                     }
                        else -> false
                    }
                } },viewLifecycleOwner)
        }
    }

    private fun prefillTextField() {
        viewModel.uiState.value.userProfileDetails?.userDetails?.let {
            viewBinding.tiUsernameEditText.setText(it.userName)
        }
    }

    private fun setupSaveButton() {
        val currentUserDetails = viewModel.uiState.value.userProfileDetails
        viewBinding.btnSaveUserDetails.setOnClickListener {
            debugLogger("dietary Preference: $dietaryPreference")
            val userName = viewBinding.tiUsernameEditText.text.toString()
            val uid = currentUserDetails?.userDetails?.id
            if (userName.isValidUserName().first && uid != null){
                currentUserDetails.userDetails.let { user ->
                    val userProfileDetails = UserProfileDetails(
                        userDetails = user.copy(
                            userName = userName,
                            isProfileCompleted = true
                        ),
                        userPreferences = dietaryPreference,
                        userRestrictions = dietaryRestriction,
                        userAllergen = allergens
                    )
                    viewModel.onEvent(ProfilePageEvent.UpdateUserDetails(userProfileDetails))
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
        val userProfileDetails = viewModel.uiState.value.userProfileDetails
        viewBinding.apply {
            llAllergenHeaderLayout.setOnClickListener {
                debugLogger("clicked allergen header")
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
                       userProfileDetails?.userDetails?.id?.let {userId ->
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
            val buttonIds =  viewBinding.flAllergenLayout.referencedIds.toMutableList()
            buttonIds.add(button.id)
            viewBinding.flAllergenLayout.referencedIds = buttonIds.toIntArray()

            Log.d("logger", "adding ${allergen.heading} button to layout")
            viewBinding.clAllergenCollapsable.addView(button)
        }
    }

    private fun buildDietaryRestrictionView() {
        val userProfileDetails = viewModel.uiState.value.userProfileDetails
        viewBinding.apply {
            llDietaryRestrictionHeader.setOnClickListener {
                infoLogger("clicked restriction header")
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
                        userProfileDetails?.userDetails?.id?.let {userId ->
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
            val buttonIds = viewBinding.flDietaryRestrictionCollapsableLayout.referencedIds.toMutableList()
            buttonIds.add(button.id)
            viewBinding.flDietaryRestrictionCollapsableLayout.referencedIds = buttonIds.toIntArray()


            Log.d("logger", "adding ${restriction.heading} button to layout")
            viewBinding.clCollapsableDietaryRestriction.addView(button)
        }

    }

    private fun buildDietaryPreferenceView() {
        val userId = viewModel.uiState.value.userProfileDetails?.userDetails?.id
        viewBinding.apply {
            llDietaryPreferenceHeader.setOnClickListener{
                infoLogger("clicked preference header")
                toggleLayout(clDietaryPreferenceCollapsable, ivDietaryPreferenceExpandCollapse)
            }
        }
        NutrientType.entries.forEach{ nutrient ->
            val itemView = LayoutInflater.from(requireContext()).inflate(R.layout.component_pp_nutrient_radio_group,viewBinding.nutrientsCollapsableLayout,false)
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

            viewBinding.nutrientsCollapsableLayout.addView(itemView)

        }
    }


    private fun toggleLayout(layout: ConstraintLayout, iconView: ImageView){
        infoLogger("toggling $layout")
        val headers = mutableListOf(
            Pair(viewBinding.clDietaryPreferenceCollapsable,viewBinding.ivDietaryPreferenceExpandCollapse),
            Pair(viewBinding.clCollapsableDietaryRestriction,viewBinding.ivDietaryRestrictionExpandCollapse),
            Pair(viewBinding.clAllergenCollapsable,viewBinding.ivAllergenExpandCollapse)
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

