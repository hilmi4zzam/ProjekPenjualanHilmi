package com.hilmi.projekpenjualan.model

import android.os.Parcel
import android.os.Parcelable

data class ModelPegawai(
    var idPegawai: String? = null,
    var namaPegawai: String? = null,
    var statusPegawai: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        idPegawai = parcel.readString(),
        namaPegawai = parcel.readString(),
        statusPegawai = parcel.readString()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idPegawai)
        parcel.writeString(namaPegawai)
        parcel.writeString(statusPegawai)
    }

    companion object CREATOR : Parcelable.Creator<ModelPegawai> {
        override fun createFromParcel(parcel: Parcel): ModelPegawai {
            return ModelPegawai(parcel)
        }

        override fun newArray(size: Int): Array<ModelPegawai?> {
            return arrayOfNulls(size)
        }
    }
}