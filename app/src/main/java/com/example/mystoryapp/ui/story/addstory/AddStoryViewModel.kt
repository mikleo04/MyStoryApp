package com.example.mystoryapp.ui.story.addstory

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.api.ApiConfig
import com.example.mystoryapp.api.response.AddNewStoryResponse
import com.example.mystoryapp.api.response.LoginResponse
import com.example.mystoryapp.data.User
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel : ViewModel() {
    private val _addNewStoryResponse = MutableLiveData<AddNewStoryResponse>()
    val addNewStoryResponse: LiveData<AddNewStoryResponse> = _addNewStoryResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val TAG = "AddStoryModel"
    }
    
    fun addNewStory(description: String, lat: Double, lng: Double, photo: File, token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService2(token).addNewStory(
            description.toRequestBody("text/plain".toMediaType()),
            lat,
            lng,
            MultipartBody.Part.createFormData("photo",
                photo.name,
                photo.asRequestBody("image/jpeg".toMediaTypeOrNull()))
        )
        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _addNewStoryResponse.value = response.body()
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _addNewStoryResponse.value = AddNewStoryResponse(
                    true,
                    "Something went wrong"
                )
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

}