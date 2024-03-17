package com.ahmadhassan.i210403

data class BookedSession(
    val mentorId: String,
    val date: String,
    val time: String,
    val userId: String,
){
    constructor() : this("", "", "", "")
}