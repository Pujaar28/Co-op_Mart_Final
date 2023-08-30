package com.pujaad.coopmart.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.pujaad.coopmart.api.ApiFactory
import com.pujaad.coopmart.api.PosApi
import com.pujaad.coopmart.api.PosRepository
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.body.LoginBody
import com.pujaad.coopmart.api.common.*
import com.pujaad.coopmart.model.Karyawan
import com.ppapujasera_mhu.base.BaseViewModel
import kotlinx.coroutines.launch


class AwalViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservableUser: MutableLiveData<Resource<Karyawan>> = MutableLiveData()
    val observableUser: LiveData<Resource<Karyawan>> = mutableObservableUser

    fun login(username: String, password: String) {
        when {
            username.isEmpty() -> {
                onError.value = AppError(ErrorType.EMPTY_FIELD, "Username tidak boleh kosong")
                return
            }

            password.isEmpty() -> {
                onError.value = AppError(ErrorType.EMPTY_FIELD, "Password tidak boleh kosong")
                return
            }
        }

        val loginBody = LoginBody()
        loginBody.username = username
        loginBody.password = password

        viewModelScope.launch {
            isLoading.value = true

            when (val res = repository.login(loginBody)) {
                is Outcome.Success -> {
                    try {
                        val user = res.value.toKaryawan()
                        prefs.id = user.id.toString()
                        prefs.type = user.type.toString()
                        prefs.user = user.name
                        mutableObservableUser.value = Resource(
                            ResourceState.SUCCESS,
                            user
                        )
                    } catch (e: Exception) {
                        onError.value = AppError(
                            ErrorType.OPERATION_FAILED,
                            "Error occured when trying to fetch user data"
                        )
                    }
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")

                        401 -> onError.value = AppError(ErrorType.LOGIN_NOT_FOUND, "Kombinasi username dan password salah")
                        404 -> onError.value = AppError(
                            ErrorType.LOGIN_NOT_FOUND, "Invalid username and password combination"
                        )
                        else -> onError.value = AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                }

                else -> {
                    onError.value = AppError(
                        ErrorType.OPERATION_FAILED,
                        "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AwalViewModel(application) as T
        }
    }

}