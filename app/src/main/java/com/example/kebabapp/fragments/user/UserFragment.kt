package com.example.kebabapp.fragments.user

import RetrofitClient
import SharedPreferencesManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kebabapp.KebabDetailPageViewModel
import com.example.kebabapp.R
import com.example.kebabapp.UserViewModel
import com.example.kebabapp.databinding.FragmentUserPanelBinding
import com.example.kebabapp.utilities.LogoutResponse
import com.example.kebabapp.utilities.UserService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFragment : Fragment(), AdapterFavoritesClass.OnLogoClickListener {
    private lateinit var binding: FragmentUserPanelBinding
    private lateinit var kebabDetailPageViewModel: KebabDetailPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentUserPanelBinding.inflate(layoutInflater)
        val userService = RetrofitClient.retrofit.create(UserService::class.java)
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        kebabDetailPageViewModel = ViewModelProvider(requireActivity()).get(KebabDetailPageViewModel::class.java)
        val sharedPreferencesManager = context?.let { SharedPreferencesManager(it) }
        val isLogged = sharedPreferencesManager?.checkStatus()
        if (sharedPreferencesManager?.getName()?.isNotEmpty() == true) {
            binding.tvUserLoggedName.text = sharedPreferencesManager.getName().toString()
        }
        if (!isLogged!!) {
            Log.i("TOKEN", "User is not logged")
            userViewModel.clearSuggestions()
            userViewModel.clearFavKebabPlaces()
            viewLifecycleOwner.lifecycleScope.launch {
                findNavController().navigate(R.id.action_navigation_user_to_navigation_user_logging)
            }
            return binding.root
        }
        if (isLogged == true && sharedPreferencesManager.getName().isNullOrEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                userViewModel.getFavKebabsFromApi(userService)
                userViewModel.getUserSuggestionsFromApi(userService)
                val userName = getUserName(userService)
                Log.i("SUGG", userViewModel.getUserSuggestions().toString())
                if (!userName.isNullOrEmpty()) {
                    binding.tvUserLoggedName.text = userName
                    sharedPreferencesManager.saveName(userName)
                } else {
                    binding.tvUserLoggedName.text = "Failed to load username"
                }
            }
        }
        if (isLogged == true) {
            binding.rvFavoriteKebabPlaces.layoutManager = LinearLayoutManager(context)
            binding.rvFavoriteKebabPlaces.setHasFixedSize(true)
            binding.rvSuggestions.layoutManager = LinearLayoutManager(context)
            binding.rvSuggestions.setHasFixedSize(true)
            viewLifecycleOwner.lifecycleScope.launch {
                userViewModel.getUserSuggestionsFromApi(userService)
                userViewModel.getFavKebabsFromApi(userService)
                getData()
            }
        }
        binding.buttonRefresh.setOnClickListener {
            userViewModel.clearSuggestions()
            userViewModel.clearFavKebabPlaces()
            viewLifecycleOwner.lifecycleScope.launch {
                Log.i("TEST", userViewModel.getUserSuggestions().toString())
                userViewModel.getFavKebabsFromApi(userService)
                userViewModel.getUserSuggestionsFromApi(userService)
            }
            val fragmentId = findNavController().currentDestination?.id
            findNavController().popBackStack(fragmentId!!, true)
            findNavController().navigate(fragmentId)
            getData()
        }
        binding.buttonLogout.setOnClickListener {
            sharedPreferencesManager?.clearName()
            sharedPreferencesManager?.clearAuthToken()
            sharedPreferencesManager?.logout()
            userViewModel.clearFavKebabPlaces()
            userViewModel.clearSuggestions()
            findNavController().navigate(R.id.action_navigation_user_to_navigation_user_logging)
            userService.logoutUser().enqueue(
                object : Callback<LogoutResponse> {
                    override fun onResponse(
                        call: Call<LogoutResponse>,
                        response: Response<LogoutResponse>,
                    ) {
                        if (response.isSuccessful) {
                            RetrofitClient.setAuthToken("")
                            Log.i("LOGOUT", "SUCCESS LOGOUT")
                        } else {
                            Log.i("LOGOUT", "Something went wrong ")
                        }
                    }

                    override fun onFailure(
                        p0: Call<LogoutResponse>,
                        p1: Throwable,
                    ) {
                        Log.e("LOGOUT", "Error")
                    }
                },
            )
        }
        return binding.root
    }

    private suspend fun getUserName(userService: UserService): String? {
        return try {
            val response = userService.getProfileInfo()
            if (response.isSuccessful) {
                response.body()?.data?.name
            } else {
                Log.e("Profile", "Error: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("Profile", "Failure: ${e.message}")
            null
        }
    }

    private fun getData() {
        val userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val adapter = AdapterFavoritesClass(userViewModel.getFavKebabPlaces(), this)
        val adapterSuggestion = AdapterSuggestionsClass(userViewModel.getUserSuggestions())
        binding.rvFavoriteKebabPlaces.adapter = adapter
        binding.rvSuggestions.adapter = adapterSuggestion
    }

    override fun onLogoClick(itemId: Int) {
        kebabDetailPageViewModel.setKebabId(itemId)
        Log.i("ADAPTER", kebabDetailPageViewModel.getKebabId().toString())
        val navController = this.findNavController()
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.navigation_user) {
            navController.navigate(R.id.action_navigation_user_to_navigation_kebab_detail_page)
        } else {
            Log.e("NavigationError", "Invalid navigation state " + navController.currentDestination?.id.toString())
            navController.navigate(navController.currentDestination?.id!!)
        }
    }
}
