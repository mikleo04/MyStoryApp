package com.example.mystoryapp.ui.story.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.api.ApiConfig
import com.example.mystoryapp.api.response.AllStoryResponse
import com.example.mystoryapp.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel : ViewModel() {
    private lateinit var user: User

    private val _allStoryResponse = MutableLiveData<AllStoryResponse>()
    val allStoryResponse: LiveData<AllStoryResponse> = _allStoryResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun setUserData(userX: User){
        user = userX
    }

    fun getAllStory() {
        _isLoading.value = true
        val client = ApiConfig.getApiService2(user.token.toString()).getAllStory()
        client.enqueue(object : Callback<AllStoryResponse> {
            override fun onResponse(
                call: Call<AllStoryResponse>,
                response: Response<AllStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _allStoryResponse.value = response.body()
                }
            }

            override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

}