package com.ahmadhassan.i210403

object CameraImageObject {
    private var imageUri: String = ""
    private var chatRoom: ChatRoom = ChatRoom()

    fun getInstanceOfImageObject(): CameraImageObject {
        return this
    }

    fun setInstance(imageUri: String, chatRoom: ChatRoom) {
        this.imageUri = imageUri
        this.chatRoom = chatRoom
    }

    fun resetInstance() {
        this.imageUri = ""
        this.chatRoom = ChatRoom()
    }

    fun getImageUri(): String {
        return this.imageUri
    }
    fun getChatRoom(): ChatRoom {
        return this.chatRoom
    }
}