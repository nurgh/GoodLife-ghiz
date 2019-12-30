package com.android.goodlife.Model



data class Doctor (val id : String, val name: String, val email: String, val password: String, val gender: String, val photos: String){

    constructor(): this("", "", "", "", "", "")
}
