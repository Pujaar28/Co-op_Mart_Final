package com.pujaad.coopmart.extension

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.pujaad.coopmart.api.common.ErrorResponse
import retrofit2.HttpException

fun Throwable.toErrorResponse(): ErrorResponse {
    val httpError = this as? HttpException ?: return ErrorResponse(
        null,
        null
    )
    val str = httpError.response()?.errorBody()?.string() ?: ""
    return try {
        Gson().fromJson(str,
            ErrorResponse::class.java)
            ?: return ErrorResponse(
                null,
                null
            )
    } catch (e: JsonSyntaxException) {
        ErrorResponse(
            httpError.code(),
            str
        )
    } catch (e: Throwable) {
        ErrorResponse(
            httpError.code(),
            httpError.message()
        )
    }
}

fun Throwable.getErrorResponseUrl(): String {
    val httpError = this as? HttpException ?: return "unable to parse"
    return httpError.response()?.raw()?.request?.url?.toUrl().toString()
}
