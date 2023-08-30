package com.pujaad.coopmart.api.common

data class Resource<out T>(val status: ResourceState, val data: T? = null, val type: ErrorType? = null, val msg: String? = null) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                ResourceState.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(type: ErrorType?, msg: String?, data: T?): Resource<T> {
            return Resource(
                ResourceState.ERROR,
                data,
                type,
                msg
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                ResourceState.LOADING,
                data,
                null
            )
        }
    }
}