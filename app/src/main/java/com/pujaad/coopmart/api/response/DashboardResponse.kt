package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.Dashboard

data class DashboardResponse(
    @SerializedName("masuk")
    val absensiMasuk: List<AbsensiResponse>,
    @SerializedName("keluar")
    val absensiKeluar: List<AbsensiResponse>,
    @SerializedName("izin")
    val absensiIzin: List<AbsensiResponse>,
    @SerializedName("terlambat")
    val absensiTerlambat: List<AbsensiResponse>,
) {
    fun toDashboard(name: String): Dashboard =
        Dashboard(
            absensiMasuk = AbsensiListResponse(absensiMasuk, name).toAbsensiList(),
            absensiKeluar = AbsensiListResponse(absensiKeluar, name).toAbsensiList(),
            absensiIzin = AbsensiListResponse(absensiIzin, name).toAbsensiList(),
            absensiTerlambat = AbsensiListResponse(absensiTerlambat, name).toAbsensiList(),
        )
}
