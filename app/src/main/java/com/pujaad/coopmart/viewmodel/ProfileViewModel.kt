package com.pujaad.coopmart.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.pujaad.coopmart.api.ApiFactory
import com.pujaad.coopmart.api.PosApi
import com.pujaad.coopmart.api.PosRepository
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.body.ChangePasswordBody
import com.pujaad.coopmart.api.common.*
import com.pujaad.coopmart.model.Karyawan
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent
import kotlinx.coroutines.launch


class ProfileViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository

    private lateinit var userProfile: Karyawan

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservableUser: MutableLiveData<Resource<Karyawan>> = MutableLiveData()
    val observableUser: LiveData<Resource<Karyawan>> = mutableObservableUser
    val changeProfileSuccess: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val changePasswordSuccess: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun getProfile() {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED,
                    "Unauthorized user detected, please re-login"
                )
                return@launch
            }
            isLoading.value = true
            when (val res = repository.getProfile(id)) {
                is Outcome.Success -> {
                    try {
                        userProfile = res.value.toKaryawan()
                        mutableObservableUser.value = Resource(
                            ResourceState.SUCCESS,
                            userProfile
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

                        401 -> onError.value = AppError(
                            ErrorType.LOGIN_NOT_FOUND,
                            "Invalid username and password combination"
                        )

                        404 -> onError.value = AppError(
                            ErrorType.LOGIN_NOT_FOUND, "Invalid username and password combination"
                        )

                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
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

    fun changeProfile(username: String,phone: String,name: String, address: String, age: Int) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            when {
                name.isEmpty() -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Name cannot be empty")

                address.isEmpty() -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "adress cannot be empty")

                username.isEmpty() -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "username cannot be empty")

                phone.isEmpty() -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "phone cannot be empty")

                age == 0 -> onError.value = AppError(ErrorType.EMPTY_FIELD, "Age cannot be empty")
                else -> {
                    isLoading.value = true
                    userProfile.username = username
                    userProfile.phone = phone
                    userProfile.name = name
                    userProfile.address = address
                    userProfile.age = age
                    when (val res = repository.changeProfile(userProfile)) {
                        is Outcome.Success -> {
                            changeProfileSuccess.value = true
                        }

                        is Outcome.Error -> {
                            when (res.cause?.code) {
                                null -> onError.value =
                                    AppError(
                                        ErrorType.INTERNET_ERROR,
                                        "Check your internet connection"
                                    )

                                401 -> onError.value = AppError(
                                    ErrorType.LOGIN_NOT_FOUND,
                                    "Invalid username and password combination"
                                )

                                404 -> onError.value = AppError(
                                    ErrorType.LOGIN_NOT_FOUND,
                                    "Invalid username and password combination"
                                )

                                else -> onError.value =
                                    AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
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
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmNewPassword: String) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            when {
                currentPassword.isEmpty() -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Current Password cannot be empty")

                newPassword.isEmpty() -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "New Password cannot be empty")

                confirmNewPassword.isEmpty() -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Confirm New Password cannot be empty")

                confirmNewPassword != newPassword -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "New password and Confirm new password must have same value")

                newPassword == currentPassword -> onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "New password must be different with current password")

                else -> {
                    isLoading.value = true
                    val changePassBody = ChangePasswordBody(id = id, old_password = currentPassword, new_password = confirmNewPassword)
                    when (val res = repository.changePassword(changePassBody)) {
                        is Outcome.Success -> {
                            changePasswordSuccess.value = true
                        }

                        is Outcome.Error -> {
                            when (res.cause?.code) {
                                null -> onError.value =
                                    AppError(
                                        ErrorType.INTERNET_ERROR,
                                        "Check your internet connection"
                                    )

                                401 -> onError.value = AppError(
                                    ErrorType.LOGIN_NOT_FOUND,
                                    "Unauthorized user detected"
                                )

                                502 -> onError.value = AppError(
                                    ErrorType.INVALID_PASSWORD,
                                    "Old password not match with current user"
                                )

                                else -> onError.value =
                                    AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                            }
                        }

                        else -> {
                            onError.value = AppError(
                                ErrorType.OPERATION_FAILED,
                                "Change password failed, try again later"
                            )
                        }
                    }

                    isLoading.value = false
                }
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(application) as T
        }
    }

}