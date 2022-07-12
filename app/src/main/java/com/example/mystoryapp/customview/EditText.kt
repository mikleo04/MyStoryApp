package com.example.mystoryapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.mystoryapp.R
import com.google.android.material.textfield.TextInputLayout

class EditText: AppCompatEditText,  View.OnTouchListener {
    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }
    private fun init(){
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                errorHandler()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        errorHandler()
        return false
    }

    private fun errorHandler(){
        if (text.isNullOrEmpty() || text?.isEmpty() == true){
            showError()
        }else{
            hideError()
        }
    }

    private fun showError(){
        val textInputLayout = this.parent.parent as TextInputLayout
        textInputLayout.error = context.getString(R.string.cannot_be_empty)
    }

    private fun hideError(){
        val textInputLayout = this.parent.parent as TextInputLayout
        textInputLayout.error = null
    }

}