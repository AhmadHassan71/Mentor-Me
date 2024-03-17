import android.os.Parcel
import android.os.Parcelable

data class PersonProfile(
    val profilePicture: String,
    val personName: String,
    val unreadMessages: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(profilePicture)
        parcel.writeString(personName)
        parcel.writeInt(unreadMessages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PersonProfile> {
        override fun createFromParcel(parcel: Parcel): PersonProfile {
            return PersonProfile(parcel)
        }

        override fun newArray(size: Int): Array<PersonProfile?> {
            return arrayOfNulls(size)
        }
    }
}
