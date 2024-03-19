package com.ahmadhassan.i210403

import android.os.Parcel
import android.os.Parcelable

data class Message(
    val id: String,
    var text: String,
    val timestamp: String,
    val imageUrl: String?,
    val sentByCurrentUser: Boolean,
    val isAudioMessage: Boolean = false // Added isAudioMessage property with default value
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()?:"",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte() // Read isAudioMessage from parcel
    )
    constructor() : this("", "", "", null, false, false)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(text)
        parcel.writeString(timestamp)
        parcel.writeString(imageUrl)
        parcel.writeByte(if (sentByCurrentUser) 1 else 0)
        parcel.writeByte(if (isAudioMessage) 1 else 0) // Write isAudioMessage to parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}