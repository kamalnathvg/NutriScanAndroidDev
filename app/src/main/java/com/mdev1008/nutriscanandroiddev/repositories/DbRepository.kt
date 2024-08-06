package com.mdev1008.nutriscanandroiddev.repositories

import com.mdev1008.nutriscanandroiddev.models.data.SearchHistoryItem
import com.mdev1008.nutriscanandroiddev.models.remote.AppDatabase
import com.mdev1008.nutriscanandroiddev.models.remote.User
import com.mdev1008.nutriscanandroiddev.models.remote.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.models.remote.generateUserDietaryPreferences
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.encrypt
import com.mdev1008.nutriscanandroiddev.utils.logger
import kotlin.math.log


class DbRepository(private val db: AppDatabase) {

    private var currentUser: User? = null

    fun getCurrentUser() = currentUser

    fun signInWithUserNamePassword(userName: String, password: String): Resource<User>{
        val user = getUser(userName) ?: return Resource.Failure("Not user exists with the given userName")

        if (user.password == password.encrypt()){
            this.currentUser = user
            return Resource.Success(user)

        }
        return Resource.Failure("Invalid Password")

    }

    fun createUserWithUserNamePassword(userName: String, password: String): Resource<User>{
        if (userAlreadyExists(userName)){
            return Resource.Failure("User Already Exists with the username")
        }
        val user = User(
            userName = userName,
            password = password.encrypt(),
            isProfileCompleted = false
        )
        try {
            db.userDao().createUser(user)
            val userId = db.userDao().getUser(userName).getOrNull(0)?.id
            userId?.let {
                db.userDietaryPreferenceDao().addPreferences(generateUserDietaryPreferences(it))
                logger(generateUserDietaryPreferences(it).toString())
            }
            return Resource.Success(user)
        }catch (e: Exception){
            logger(e.message.toString())
            return Resource.Failure(e.message)
        }
    }

    fun signOut(){
        currentUser = null
    }


    /**
     * returns true if a user exists with the given userName
     */
    private fun userAlreadyExists(userName: String): Boolean{

        val users =  db.userDao().getUser(userName)
        return users.isNotEmpty()
    }

    private fun getUser(userName: String): User?{
        return db.userDao().getUser(userName).getOrNull(0)
    }

    fun getSearchHistory(): Resource<List<SearchHistoryItem>>{
        if (currentUser == null){
            return Resource.Failure("Not Signed In")
        }
        currentUser?.id?.let { userId ->
            val searchHistory = db.searchHistoryDao().getUserSearchHistory(userId = userId)
            return Resource.Success(searchHistory)
        }

        return Resource.Failure("Unknown Error Occurred")
    }
    fun addItemToSearchHistory(searchHistoryItem: SearchHistoryItem): Resource<String>{
        if (currentUser == null) return Resource.Failure("Not Signed In")

        currentUser?.id?.let {
            db.searchHistoryDao().addItem(searchHistoryItem)
            return Resource.Success("Item Added to Search History")
        }
        return Resource.Failure("Unknown Error Occurred")
    }

    fun getUserProfileDetails(): Resource<UserProfileDetails>{
        try {
            currentUser?.let { user ->
                user.id?.let { userId ->

                    val preferences = db.userDietaryPreferenceDao().getUserPreferences(userId)
                    val restrictions = db.userRestrictionDao().getUserRestrictions(userId)
                    val allergens = db.userAllergenDao().getUserAllergens(userId)
                    val userProfileDetails = UserProfileDetails(
                        userDetails = user,
                        userPreferences = preferences,
                        userRestrictions = restrictions,
                        userAllergen = allergens
                    )
                    return Resource.Success(userProfileDetails)
                }
            }
            return Resource.Failure("User not Signed In")
        }catch (e: Exception){
            return Resource.Failure(e.message)
        }
    }

    fun upsertUserProfileDetails(userProfileDetails: UserProfileDetails): Resource<String>{
        try {
            logger("Trying to update user details")
            currentUser?.id?.let {
//                db.userDietaryPreferenceDao().deletePreferences(it)
                db.userRestrictionDao().deleteUserRestrictions(it)
                db.userAllergenDao().deleteUserAllergens(it)

                db.userDao().updateUser(userProfileDetails.userDetails)
                db.userDietaryPreferenceDao().addPreferences(userProfileDetails.userPreferences)
                db.userRestrictionDao().addUserRestriction(userProfileDetails.userRestrictions)
                db.userAllergenDao().addUserAllergens(userProfileDetails.userAllergen)
                logger("Successfully updated")
                return Resource.Success("Profile Updated Successfully")
            }
            return Resource.Failure("User not Signed In")

        }catch (e: Exception){
            return Resource.Failure(e.message)
        }
   }
}