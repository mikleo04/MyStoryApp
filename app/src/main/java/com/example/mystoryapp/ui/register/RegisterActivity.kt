package com.example.mystoryapp.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.hide()
        
        val mFragmentManager = supportFragmentManager
        val mRegisterFragment = RegisterFragment()
        val fragment = mFragmentManager.findFragmentByTag(RegisterFragment::class.java.simpleName)
        if (fragment !is RegisterFragment){
            mFragmentManager.beginTransaction().apply {
                replace(R.id.register_container, mRegisterFragment, RegisterFragment::class.java.simpleName)
                commit()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}