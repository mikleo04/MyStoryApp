package com.example.mystoryapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var name: String? = "",
    var email: String? = "",
    var password: String? = "",
    var userId: String? = "",
    var token: String? = ""
): Parcelable