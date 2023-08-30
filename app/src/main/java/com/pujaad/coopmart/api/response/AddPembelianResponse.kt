package com.pujaad.coopmart.api.response

import com.google.gson.annotations.SerializedName

data class AddPembelianResponse(
    @SerializedName("invoice_id")
    val invoiceId: Int = 0,
)