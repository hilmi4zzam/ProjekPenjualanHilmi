package com.hilmi.projekpenjualan.model

import android.os.Parcel
import android.os.Parcelable

data class ModelCabang(
    var idCabang: String? = null,
    var namaCabang: String? = null,
    var statusCabang: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idCabang)
        parcel.writeString(namaCabang)
        parcel.writeString(statusCabang)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ModelCabang> {
        override fun createFromParcel(parcel: Parcel): ModelCabang {
            return ModelCabang(parcel)
        }

        override fun newArray(size: Int): Array<ModelCabang?> {
            return arrayOfNulls(size)
        }
    }
}
