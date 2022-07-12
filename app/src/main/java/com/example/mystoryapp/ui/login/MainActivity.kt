package com.example.mystoryapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.ui.register.RegisterActivity
import com.example.mystoryapp.ui.story.main.StoryActivity
import com.example.mystoryapp.constants.Constants.USER_DATA_MIN_PASSWORD_LENGTH
import com.example.mystoryapp.data.User
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.preferences.UserPreference
import com.example.mystoryapp.tools.Matcher

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    private lateinit var loginViewModelModel: LoginViewModel
    companion object{
        const val USER_EXTRA = "user_extra_data_after_create_account"
        const val USER_EXTRA_CODE = 1
    }

    private val resultLauncher = registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == USER_EXTRA_CODE && result.data != null) {
            val user = result.data!!.getParcelableExtra<User>(USER_EXTRA)
            binding.mainEtEmail.setText(user?.email)
            binding.mainEtPassword.setText(user?.password)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        userPreference = UserPreference(this)
        loginViewModelModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LoginViewModel::class.java)
        // check apakah user sudah login 
        val user = userPreference.getUser()
        if (
            !user.name.isNullOrEmpty() &&
            !user.token.isNullOrEmpty() &&
            !user.userId.isNullOrEmpty()
        ){
            startActivity(Intent(this, StoryActivity::class.java))
            finish()
        }
        
        setContentView(binding.root)
        supportActionBar?.title = "Login"
        supportActionBar?.hide()
        binding.mainTvDoNotHaveAcc.setOnClickListener {
            resultLauncher.launch(Intent(this@MainActivity, RegisterActivity::class.java))
        }
        
        
        //Login process
        loginViewModelModel.isLoading.observe(this){
            if (it) binding.mainPbProgressBarLogin.visibility = View.VISIBLE
            else binding.mainPbProgressBarLogin.visibility = View.GONE
        }

        loginViewModelModel.loginResponse.observe(this) {
            if (it.error == false){
                val user = User(
                    name = it.loginResult?.name.toString(),
                    userId = it.loginResult?.userId.toString(),
                    token = it.loginResult?.token.toString(),
                )
                userPreference.setUser(user)
                startActivity(Intent(this, StoryActivity::class.java))
                finish()
            }else{
                Toast.makeText(this, getString(R.string.wrong_email_or_password), Toast.LENGTH_SHORT).show()
            }

        }
        
        binding.mainBtnLogin.setOnClickListener{
                val email = binding.mainEtEmail.text.toString()
                val password = binding.mainEtPassword.text.toString()
                if (
                    email.isEmpty()
                    or !Matcher.emailValid(email)
                    or password.isEmpty()
                    or (password.length < USER_DATA_MIN_PASSWORD_LENGTH)
                ){
                    Toast.makeText(this, "Please check your data", Toast.LENGTH_SHORT).show()
                }else{
                    loginViewModelModel.login(email, password)
                }
        }
        
    }
}