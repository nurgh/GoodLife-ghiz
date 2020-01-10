package com.android.goodlife.Model

class Users (val id: String, val name: String, val email: String, val photo: String, val pass: String) {

    constructor(): this("", "", "", "", "")
}