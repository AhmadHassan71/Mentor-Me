package com.ahmadhassan.i210403

object MentorChatInstance {
    private var instance: Mentors? = null

    fun getInstance(): Mentors {
        if (instance == null) {
            instance = Mentors()
        }
        return instance!!
    }
    fun setInstance(mentorChat: Mentors) {
        instance = mentorChat
    }

    fun clearInstance() {
        instance = null
    }
}