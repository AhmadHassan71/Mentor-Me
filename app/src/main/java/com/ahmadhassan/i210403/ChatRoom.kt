package com.ahmadhassan.i210403

import android.os.Parcel
import android.os.Parcelable

data class ChatRoom(
    val roomId: String,
    val mentorId: String,
    val userId: String,
    val messages: List<Message>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<Message>().apply {
            parcel.readList(this, Message::class.java.classLoader)
        }
    )

    constructor() : this("", "", "", mutableListOf())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roomId)
        parcel.writeString(mentorId)
        parcel.writeString(userId)
        parcel.writeList(messages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatRoom> {
        override fun createFromParcel(parcel: Parcel): ChatRoom {
            return ChatRoom(parcel)
        }

        override fun newArray(size: Int): Array<ChatRoom?> {
            return arrayOfNulls(size)
        }
    }
}

