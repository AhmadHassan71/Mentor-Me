package com.ahmadhassan.i210403

data class User(val email: String, var fullName: String? = null, val city: String, val country: String, var profilePic: String, var bannerPic: String){
    constructor() : this("","","","","","")
}

