package com.hilmi.projekpenjualan.model

import android.os.Parcel
import android.os.Parcelable

class ModelLaporan(
    val idLaporan: String? = null,
    val namaPemesan: String? = null,
    val totalHarga: Int? = 0,
    val timestamp: Long? = 0,
    val items: List<CartItem>? = null,
    val status: String? = "Dikerjakan",
    val namaKasir: String? = null,
    val dibayar: Long? = 0,
    val kembalian: Long? = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.createTypedArrayList(CartItem.CREATOR),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idLaporan)
        parcel.writeString(namaPemesan)
        parcel.writeValue(totalHarga)
        parcel.writeValue(timestamp)
        parcel.writeTypedList(items)
        parcel.writeString(status)
        parcel.writeString(namaKasir)
        parcel.writeValue(dibayar)
        parcel.writeValue(kembalian)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ModelLaporan> {
        override fun createFromParcel(parcel: Parcel): ModelLaporan = ModelLaporan(parcel)
        override fun newArray(size: Int): Array<ModelLaporan?> = arrayOfNulls(size)
    }
}
