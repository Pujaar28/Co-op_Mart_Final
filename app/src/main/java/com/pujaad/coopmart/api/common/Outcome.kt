package com.pujaad.coopmart.api.common

sealed class Outcome<out T : Any> {
    data class Success<out T : Any>(val value: T) : Outcome<T>()
    data class Error(val cause: ErrorResponse? = null) : Outcome<Nothing>()
    data class Throwables(val cause: Throwable? = null): Outcome<Nothing>()
}