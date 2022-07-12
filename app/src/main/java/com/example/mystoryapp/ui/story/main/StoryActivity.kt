package com.example.mystoryapp.ui.story.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.ui.story.addstory.AddStoryActivity
import com.example.mystoryapp.ui.story.detail.DetailStoryActivity
import com.example.mystoryapp.api.response.ListStoryItem
import com.example.mystoryapp.data.User
import com.example.mystoryapp.databinding.ActivityStoryBinding
import com.example.mystoryapp.ui.login.MainActivity
import com.example.mystoryapp.preferences.UserPreference

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyModel: StoryViewModel
    private lateinit var listStoryAdapter: StoryAdapter
    private var storyList = arrayListOf<ListStoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storyModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            StoryViewModel::class.java)
        listStoryAdapter = StoryAdapter(storyList)

        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = getString(R.string.story_note_app)

        binding.storiesFabAddStory.setOnClickListener{
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        
        showStoryRecyclerList()
        storyModel.allStoryResponse.observe(this){
            storyList.clear()
            storyList = it.listStory as ArrayList<ListStoryItem>
            showStoryRecyclerList()
        }
        storyModel.isLoading.observe(this){
            if (it){
                binding.storiesProgressBar.visibility = View.VISIBLE
            }else{
                binding.storiesProgressBar.visibility = View.GONE
            }
        }
        storyModel.getAllStory(UserPreference(this).getUser().token.toString())
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.option_menu_log_out -> {
                val builder = AlertDialog.Builder(this)
                with(builder){
                    setTitle(getString(R.string.logout_question))
                    setMessage(getString(R.string.are_you_sure))
                    setPositiveButton(getString(R.string.logout)) { _, _ ->
                        UserPreference(this@StoryActivity).setUser(User())
                        startActivity(Intent(context, MainActivity::class.java))
                        finish()
                    }
                    setNegativeButton(getString(R.string.cancel), null)
                    show()
                }
            }
            R.id.option_menu_refresh -> {
                storyModel.getAllStory(UserPreference(this).getUser().token.toString())
                listStoryAdapter.notifyDatasetChangedHelper()
                binding.storiesProgressBar.visibility = View.VISIBLE
            }
        }

        return super.onOptionsItemSelected(item)
    }
    
    override fun onResume() {
        super.onResume()
        storyModel.getAllStory(UserPreference(this).getUser().token.toString())
        listStoryAdapter.notifyDatasetChangedHelper()
    }

    private fun showStoryRecyclerList(){
        listStoryAdapter = StoryAdapter(storyList)
        binding.storiesRvStoryList.layoutManager = LinearLayoutManager(this@StoryActivity)

        listStoryAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(applicationContext, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.USER_DETAIL_EXTRA, data)
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@StoryActivity as Activity).toBundle())
            }
        })
        binding.storiesRvStoryList.adapter = listStoryAdapter
    }
}