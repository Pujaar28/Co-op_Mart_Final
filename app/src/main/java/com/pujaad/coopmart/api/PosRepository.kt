package com.pujaad.coopmart.api

import com.pujaad.coopmart.api.body.ChangePasswordBody
import com.pujaad.coopmart.api.body.LoginBody
import com.pujaad.coopmart.api.body.PembelianBody
import com.pujaad.coopmart.api.body.PenjualanBody
import com.pujaad.coopmart.api.body.ProductBody
import com.pujaad.coopmart.api.body.ReportBody
import com.pujaad.coopmart.api.common.Outcome
import com.pujaad.coopmart.api.response.AddPembelianResponse
import com.pujaad.coopmart.api.response.AddPenjualanResponse
import com.pujaad.coopmart.api.response.KaryawanListResponse
import com.pujaad.coopmart.api.response.KaryawanResponse
import com.pujaad.coopmart.api.response.LabaRugiListResponse
import com.pujaad.coopmart.api.response.PembelianListResponse
import com.pujaad.coopmart.api.response.PembelianResponse
import com.pujaad.coopmart.api.response.PenjualanListResponse
import com.pujaad.coopmart.api.response.PenjualanResponse
import com.pujaad.coopmart.api.response.ProductCategoryListResponse
import com.pujaad.coopmart.api.response.ProductListResponse
import com.pujaad.coopmart.api.response.ProductResponse
import com.pujaad.coopmart.api.response.SupplierListResponse
import com.pujaad.coopmart.extension.toErrorResponse
import com.pujaad.coopmart.extension.toStringOrEmpty
import com.pujaad.coopmart.model.Karyawan
import com.pujaad.coopmart.model.ProductCategory
import com.pujaad.coopmart.model.Supplier
import okhttp3.ResponseBody

class PosRepository(private val cloud: PosApi, private val prefs: Prefs) {

