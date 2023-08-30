package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.LabaRugi

data class LabaRugiListResponse(
    val result: List<LabaRugiResponse>
) {
    fun toLabaRugiList(): List<LabaRugi> = this.result.map { it.toLabaRugi() }
}

data class LabaRugiResponse(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("pembelian")
    val pembelian: Int = 0,
    @SerializedName("penjualan")
    val penjualan: Int = 0,
    @SerializedName("pendapatan")
    val pendapatan: Int = 0,
) {
    fun toLabaRugi(): LabaRugi =
        LabaRugi(
            date = date,
            pembelian = pembelian,
            penjualan = penjualan,
            pendapatan = pendapatan
        )
}
