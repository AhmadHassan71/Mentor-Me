package com.ahmadhassan.i210403

data class User(val userId:String,val email: String, var fullName: String? = null, val city: String, val country: String, var profilePic: String, var bannerPic: String){
    constructor() : this("","","","","","","")
    constructor(userId: String, profilePic: String) : this(userId, "", null, "", "", profilePic, "")
    constructor(userId: String, email: String, fullName: String, city: String, country: String) : this(userId, email, fullName, city, country, "", "")
}

