package com.ahmadhassan.i210403

object ChatRoomInstance {
    private var instance: ChatRoom? = null

    fun getInstance(): ChatRoom {
        if (instance == null) {
            instance = ChatRoom()
        }
        return instance!!
    }
    fun setInstance(chatRoom: ChatRoom) {
        instance = chatRoom
    }

    fun clearInstance() {
        instance = null
    }
}