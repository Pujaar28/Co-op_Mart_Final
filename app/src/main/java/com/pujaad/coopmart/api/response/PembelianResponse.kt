package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.Pembelian

data class PembelianListResponse(
    val result: List<PembelianResponse>
) {
    fun toPembelianList(): List<Pembelian> = this.result.map { it.toPembelian() }
}

data class PembelianResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("id_karyawan")
    val userId: Int = 0,
    @SerializedName("karyawan")
    val karwayan: KaryawanResponse? = null,
    @SerializedName("total_price")
    val totalPrice: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("item")
    val items: List<PembelianItemResponse>? = null,
) {
    fun toPembelian(): Pembelian =
        Pembelian(
            id = this.id,
            idKaryawan = this.userId,
            karyawan = karwayan?.toKaryawan(),
            totalPrice = this.totalPrice,
            date = this.createdAt,
            items = PembelianItemListResponse(this.items ?: listOf()).toPembelianItemList()
        )
}
