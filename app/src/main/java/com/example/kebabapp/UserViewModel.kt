package com.example.kebabapp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kebabapp.utilities.DataXX
import com.example.kebabapp.utilities.UserService

class UserViewModel : ViewModel() {
    private var favKebabPlaces = KebabPlaces()
    private var userSuggestions = ArrayList<DataXX>()

    fun getUserSuggestions(): ArrayList<DataXX> {
        return userSuggestions
    }

    fun getFavKebabPlaces(): KebabPlaces {
        return favKebabPlaces
    }

    suspend fun getUserSuggestionsFromApi(userService: UserService) {
        try {
            val response = userService.getUserSuggestions()
            if (response.isSuccessful) {
                userSuggestions = response.body()?.data as ArrayList<DataXX>
            } else {
                Log.e("Profile", "Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("Profile", "Failure: ${e.message}")
        }
    }

    suspend fun getFavKebabsFromApi(userService: UserService) {
        try {
            val response = userService.getFavourites()
            if (response.isSuccessful) {
                Log.i("FAVS", response.body()?.data.toString())
                favKebabPlaces = response.body()?.data!!
            } else {
                Log.e("Profile", "Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("Profile", "Failure: ${e.message}")
        }
    }

    fun checkIfKebabIsFavourite(kebabId: Int): Boolean {
        for (kebab in favKebabPlaces) {
            if (kebab.id == kebabId) {
                return true
            }
        }
        return false
    }

    fun clearFavKebabPlaces() {
        favKebabPlaces.clear()
    }

    fun clearSuggestions() {
        userSuggestions.clear()
    }
}
