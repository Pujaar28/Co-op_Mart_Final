package com.pujaad.coopmart.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.pujaad.coopmart.api.ApiFactory
import com.pujaad.coopmart.api.PosApi
import com.pujaad.coopmart.api.PosRepository
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.common.*
import com.pujaad.coopmart.model.Karyawan
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent
import kotlinx.coroutines.launch


class UsersViewModel(application: Application) : BaseViewModel() {
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository
    val searchHandler: Handler = Handler(Looper.getMainLooper())
    val TYPING_DELAY: Long = 1000

    var isAddFormState = true

    private lateinit var selectedUser: Karyawan

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservableUsers: MutableLiveData<Resource<List<Karyawan>>> =
        MutableLiveData()
    private var mutableObservableUser: MutableLiveData<Karyawan> =
        MutableLiveData()

    val observableUsers: LiveData<Resource<List<Karyawan>>> = mutableObservableUsers
    val observableUser: LiveData<Karyawan> = mutableObservableUser
    val onSuccessSubmit: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun setUser(user: Karyawan) {
        selectedUser = user
        mutableObservableUser.value = selectedUser
        isAddFormState = false
    }

    fun getUserById(idUser: Int) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return@launch
            }
            isLoading.value = true
            when (val res = repository.getUser(id = idUser)) {
                is Outcome.Success -> {
                    try {
                        val user = res.value.toKaryawanList()[0]
                        mutableObservableUser.value = user
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

                        404 -> onError.value = AppError(
                            ErrorType.USER_NOT_FOUND, "Data not found"
                        )

                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                }

                else -> {
                    onError.value = AppError(
                        ErrorType.OPERATION_FAILED, "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun lookupUser(query: String) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return@launch
            }
            isLoading.value = true
            when (val res = repository.getUsers(query = query)) {
                is Outcome.Success -> {
                    try {
                        val users = res.value.toKaryawanList()
                        mutableObservableUsers.value = Resource(
                            ResourceState.SUCCESS, users
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

                        404 -> onError.value = AppError(
                            ErrorType.USER_NOT_FOUND, "Data not found"
                        )

                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                }

                else -> {
                    onError.value = AppError(
                        ErrorType.OPERATION_FAILED, "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun submitUser(name: String, username: String, phone: String, address: String, age: Int?) {
        val id = prefs.id?.toInt() ?: 0
        when {
            name.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "User name cannot be empty")
                return
            }

            username.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "User username cannot be empty")
                return
            }

            phone.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "User phone cannot be empty")
                return
            }

            address.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "User address cannot be empty")
                return
            }

            age == 0 -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "User age cannot be empty")
                return
            }

            id == 0 -> {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return
            }
        }

        viewModelScope.launch {
            selectedUser = if (isAddFormState){
                Karyawan(
                    id = 0,
                    name = name,
                    username = username,
                    phone = phone,
                    address = address,
                    age = age,
                    type = 1
                )
            }else{
                selectedUser.name = name
                selectedUser.username = username
                selectedUser.phone = phone
                selectedUser.age = age
                selectedUser.address
                selectedUser
            }
            isLoading.value = true
            when (val res = if (isAddFormState)
                repository.addUser(body = selectedUser)
            else
                repository.updateDataUsers(body = selectedUser)
            ) {
                is Outcome.Success -> {
                    onSuccessSubmit.value = true
                }

                is Outcome.Error -> {
                    when (res.cause?.code) {
                        null -> onError.value =
                            AppError(ErrorType.INTERNET_ERROR, "Check your internet connection")

                        else -> onError.value =
                            AppError(ErrorType.SERVER_ERROR, "Internal Server Error")
                    }
                }

                else -> {
                    onError.value = AppError(
                        ErrorType.OPERATION_FAILED, "Failed to do operation, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }



    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UsersViewModel(application) as T
        }
    }

}