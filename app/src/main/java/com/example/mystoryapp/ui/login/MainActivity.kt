package com.example.mystoryapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
    private var doubleTabToExit = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    private lateinit var loginViewModelModel: LoginViewModel
    private var isExecutingLogin = false
    companion object{
        const val USER_EXTRA = "user_extra_data_after_create_account"
        const val USER_EXTRA_CODE = 1
        private const val TAG = "MainActivity"
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
        checkLoggedInUser()

        setContentView(binding.root)
        supportActionBar?.title = "Login"
        supportActionBar?.hide()
        prepareSignUp()
        prepareLogin()
    }

    override fun onResume() {
        super.onResume()
        binding.mainTvErrLoginFailed.text = ""
    }
//    private fun startOnBoarding(){
//        val onBoarding = Intent(this, OnBoardingActivity::class.java)
//        startActivity(onBoarding)
//    }
    private fun checkLoggedInUser() {
        val user = userPreference.getUser()
        val name = user.name
        val email = user.email
        val userId = user.userId
        val token = user.token

        Log.d(TAG, "checkLoggedInUser: name: $name" )
        Log.d(TAG, "checkLoggedInUser: email: $email" )
        Log.d(TAG, "checkLoggedInUser: userId: $userId" )
        Log.d(TAG, "checkLoggedInUser: token: $token" )

        if (
            !name.isNullOrEmpty() &&
            !token.isNullOrEmpty() &&
            !userId.isNullOrEmpty()
        ){
            val storiesActivity = Intent(this, StoryActivity::class.java)
            startActivity(storiesActivity)
            finish()
        }
    }


    private fun prepareLogin(){

        loginViewModelModel.isLoading.observe(this){
            if (it) binding.mainPbProgressBarLogin.visibility = View.VISIBLE
            else binding.mainPbProgressBarLogin.visibility = View.GONE
            isExecutingLogin = false
        }

        loginViewModelModel.loginResponse.observe(this) {
            if (it.error == false){
                val user = User(
                    name = it.loginResult?.name.toString(),
                    userId = it.loginResult?.userId.toString(),
                    token = it.loginResult?.token.toString(),
                )
                userPreference.setUser(user)
                val intent = Intent(this, StoryActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val msg = getString(R.string.wrong_email_or_password)
                binding.mainTvErrLoginFailed.text = msg
            }

        }


        binding.mainBtnLogin.setOnClickListener{
            if(!isExecutingLogin){
                binding.mainTvErrLoginFailed.text = ""
                val email = binding.mainEtEmail.text.toString()
                val password = binding.mainEtPassword.text.toString()

                var str = ""
                var allowLogin = true

                if (allowLogin && email.isEmpty()){
                    str = getString(R.string.email) + " "
                    str += getString(R.string.cannot_be_empty)
                    allowLogin = false
                }
                if (allowLogin && !Matcher.emailValid(email)){
                    str = getString(R.string.email_is_not_valid)
                    allowLogin = false
                }
                if (allowLogin && password.isEmpty()){
                    str = getString(R.string.password) + " "
                    str += getString(R.string.cannot_be_empty)
                    allowLogin = false
                }
                if (allowLogin && password.length < USER_DATA_MIN_PASSWORD_LENGTH){
                    str = getString(R.string.password_should_at_least)
                    str += " $USER_DATA_MIN_PASSWORD_LENGTH  ${getString(R.string.characters)}"
                    allowLogin = false
                }

                binding.mainTvErrLoginFailed.text = str
                if (allowLogin){
                    loginViewModelModel.login(email, password)
                    isExecutingLogin = true
                }
            }
        }
    }
    private fun prepareSignUp(){
        binding.mainTvDoNotHaveAcc.setOnClickListener {
            startRegisterActivity()
        }
        binding.mainTvSignUp.setOnClickListener {
            startRegisterActivity()
        }
    }

    private fun startRegisterActivity(){
        val registerIntent = Intent(this@MainActivity, RegisterActivity::class.java)
        resultLauncher.launch(registerIntent)
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