    suspend fun login(body: LoginBody): Outcome<KaryawanResponse> {
        return try {
            val res = cloud.login(
                username = body.username,
                password = body.password,
            )

            Outcome.Success(res.data[0])
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun getProfile(id: Int): Outcome<KaryawanResponse> {
        return try {
            val res = cloud.getProfile(
                id = id
            )

            Outcome.Success(res.data[0])
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun getUsers(query: String = ""): Outcome<KaryawanListResponse> {
        return try {
            val res = cloud.getUsers(
                type = 1,
                query = query
            )

            val result = KaryawanListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun getUser(id: Int): Outcome<KaryawanListResponse> {
        return try {
            val res = cloud.getProfile(
                id = id
            )

            val result = KaryawanListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun addUser(body: Karyawan): Outcome<String> {
        return try {
            val res = cloud.addUser(
                username = body.username,
                age = body.age,
                address = body.address,
                name = body.name,
                type = body.type,
                phone = body.phone
            )

            Outcome.Success(res.data.toStringOrEmpty())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun changeProfile(body: Karyawan): Outcome<String> {
        return try {
            val res = cloud.changeProfile(
                id = body.id,
                age = body.age,
                address = body.address,
                name = body.name,
                username = body.username,
                phone = body.phone
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun updateDataUsers(body: Karyawan): Outcome<String> {
        return try {
            val res = cloud.updateDataUsers(
                id = body.id,
                name = body.name,
                age = body.age,
                username = body.username,
                phone = body.phone,
                address = body.address
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun changePassword(body: ChangePasswordBody): Outcome<String> {
        return try {
            val res = cloud.changePassword(
                id = body.id,
                oldPassword = body.old_password,
                newPassword = body.new_password,
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun getInventoryById(id: Int): Outcome<ProductResponse> {
        return try {
            val res = cloud.getProductById(
                id = id
            )

            Outcome.Success(res.data[0])
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun lookupInventory(query: String): Outcome<ProductListResponse> {
        return try {
            val res = cloud.lookupProduct(
                query = query
            )
            val result = ProductListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }



    suspend fun getPembelian(id: Int? = null): Outcome<PembelianListResponse> {
        return try {
            val res = cloud.getPembelian(
                id = id
            )
            val result = PembelianListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun getPembelianInvoice(id: Int): Outcome<PembelianResponse> {
        return try {
            val res = cloud.getPembelianInvoice(
                id = id
            )

            Outcome.Success(res.data)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun addPembelian(pembelianBody: PembelianBody): Outcome<AddPembelianResponse> {
        return try {
            val res = cloud.addPembelian(
                pembelianBody = pembelianBody
            )
            Outcome.Success(res.data)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun getPenjualan(id: Int? = null): Outcome<PenjualanListResponse> {
        return try {
            val res = cloud.getPenjualan(
                id = id
            )
            val result = PenjualanListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun getPenjualanInvoice(id: Int): Outcome<PenjualanResponse> {
        return try {
            val res = cloud.getPenjualanInvoice(
                id = id
            )

            Outcome.Success(res.data)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun addPenjualan(penjualanBody: PenjualanBody): Outcome<AddPenjualanResponse> {
        return try {
            val res = cloud.addPenjualan(
                penjualanBody = penjualanBody
            )
            Outcome.Success(res.data)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun lookupInventoryCategory(query: String): Outcome<ProductCategoryListResponse> {
        return try {
            val res = cloud.lookupProductCategory(
                query = query
            )
            val result = ProductCategoryListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun addCategory(body: ProductCategory): Outcome<String> {
        return try {
            val res = cloud.addCategory(
                name = body.name,
                desc = body.description,
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun updateCategory(body: ProductCategory): Outcome<String> {
        return try {
            val res = cloud.updateCategory(
                id = body.id,
                name = body.name,
                desc = body.description,
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun lookupSupplier(query: String): Outcome<SupplierListResponse> {
        return try {
            val res = cloud.lookupSupplier(
                query = query
            )
            val result = SupplierListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun addSupplier(body: Supplier): Outcome<String> {
        return try {
            val res = cloud.addSupplier(
                name = body.name,
                desc = body.description,
                code = body.code,
                phone = body.phone,
                address = body.address,
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun updateSupplier(body: Supplier): Outcome<String> {
        return try {
            val res = cloud.updateSupplier(
                id = body.id,
                name = body.name,
                desc = body.description,
                code = body.code,
                phone = body.phone,
                address = body.address,
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun addProduct(body: ProductBody): Outcome<String> {
        return try {
            val res = cloud.addProduct(
                userId = body.userId,
                categoryId = body.productCategoryId,
                name = body.name,
                desc = body.description,
                supplierCode = body.supplierCode,
                buyPrice = body.buyPrice,
                sellPrice = body.sellPrice,
                stock = body.stock,
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun updateProduct(body: ProductBody): Outcome<String> {
        return try {
            val res = cloud.updateProduct(
                id = body.id,
                userId = body.userId,
                categoryId = body.productCategoryId,
                name = body.name,
                desc = body.description,
                supplierCode = body.supplierCode,
                buyPrice = body.buyPrice,
                sellPrice = body.sellPrice,
                stock = body.stock,
            )

            Outcome.Success(res.data.toString())
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }



    suspend fun reportPenjualan(body: ReportBody): Outcome<PenjualanListResponse> {
        return try {
            val res = cloud.reportPenjualan(
                body = body
            )
            val result = PenjualanListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun reportPembelian(body: ReportBody): Outcome<PembelianListResponse> {
        return try {
            val res = cloud.reportPembelian(
                body = body
            )
            val result = PembelianListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun reportProduct(body: ReportBody): Outcome<ProductListResponse> {
        return try {
            val res = cloud.reportProduct(
                body = body
            )
            val result = ProductListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun reportLabaRugi(body: ReportBody): Outcome<LabaRugiListResponse> {
        return try {
            val res = cloud.reportLabaRugi(
                body = body
            )
            val result = LabaRugiListResponse(res.data)
            Outcome.Success(result)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun reportPembelianPDF(userId: Int?, startDate: String?, endDate: String?): Outcome<ResponseBody> {
        return try {
            val res = cloud.getPembelianPDF(
                userId = userId,
                startDate = startDate,
                endDate = endDate
            )

            Outcome.Success(res)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun reportPenjualanPDF(userId: Int?, startDate: String?, endDate: String?): Outcome<ResponseBody> {
        return try {
            val res = cloud.getPenjualanPDF(
                userId = userId,
                startDate = startDate,
                endDate = endDate
            )

            Outcome.Success(res)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun reportProductPDF(userId: Int?, startDate: String?, endDate: String?): Outcome<ResponseBody> {
        return try {
            val res = cloud.getProductPDF(
                userId = userId,
                startDate = startDate,
                endDate = endDate
            )

            Outcome.Success(res)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

    suspend fun reportLabaRugiPDF(userId: Int?, startDate: String?, endDate: String?): Outcome<ResponseBody> {
        return try {
            val res = cloud.getLabaRugiPDF(
                userId = userId,
                startDate = startDate,
                endDate = endDate
            )

            Outcome.Success(res)
        } catch (err: Throwable) {
            err.printStackTrace()
            Outcome.Error(err.toErrorResponse())
        }
    }

}