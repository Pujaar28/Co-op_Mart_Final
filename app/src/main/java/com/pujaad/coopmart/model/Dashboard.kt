package com.pujaad.coopmart.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dashboard(
    var absensiMasuk: List<ModelDatabase>,
    var absensiKeluar: List<ModelDatabase>,
    var absensiIzin: List<ModelDatabase>,
    var absensiTerlambat: List<ModelDatabase>
) : Parcelable
