package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.ModelDatabase

data class AbsensiListResponse(
    val result: List<AbsensiResponse>,
    val name: String
) {
    fun toAbsensiList(): List<ModelDatabase> = this.result.map { it.toAbsensi(name) }
}

data class AbsensiResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("id_karyawan")
    val idKaryawan: Int = 0,
    @SerializedName("date")
    val date: String = "",
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("detail")
    val detail: String = "",
    @SerializedName("location")
    val location: String = "",
    @SerializedName("photo")
    val photo: String = "",
) {
    fun toAbsensi(name: String): ModelDatabase {
        val result =  ModelDatabase()
        result.uid = id
        result.nama = name
        result.fotoSelfie = photo
        result.lokasi = location
        result.tanggal = date
        result.keterangan = detail
        result.status = status
        return result
    }
}
