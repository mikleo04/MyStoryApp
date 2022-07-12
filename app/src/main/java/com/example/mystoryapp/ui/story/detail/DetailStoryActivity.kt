package com.example.mystoryapp.ui.story.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mystoryapp.api.response.ListStoryItem
import com.example.mystoryapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    companion object{
        const val USER_DETAIL_EXTRA = "userDetailExtra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<ListStoryItem>(USER_DETAIL_EXTRA)
        val storyText = data?.description.toString()

        binding.tvDetailusername.text = data?.name.toString()
        
        binding.tvDetailstory.text = storyText

        Glide.with(binding.root.context)
            .load(data?.photoUrl)
            .into(binding.ivDetailbanner)


    }
}