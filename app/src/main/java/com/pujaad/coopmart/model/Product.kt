package com.pujaad.coopmart.model

import android.os.Parcelable
import com.pujaad.coopmart.api.body.ProductBody
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    var id: Int,
    var productCategoryId: Int?,
    var category: ProductCategory?,
    var name : String,
    var description: String,
    var buyPrice: Int,
    var sellPrice: Int,
    var stock: Int,
    var createdAt: String,
    var supplier: Supplier?,
    var  user: Karyawan?,
) : Parcelable {
    fun toProductBody(userId: Int) : ProductBody = ProductBody(
        id = id,
        productCategoryId = category?.id ?: 0,
        name = name,
        description = description,
        buyPrice =  buyPrice,
        sellPrice = sellPrice,
        stock = stock,
        supplierCode = supplier?.code ?: "",
        userId = userId,
    )

    fun toNewPembelianItem(): PembelianItem = PembelianItem(
        id = 0,
        idPembelian = 0,
        idSupplier = supplier?.id ?: 0,
        supplier = this.supplier,
        idProduct = id,
        product = this,
        quantity = 1,
        price = buyPrice,
        createdAt = ""
    )

    fun toNewPenjualanItem(): PenjualanItem = PenjualanItem(
        id = 0,
        idPenjualan = 0,
        idProduct = id,
        product = this,
        quantity = 0,
        price = sellPrice,
    )
}