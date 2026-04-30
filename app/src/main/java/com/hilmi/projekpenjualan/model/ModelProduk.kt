package com.hilmi.projekpenjualan.model

import android.os.Parcel
import android.os.Parcelable

class ModelProduk(
    val idProduk: String? = null,
    val namaProduk: String? = null,
    val hargaProduk: Int? = 0,
    val idKategori: String? = null,
    val idCabang: String? = null,
    val fotoProduk: String? = null,
    val stokProduk: Int? = 0,
    var createdAt: String? = null,
    var updateAt: String? = null
) : Parcelable {
    var jumlahTerjual: Int = 0
        get() = field
        set(value) { field = value }

    constructor(parcel: Parcel) : this(
        idProduk = parcel.readString(),
        namaProduk = parcel.readString(),
        hargaProduk = parcel.readValue(Int::class.java.classLoader) as? Int,
        idKategori = parcel.readString(),
        idCabang = parcel.readString(),
        fotoProduk = parcel.readString(),
        stokProduk = parcel.readValue(Int::class.java.classLoader) as? Int,
        createdAt = parcel.readString(),
        updateAt = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idProduk)
        parcel.writeString(namaProduk)
        parcel.writeValue(hargaProduk)
        parcel.writeString(idKategori)
        parcel.writeString(idCabang)
        parcel.writeString(fotoProduk)
        parcel.writeValue(stokProduk)
        parcel.writeString(createdAt)
        parcel.writeString(updateAt)
    }

    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<ModelProduk> {
        override fun createFromParcel( parcel: Parcel): ModelProduk {
            return ModelProduk(parcel)
        }

        override fun newArray(size: Int): Array<ModelProduk?> {
            return arrayOfNulls(size)
        }
    }
}
