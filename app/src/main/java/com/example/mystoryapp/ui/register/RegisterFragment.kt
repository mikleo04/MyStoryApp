package com.example.mystoryapp.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.constants.Constants
import com.example.mystoryapp.databinding.FragmentRegisterBinding
import com.example.mystoryapp.tools.DateHelper
import com.example.mystoryapp.tools.Matcher

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var registerViewModelModel: RegisterViewModel
    private var isExecutingLogin = false

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    /*
    * this function still required for development process
    * */
    private fun devAutoFillForms(){
        val dateStr = DateHelper.getFormattedCurrentDate("yyyyMMddHHmmss")
        var str = getString(R.string.user)
        str += dateStr
        binding.registerEtName.setText(str)
        str += getString(R.string.at_gmail_dot_com)
        binding.registerEtEmail.setText(str)
        binding.registerEtPassword.setText(getString(R.string.default_password))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // this function is commented, but still required
        // devAutoFillForms()

        registerViewModelModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RegisterViewModel::class.java)

        binding.registerIbBackBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        registerViewModelModel.failMessage.observe(viewLifecycleOwner){
            binding.registerTvErrorMessage.text = it
        }

        registerViewModelModel.registerResponse.observe(viewLifecycleOwner) {
            if (it.error == false) {
//                onCreateUserSuccess()
            } else {
                binding.registerTvErrorMessage.text = getString(R.string.registration_failed)
                Toast.makeText(context, "Request Failed", Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModelModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.registerPbProgressBar.visibility = View.VISIBLE
            } else {
                binding.registerPbProgressBar.visibility = View.GONE
                isExecutingLogin = false
            }
        }

        binding.registerBtnRegister.setOnClickListener{
            if(!isExecutingLogin){
                val name = binding.registerEtName.text.toString()
                val email = binding.registerEtEmail.text.toString()
                val password = binding.registerEtPassword.text.toString()
                //
                var allowRegister = true
                var str = ""

                if (name.isEmpty()){
                    str = getString(R.string.name) + " "
                    str += getString(R.string.cannot_be_empty)
                    allowRegister = false
                }
                if (allowRegister && email.isEmpty()){
                    str = getString(R.string.email) + " "
                    str += getString(R.string.cannot_be_empty)
                    allowRegister = false
                }
                if (allowRegister && !Matcher.emailValid(email)){
                    str = getString(R.string.email_is_not_valid)
                    allowRegister = false
                }
                if (allowRegister && password.isEmpty()){
                    str = getString(R.string.password) + " "
                    str += getString(R.string.cannot_be_empty)
                    allowRegister = false
                }
                if (allowRegister && password.length < Constants.USER_DATA_MIN_PASSWORD_LENGTH){
                    str = getString(R.string.password_should_at_least)
                    str += " ${Constants.USER_DATA_MIN_PASSWORD_LENGTH}  ${getString(R.string.characters)}"
                    allowRegister = false
                }

                binding.registerTvErrorMessage.text = str
                if (allowRegister){
                    registerViewModelModel.register(name, email, password)
                    isExecutingLogin = true
                }

            }
        }
        binding.registerTvSignIn.setOnClickListener { activity?.onBackPressed() }
        binding.registerTvAlreadyHaveAccount.setOnClickListener { activity?.onBackPressed() }
    }

//    private fun onCreateUserSuccess(){
//        val mFragmentManager = parentFragmentManager
//        val mRegisterPopUpFragment = RegisterPopUpFragment()
//        val fragment = mFragmentManager.findFragmentByTag(RegisterPopUpFragment::class.java.simpleName)
//        if (fragment !is RegisterPopUpFragment){
//            val etName = binding.registerEtName.text
//            val etEmail = binding.registerEtEmail.text
//            val etPass = binding.registerEtPassword.text
//
//            val userData = User(etName.toString(), etEmail.toString(), etPass.toString())
//
//            val i = Intent()
//            i.putExtra(MainActivity.USER_EXTRA, userData)
//            activity?.setResult(MainActivity.USER_EXTRA_CODE, i)
//
//            mFragmentManager.beginTransaction().apply {
//                replace(R.id.register_container, mRegisterPopUpFragment, RegisterPopUpFragment::class.java.simpleName)
//                commit()
//            }
//        }
//    }
}