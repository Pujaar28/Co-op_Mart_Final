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
import com.pujaad.coopmart.model.Supplier
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent
import kotlinx.coroutines.launch


class SupplierViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository

    private lateinit var userProfile: Karyawan

    var isAddFormState = true

    private lateinit var selectedSupplier: Supplier

    val searchHandler: Handler = Handler(Looper.getMainLooper())
    val TYPING_DELAY: Long = 1000

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservableSupplier: MutableLiveData<Supplier> =
        MutableLiveData()
    private var mutableObservableSuppliers: MutableLiveData<Resource<List<Supplier>>> = MutableLiveData()
    val observableSupplier: LiveData<Supplier> = mutableObservableSupplier
    val observableSuppliers: LiveData<Resource<List<Supplier>>> = mutableObservableSuppliers
    val onSuccessSubmit: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun setSupplier(supplier: Supplier) {
        selectedSupplier = supplier
        mutableObservableSupplier.value = supplier
        isAddFormState = false
    }

    fun lookupSupplier(query: String) {

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
            when (val res = repository.lookupSupplier(query = query)) {
                is Outcome.Success -> {
                    try {
                        val suppliers = res.value.toSupplierList()
                        mutableObservableSuppliers.value = Resource(
                            ResourceState.SUCCESS,
                            suppliers
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
                            ErrorType.INVENTORY_NOT_FOUND, "Data not found"
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

    fun submitSupplier(name: String, desc: String, code: String, phone: String, address: String) {
        val id = prefs.id?.toInt() ?: 0
        when {
            name.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Supplier name cannot be empty")
                return
            }

            desc.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Supplier description cannot be empty")
                return
            }

            code.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Supplier code cannot be empty")
                return
            }

            phone.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Supplier phone cannot be empty")
                return
            }

            address.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Supplier address cannot be empty")
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
            selectedSupplier = if (isAddFormState) {
                Supplier(
                    id = 0,
                    name = name,
                    description = desc,
                    code = code,
                    phone = phone,
                    address = address
                )
            } else {
                selectedSupplier.name = name
                selectedSupplier.description = desc
                selectedSupplier.code = code
                selectedSupplier.phone = phone
                selectedSupplier.address = address
                selectedSupplier
            }
            isLoading.value = true
            when (val res =
                if (isAddFormState)
                    repository.addSupplier(body = selectedSupplier)
                else
                    repository.updateSupplier(body = selectedSupplier)
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
            return SupplierViewModel(application) as T
        }
    }

}