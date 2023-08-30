package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.Penjualan

data class PenjualanListResponse(
    val result: List<PenjualanResponse>
) {
    fun toPenjualanList(): List<Penjualan> = this.result.map { it.toPenjualan() }
}

data class PenjualanResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("id_karyawan")
    val userId: Int = 0,
    @SerializedName("karyawan")
    val karwayan: KaryawanResponse? = null,
    @SerializedName("total_price")
    val totalPrice: Int = 0,
    @SerializedName("payment_received")
    val paymentReceived: Int = 0,
    @SerializedName("payment_changes")
    val paymentChanges: Int = 0,
    @SerializedName("customer_name")
    val customerName: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("item")
    val items: List<PenjualanItemResponse>? = null,
) {
    fun toPenjualan(): Penjualan =
        Penjualan(
            id = this.id,
            idKaryawan = this.userId,
            karyawan = this.karwayan?.toKaryawan(),
            totalPrice = this.totalPrice,
            paymentReceived = this.paymentReceived,
            paymentChanges = this.paymentChanges,
            date = this.createdAt,
            customerName = customerName,
            items = PenjualanItemListResponse(this.items ?: listOf()).toPenjualanItemList()
        )
}
