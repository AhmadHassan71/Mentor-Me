package com.ahmadhassan.i210403

data class Review (
val mentorName: String,
val rating: Float,
val reviewDescription: String,
val mentorId: String,
val userId: String,
){
    constructor() : this("", 0.0f, "", "", "")
}