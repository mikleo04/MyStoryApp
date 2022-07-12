package com.example.mystoryapp.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.databinding.FragmentRegisterBinding
import com.example.mystoryapp.tools.Matcher

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var registerViewModelModel: RegisterViewModel

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater) 
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerViewModelModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RegisterViewModel::class.java)

        registerViewModelModel.registerResponse.observe(viewLifecycleOwner) {
            if (it.error == false) {
                activity?.finish()
            } else {
                Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModelModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.pbRegisterprogressbar.visibility = View.VISIBLE
            } else {
                binding.pbRegisterprogressbar.visibility = View.GONE
            }
        }

        binding.btnRegister.setOnClickListener{
                val name = binding.etRegistername.text.toString()
                val email = binding.etRegisteremail.text.toString()
                val password = binding.etRegisterpassword.text.toString()

                if (name.isEmpty()
                    or email.isEmpty()
                    or !Matcher.emailValid(email)
                    or password.isEmpty()
                    or (password.length < 6)
                ){
                    Toast.makeText(requireContext(), "Please check your data", Toast.LENGTH_SHORT).show()
                }else{
                    registerViewModelModel.register(name, email, password)
                }
        }
        binding.tvHaveaccount.setOnClickListener { activity?.onBackPressed() }
    }
}