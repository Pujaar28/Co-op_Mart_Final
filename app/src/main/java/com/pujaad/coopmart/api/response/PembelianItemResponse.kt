package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName
import com.pujaad.coopmart.model.PembelianItem

data class PembelianItemListResponse(
    val result: List<PembelianItemResponse>
) {
    fun toPembelianItemList(): List<PembelianItem> = this.result.map { it.toPembelianItem() }
}

data class PembelianItemResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("id_pembelian")
    val idPembelian: Int = 0,
    @SerializedName("id_supplier")
    val idSupplier: Int? = null,
    @SerializedName("supplier")
    val supplier: SupplierResponse? = null,
    @SerializedName("id_product")
    val idProduct: Int? = null,
    @SerializedName("product")
    val product: ProductResponse? = null,
    @SerializedName("quantity")
    val quantity: Int = 0,
    @SerializedName("price")
    val price: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
) {
    fun toPembelianItem(): PembelianItem =
        PembelianItem(
            id = this.id,
            idPembelian = this.idPembelian,
            idSupplier = idSupplier,
            supplier = supplier?.toSupplier(),
            idProduct = idProduct,
            product = product?.toProduct(),
            quantity = this.quantity,
            price = this.price,
            createdAt = this.createdAt,
        )
}
