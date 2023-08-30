package com.pujaad.coopmart.api.common

data class BaseResponse<M,D>(
    val status: M,
    val message: String?,
    val data: D
)