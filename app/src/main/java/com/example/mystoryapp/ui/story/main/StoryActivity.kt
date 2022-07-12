package com.example.mystoryapp.ui.story.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
    private lateinit var userPreference: UserPreference
    private var doubleTabToExit = false
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyModel: StoryViewModel
    private lateinit var user: User
    private lateinit var listStoryAdapter: StoryAdapter
    private var storyList = arrayListOf<ListStoryItem>()
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference(this)
        storyModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            StoryViewModel::class.java)
        user = userPreference.getUser()
        storyModel.setUserData(user)
        listStoryAdapter = StoryAdapter(storyList)

        supportActionBar?.title = getString(R.string.app_name)
        supportActionBar?.subtitle = getString(R.string.story_note_app)
//        supportActionBar?.hide()
        prepareFabAddStory()
        showStoryRecyclerList()
        storyModel.allStoryResponse.observe(this){
            storyList.clear()
            storyList = it.listStory as ArrayList<ListStoryItem>
            showStoryRecyclerList()
            binding.storiesSwipeRefreshContainer.isRefreshing = false
            isUpdating = false
        }
        storyModel.isLoading.observe(this){
            if (it){
//                binding.storiesProgressBar.visibility = View.VISIBLE
//                binding.storiesSwipeRefreshContainer.isRefreshing = true
                isUpdating = true
            }else{
                binding.storiesProgressBar.visibility = View.GONE
                binding.storiesSwipeRefreshContainer.isRefreshing = false
                isUpdating = false
            }
        }
        storyModel.getAllStory()
    }

    private fun refreshList(){
        if (!isUpdating){
            isUpdating = true
            storyModel.getAllStory()
            listStoryAdapter.notifyDatasetChangedHelper()

            Toast.makeText(this, getString(R.string.updating), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.option_menu_log_out -> {
                logOutAlert()
            }
            R.id.option_menu_refresh -> {
                if (!isUpdating){
                    refreshList()
                    binding.storiesProgressBar.visibility = View.VISIBLE
                }else{
                    Toast.makeText(this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun prepareFabAddStory(){
        binding.storiesFabAddStory.setOnClickListener{
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }
    private fun logOutAlert(){
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle(getString(R.string.logout_question))
            setMessage(getString(R.string.are_you_sure))
            setPositiveButton(getString(R.string.logout)) { _, _ ->
                userPreference.setUser(User())
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton(getString(R.string.cancel), null)
            show()
        }
    }

    private fun showStoryRecyclerList(){
        Log.d("TAG", "showStoryRecyclerList LIST SIZE: ${storyList.size}")
        if(storyList.isEmpty()){
            binding.storiesNoData.visibility = View.VISIBLE
        }else{
            binding.storiesNoData.visibility = View.INVISIBLE
        }

        binding.storiesSwipeRefreshContainer.setOnRefreshListener {
            if (!isUpdating){
                refreshList()
            }else{
                binding.storiesSwipeRefreshContainer.isRefreshing = false
                Toast.makeText(this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show()
            }
        }

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

    override fun onBackPressed() {
        if (doubleTabToExit) {
            super.onBackPressed()
            return
        }
        this.doubleTabToExit = true
        Toast.makeText(this, getString(R.string.double_tap_to_exit), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleTabToExit = false }, 2000)
    }
}