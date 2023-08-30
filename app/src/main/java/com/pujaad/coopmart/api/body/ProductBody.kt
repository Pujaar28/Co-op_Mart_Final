package com.pujaad.coopmart.api.body

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductBody(
    var id: Int,
    var productCategoryId: Int,
    var name : String,
    var description: String,
    var buyPrice: Int,
    var sellPrice: Int,
    var stock: Int,
    var supplierCode: String,
    var userId: Int,
) : Parcelable