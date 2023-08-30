package com.pujaad.coopmart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LabaRugi(
    var penjualan : Int,
    var pembelian: Int,
    var pendapatan: Int,
    var date: String,
) : Parcelable