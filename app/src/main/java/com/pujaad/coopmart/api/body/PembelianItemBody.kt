package com.pujaad.coopmart.api.body

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PembelianItemBody(
    val id_supplier : Int,
    val id_product: Int?,
    val quantity: Int,
    val price: Int,
) : Parcelable