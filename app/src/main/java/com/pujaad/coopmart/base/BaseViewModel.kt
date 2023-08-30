package com.ppapujasera_mhu.base

import androidx.lifecycle.ViewModel
import com.pujaad.coopmart.api.common.AppError

abstract class BaseViewModel: ViewModel() {
    val onError: SingleLiveEvent<AppError> = SingleLiveEvent()
    val isLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
}