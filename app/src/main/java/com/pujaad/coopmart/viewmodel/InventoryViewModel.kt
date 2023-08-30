package com.pujaad.coopmart.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.ppapujasera_mhu.base.BaseViewModel
import com.ppapujasera_mhu.base.SingleLiveEvent
import com.pujaad.coopmart.api.ApiFactory
import com.pujaad.coopmart.api.PosApi
import com.pujaad.coopmart.api.PosRepository
import com.pujaad.coopmart.api.Prefs
import com.pujaad.coopmart.api.body.ProductBody
import com.pujaad.coopmart.api.common.*
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.model.Product
import com.pujaad.coopmart.model.ProductCategory
import com.pujaad.coopmart.model.Supplier
import kotlinx.coroutines.launch


class InventoryViewModel(application: Application) : BaseViewModel() {
    private val application = application
    private val prefs: Prefs = Prefs(application)
    private val repository: PosRepository

    private lateinit var userProfile: Karyawan

    val searchHandler: Handler = Handler(Looper.getMainLooper())
    val TYPING_DELAY: Long = 1000

    var isAddFormState = true

    private lateinit var selectedCategory: ProductCategory

    private lateinit var selectedProduct: Product
    private lateinit var selectedProductCategory: ProductCategory
    private lateinit var selectedProductSupplier: Supplier

    init {
        val cloud = ApiFactory.createService(PosApi::class.java, prefs)
        repository = PosRepository(cloud, prefs)
    }

    private var mutableObservableProducts: MutableLiveData<Resource<List<Product>>> =
        MutableLiveData()
    private var mutableObservableProduct: MutableLiveData<Product> =
        MutableLiveData()
    private var mutableObservableProductsCategory: MutableLiveData<Resource<List<ProductCategory>>> =
        MutableLiveData()
    private var mutableObservableProductCategory: MutableLiveData<ProductCategory> =
        MutableLiveData()

    val observableProducts: LiveData<Resource<List<Product>>> = mutableObservableProducts
    val observableProduct: LiveData<Product> = mutableObservableProduct
    val observableProductsCategory: LiveData<Resource<List<ProductCategory>>> =
        mutableObservableProductsCategory
    val observableProductCategory: LiveData<ProductCategory> = mutableObservableProductCategory
    val onSuccessSubmit: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val observableSelectedProductCategory: SingleLiveEvent<String> = SingleLiveEvent()
    val observableSelectedProductSupplier: SingleLiveEvent<String> = SingleLiveEvent()

    fun setCategory(category: ProductCategory) {
        selectedCategory = category
        mutableObservableProductCategory.value = selectedCategory
        isAddFormState = false
    }

    fun setProduct(product: Product) {
        selectedProduct = product
        if (selectedProduct.category != null) {
            selectedProductCategory = selectedProduct.category!!
            observableSelectedProductCategory.value = selectedProductCategory.name
        }

        if (selectedProduct.supplier != null) {
            selectedProductSupplier = selectedProduct.supplier!!
            observableSelectedProductSupplier.value = selectedProductSupplier.name
        }

        mutableObservableProduct.value = selectedProduct
        isAddFormState = false
    }

    fun getInventoryById(invId: Int) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return@launch
            }
            isLoading.value = true
            when (val res = repository.getInventoryById(id = invId)) {
                is Outcome.Success -> {
                    try {
                        val product = res.value.toProduct()
                        mutableObservableProduct.value = product
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
                        ErrorType.OPERATION_FAILED, "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun lookupInventory(query: String) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return@launch
            }
            isLoading.value = true
            when (val res = repository.lookupInventory(query = query)) {
                is Outcome.Success -> {
                    try {
                        val products = res.value.toProductList()
                        mutableObservableProducts.value = Resource(
                            ResourceState.SUCCESS, products
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
                        ErrorType.OPERATION_FAILED, "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun lookupInventoryCategory(query: String) {

        viewModelScope.launch {
            val id = prefs.id?.toInt() ?: 0
            if (id == 0) {
                onError.value = AppError(
                    ErrorType.LOGIN_UNAUTHORIZED, "Unauthorized user detected, please re-login"
                )
                return@launch
            }
            isLoading.value = true
            when (val res = repository.lookupInventoryCategory(query = query)) {
                is Outcome.Success -> {
                    try {
                        val productCategories = res.value.toCategoryList()
                        mutableObservableProductsCategory.value = Resource(
                            ResourceState.SUCCESS, productCategories
                        )
                    } catch (e: Exception) {
                        onError.value = AppError(
                            ErrorType.OPERATION_FAILED, "Error occured when trying to fetch data"
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
                        ErrorType.OPERATION_FAILED, "Login failed, try again later"
                    )
                }
            }

            isLoading.value = false
        }
    }

    fun submitCategory(name: String, desc: String) {
        val id = prefs.id?.toInt() ?: 0
        when {
            name.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Category name cannot be empty")
                return
            }

            desc.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Category description cannot be empty")
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
            selectedCategory = if (isAddFormState) {
                ProductCategory(
                    id = 0,
                    name = name,
                    description = desc
                )
            } else {
                selectedCategory.name = name
                selectedCategory.description = desc
                selectedCategory
            }
            isLoading.value = true
            when (val res =
                if (isAddFormState)
                    repository.addCategory(body = selectedCategory)
                else
                    repository.updateCategory(body = selectedCategory)
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

    fun setSelectedCategory(selectedCategory: ProductCategory) {
        selectedProductCategory = selectedCategory
        observableSelectedProductCategory.value = selectedProductCategory.name
    }

    fun setSelectedSupplier(selectedSupplier: Supplier) {
        selectedProductSupplier = selectedSupplier
        observableSelectedProductSupplier.value = selectedProductSupplier.name
    }

    fun submitProduct(name: String, desc: String, buyPrice: Int, sellPrice: Int, stock: Int) {
        val id = prefs.id?.toInt() ?: 0
        when {
            name.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Product name cannot be empty")
                return
            }

            desc.isEmpty() -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Product description cannot be empty")
                return
            }

            buyPrice == 0 -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Product buy price cannot be empty")
                return
            }

            sellPrice == 0 -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Product sell price cannot be empty")
                return
            }
            sellPrice < buyPrice -> {
                onError.value = AppError(
                    ErrorType.SELL_PRICE_TO_LOW, "Buy Price Less than Sell Price"
                )
                return
            }
            stock == 0 -> {
                onError.value =
                    AppError(ErrorType.EMPTY_FIELD, "Product stock cannot be empty")
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
            val productBody: ProductBody = if (isAddFormState) {
                ProductBody(
                    id = 0,
                    name = name,
                    description = desc,
                    productCategoryId = selectedProductCategory.id,
                    buyPrice = buyPrice,
                    sellPrice = sellPrice,
                    stock = stock,
                    supplierCode = selectedProductSupplier.code,
                    userId = id,
                )
            } else {
                selectedProduct.name = name
                selectedProduct.description = desc
                selectedProduct.category = selectedProductCategory
                selectedProduct.buyPrice = buyPrice
                selectedProduct.sellPrice = sellPrice
                selectedProduct.stock = stock
                selectedProduct.supplier = selectedProductSupplier
                selectedProduct.toProductBody(id)
            }
            isLoading.value = true
            when (val res =
                if (isAddFormState)
                    repository.addProduct(body = productBody)
                else
                    repository.updateProduct(body = productBody)
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
            return InventoryViewModel(application) as T
        }
    }

}