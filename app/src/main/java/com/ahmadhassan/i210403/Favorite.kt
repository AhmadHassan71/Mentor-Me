package com.ahmadhassan.i210403

data class Favorite (
    val mentorId: String,
    val userId: String,
    ){
        constructor() : this("", "")
}