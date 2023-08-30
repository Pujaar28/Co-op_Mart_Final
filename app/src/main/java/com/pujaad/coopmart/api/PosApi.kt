package com.pujaad.coopmart.api

import com.pujaad.coopmart.api.body.PembelianBody
import com.pujaad.coopmart.api.body.PenjualanBody
import com.pujaad.coopmart.api.body.ReportBody
import com.pujaad.coopmart.api.common.BaseResponse
import com.pujaad.coopmart.api.response.AddPembelianResponse
import com.pujaad.coopmart.api.response.AddPenjualanResponse
import com.pujaad.coopmart.api.response.KaryawanResponse
import com.pujaad.coopmart.api.response.LabaRugiResponse
import com.pujaad.coopmart.api.response.PembelianResponse
import com.pujaad.coopmart.api.response.PenjualanResponse
import com.pujaad.coopmart.api.response.ProductCategoryResponse
import com.pujaad.coopmart.api.response.ProductResponse
import com.pujaad.coopmart.api.response.SupplierResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface PosApi {

    @POST("user/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): BaseResponse<String, List<KaryawanResponse>>

    @GET("user")
    suspend fun getProfile(@Query("id") id: Int): BaseResponse<String, List<KaryawanResponse>>

    @GET("user")
    suspend fun getUsers(
        @Query("type") type: Int,
        @Query("query") query: String,
    ): BaseResponse<String, List<KaryawanResponse>>

    @POST("user")
    @FormUrlEncoded
    suspend fun addUser(
        @Field("username") username: String,
        @Field("name") name: String,
        @Field("type") type: Int,
        @Field("phone") phone: String,
        @Field("age") age: Int?,
        @Field("address") address: String,
    ): BaseResponse<String, String?>

    @GET("pembelian")
    suspend fun getPembelian(@Query("id") id: Int? = null): BaseResponse<String, List<PembelianResponse>>

    @GET("pembelian/print_invoice")
    suspend fun getPembelianInvoice(@Query("invoice_id") id: Int? = null): BaseResponse<String, PembelianResponse>

    @POST("pembelian")
    suspend fun addPembelian(
        @Body pembelianBody: PembelianBody,
    ): BaseResponse<String, AddPembelianResponse>

    @GET("penjualan")
    suspend fun getPenjualan(@Query("id") id: Int? = null): BaseResponse<String, List<PenjualanResponse>>

    @GET("penjualan/print_invoice")
    suspend fun getPenjualanInvoice(@Query("invoice_id") id: Int? = null): BaseResponse<String, PenjualanResponse>

    @POST("penjualan")
    suspend fun addPenjualan(
        @Body penjualanBody: PenjualanBody,
    ): BaseResponse<String, AddPenjualanResponse>

    @POST("user/change_profile")
    @FormUrlEncoded
    suspend fun changeProfile(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("phone") phone: String,
        @Field("age") age: Int?,
        @Field("address") address: String,
    ): BaseResponse<String, String?>

    @PUT("user")
    @FormUrlEncoded
    suspend fun updateDataUsers(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("age") age: Int?,
        @Field("username") username: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
    ): BaseResponse<String, String?>

    @POST("user/change_password")
    @FormUrlEncoded
    suspend fun changePassword(
        @Field("id") id: Int,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
    ): BaseResponse<String, String?>

    @GET("product")
    suspend fun getProductById(@Query("id") id: Int): BaseResponse<String, List<ProductResponse>>

    @GET("product/lookup")
    suspend fun lookupProduct(@Query("query") query: String): BaseResponse<String, List<ProductResponse>>

    @GET("product/check_stock_get")
    suspend fun checkStock(@Query("id") id: Int): BaseResponse<String, List<ProductResponse>>

    @GET("ProductCategory/lookup")
    suspend fun lookupProductCategory(@Query("query") query: String): BaseResponse<String, List<ProductCategoryResponse>>

    @POST("ProductCategory")
    @FormUrlEncoded
    suspend fun addCategory(
        @Field("name") name: String,
        @Field("description") desc: String,
    ): BaseResponse<String, String?>

    @PUT("ProductCategory")
    @FormUrlEncoded
    suspend fun updateCategory(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("description") desc: String,
    ): BaseResponse<String, String?>

    @GET("supplier/lookup")
    suspend fun lookupSupplier(@Query("query") query: String): BaseResponse<String, List<SupplierResponse>>

    @POST("supplier")
    @FormUrlEncoded
    suspend fun addSupplier(
        @Field("name") name: String,
        @Field("description") desc: String,
        @Field("code") code: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
    ): BaseResponse<String, String?>

    @PUT("supplier")
    @FormUrlEncoded
    suspend fun updateSupplier(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("description") desc: String,
        @Field("code") code: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
    ): BaseResponse<String, String?>

    @POST("product")
    @FormUrlEncoded
    suspend fun addProduct(
        @Field("user_id") userId: Int,
        @Field("product_catagory_id") categoryId: Int,
        @Field("name") name: String,
        @Field("description") desc: String,
        @Field("supplier_code") supplierCode: String,
        @Field("buy_price") buyPrice: Int,
        @Field("sell_price") sellPrice: Int,
        @Field("stock") stock: Int,
    ): BaseResponse<String, String?>

    @PUT("product")
    @FormUrlEncoded
    suspend fun updateProduct(
        @Field("id") id: Int,
        @Field("user_id") userId: Int,
        @Field("product_catagory_id") categoryId: Int,
        @Field("name") name: String,
        @Field("description") desc: String,
        @Field("supplier_code") supplierCode: String,
        @Field("buy_price") buyPrice: Int,
        @Field("sell_price") sellPrice: Int,
        @Field("stock") stock: Int,
    ): BaseResponse<String, String?>

    @POST("report/pembelian")
    suspend fun reportPembelian(
        @Body body: ReportBody,
    ): BaseResponse<String, List<PembelianResponse>>

    @POST("report/penjualan")
    suspend fun reportPenjualan(
        @Body body: ReportBody,
    ): BaseResponse<String, List<PenjualanResponse>>

    @POST("report/product")
    suspend fun reportProduct(
        @Body body: ReportBody,
    ): BaseResponse<String, List<ProductResponse>>

    @POST("report/laba_rugi")
    suspend fun reportLabaRugi(
        @Body body: ReportBody,
    ): BaseResponse<String, List<LabaRugiResponse>>

    @GET(" report/pdf_pembelian")
    @Streaming
    suspend fun getPembelianPDF(
        @Query("id_karyawan") userId: Int?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
    ): ResponseBody

    @GET("report/pdf_penjualan")
    @Streaming
    suspend fun getPenjualanPDF(
        @Query("id_karyawan") userId: Int?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
    ): ResponseBody

    @GET(" report/pdf_product")
    @Streaming
    suspend fun getProductPDF(
        @Query("id_karyawan") userId: Int?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
    ): ResponseBody

    @GET("report/laba_rugi")
    @Streaming
    suspend fun getLabaRugiPDF(
        @Query("id_karyawan") userId: Int?,
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
    ): ResponseBody
}
