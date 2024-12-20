package com.mdev1008.nutriscanandroiddev.data.repository

import com.mdev1008.nutriscanandroiddev.data.model.SearchHistoryItem
import com.mdev1008.nutriscanandroiddev.data.AppDatabase
import com.mdev1008.nutriscanandroiddev.data.model.User
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.data.model.generateUserDietaryPreferences
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.encrypt
import com.mdev1008.nutriscanandroiddev.utils.debugLogger


class DbRepository(private val db: AppDatabase) {

    private var currentUser: User? = null

    fun getCurrentUser() = currentUser

    private fun updateCurrentUser(){
        currentUser?.id?.let { id ->
            currentUser = getUserById(id)
        }
    }

    fun signInWithUserNamePassword(userName: String, password: String): Resource<User>{
        val user = getUser(userName) ?: return Resource.Failure("Not user exists with the given userName")

        if (user.password == password.encrypt()){
            this.currentUser = user
            return Resource.Success(user,"Signing in...")

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
                debugLogger(generateUserDietaryPreferences(it).toString())
            }
            return Resource.Success(data = user, message = "Successfully registered with username $userName" )
        }catch (e: Exception){
            debugLogger(e.message.toString())
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
    private fun getUserById(id: Int): User?{
        return db.userDao().getUserById(id).getOrNull(0)
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

        currentUser?.id?.let {userId ->
            if (searchHistoryItem.id != null){
                db.searchHistoryDao().addItem(searchHistoryItem.copy(userId = userId))
                return Resource.Success("Item Added to Search History")
            }
            val currentItemInHistory = db.searchHistoryDao().getUserSearchHistory(userId).filter { it.productId == searchHistoryItem.productId }.getOrNull(0)
            db.searchHistoryDao().addItem(
                searchHistoryItem.copy(
                    id = currentItemInHistory?.id,
                    userId = userId
                )
            )

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

    fun upsertUserProfileDetails(userProfileDetails: UserProfileDetails): Resource<Unit>{
        try {
            debugLogger("Trying to update user details")
            currentUser?.id?.let {
//                db.userDietaryPreferenceDao().deletePreferences(it)
                db.userRestrictionDao().deleteUserRestrictions(it)
                db.userAllergenDao().deleteUserAllergens(it)

                db.userDao().updateUser(userProfileDetails.userDetails)
                db.userDietaryPreferenceDao().addPreferences(userProfileDetails.userPreferences)
                db.userRestrictionDao().addUserRestriction(userProfileDetails.userRestrictions)
                db.userAllergenDao().addUserAllergens(userProfileDetails.userAllergen)
                debugLogger("Successfully updated")
                updateCurrentUser()
                return Resource.Success(Unit)
            }
            return Resource.Failure("User not Signed In")

        }catch (e: Exception){
            return Resource.Failure(e.message)
        }
   }

    fun skipUserProfileSetup() {
        currentUser?.let {user ->
            db.userDao().updateUser(
                user.copy(isProfileCompleted = true)
            )
        }
        updateCurrentUser()
    }
}