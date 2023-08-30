package com.pujaad.coopmart.api.common

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("status")
    val code: Int?,
    val message: String? = null
)