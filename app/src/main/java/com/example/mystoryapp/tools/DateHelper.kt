package com.example.mystoryapp.tools

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    fun getFormattedCurrentDate(pattern: String = "Mdd-MM-yyyy HH:mm:ss.SSS"): String{
        val c = Calendar.getInstance().time
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(c)
    }
}