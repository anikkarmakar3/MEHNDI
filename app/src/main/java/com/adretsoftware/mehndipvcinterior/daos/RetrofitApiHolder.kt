package com.adretsoftware.mehndipvcinterior.daos

import com.adretsoftware.mehndipvcinterior.data.model.ProductImage.CategoryId.CategoryIdResp
import com.adretsoftware.mehndipvcinterior.models.*
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RetrofitApiHolder {

    @GET("item.php")
    fun getItems(): Call<RetrofitItem>

    @Multipart
    @POST("get_users_list.php")
    fun getUsersList(
        @Part("parent") parent: RequestBody,
        @Part("user_type") user_type: RequestBody
    ): Call<RetrofitUser>

    @POST("item.php")
    fun sendItem(@Body item: Item): Call<RetrofitResponse>

    @POST("save_manage_product_item.php")
    fun saveManageProductItem(@Body item: ManageProductModel): Call<RetrofitResponse>

    @POST("save_item.php")
    fun saveItem(@Body item: ManageProductModel): Call<RetrofitResponse>

    @POST("add_category.php")
    fun addCategory(@Body item: Item): Call<RetrofitResponse>

    @POST("item_delete.php")
    fun deleteItem(@Body item: ItemDelete): Call<RetrofitResponse>

    @POST("picture_gallery.php")
    fun SaveGalleryImage(@Body item: PictureGalleryModel): Call<RetrofitResponse>

    @GET("picture_gallery.php")
    fun getGalleryImage(): Call<RetrofitPictureGalleryItem>

    @GET("invoice.php")
    fun getInvoices(): Call<RetrofitInvoicesItem>

    @GET("user_invoice.php")
    fun getUserInvoices(
        @Query("phone") phone: String
    ): Call<RetrofitInvoicesItem>

    @FormUrlEncoded
    @POST("user_invoice.php")
    fun newGetUserInvoice(
        @FieldMap params: Map<String, String>
    ): Call<InvoiceModel>

    @POST("invoice.php")
    fun saveInvoices(@Body item: InvoicesModel): Call<RetrofitResponse>

    @Multipart
    @POST("item_by_parent.php")
    fun getItemsByParent(
        @Part("parent") parent: RequestBody,
        @Part("user_id") user_id: RequestBody
    ): Call<RetrofitItem>

    @Multipart
    @POST("item_my_items.php")
    fun getMyItems(
        @Part("parent") parent: RequestBody,
        @Part("user_id") user_id: RequestBody
    ): Call<RetrofitItem>

    @GET("offer_items.php")
    fun getOfferItems(
        @Query("user_id") user_id: String
    ): Call<RetrofitItem>

    @GET("get_items_of_user.php")
    fun getItemsOfUser(
        @Query("category") category: String,
        @Query("user_id") user_id: String
    ): Call<RetrofitItemOfUserModel>

    @GET("is_user_active.php")
    fun isUserActive(
        @Query("user_id") user_id: String
    ): Call<RetrofitUser>

    @GET("check_app_update.php")
    fun checkAppUpdate(
    ): Call<RetrofitJsonModel>

    @POST("update_user_status.php")
    fun updateUserStatus(
        @Body user: User
    ): Call<RetrofitResponse>

    @GET("manage_products_items.php")
    fun getManageProductsItems(
        @Query("parent") parent: String,
        @Query("user_id") user_id: String
    ): Call<RetrofitItemOfUserModel>

    @Multipart
    @POST("image_upload.php")
    fun imageUpload(
        @Part image: MultipartBody.Part,
        @Part("id") id: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("cat_id") cat_id: RequestBody
    ): Call<RetrofitResponse>

    @Multipart
    @POST("image_upload.php")
    fun imageUpload(
        @Part image: MultipartBody.Part,
        @Part("id") id: RequestBody,
    ): Call<RetrofitResponse>

    @Multipart
    @POST("item_image_download.php")
    fun itemImageDownload(@Part("item_id") id: RequestBody): Call<RetrofitImage>

    @GET("banner_image_download.php")
    fun bannerImageDownload(): Call<RetrofitImage>

    @Multipart
    @POST("get_discount.php")
    fun getDiscountByUser(
        @Part("user_id") user_id: RequestBody,
        @Part("item_id") item_id: RequestBody
    ): Call<RetrofitDiscount>

    @Multipart
    @POST("get_discount_of_user.php")
    fun getDiscountOfUser(
        @Part("user_id") user_id: RequestBody,
        @Part("item_id") item_id: RequestBody
    ): Call<RetrofitDiscount>


//    @Multipart
//    @POST("discount_by_user_type.php")
//    fun getDiscountByUserType(@Part("user_type") user_type:RequestBody,@Part("item_id") item_id:RequestBody): Call<RetrofitDiscount>

    @POST("discount.php")
    fun setDiscount(@Body discount: Discount): Call<RetrofitResponse>


    @POST("user.php")
    fun setUser(@Body user: User): Call<RetrofitResponse>

    @POST("categoryProduct.php")
    fun categoryProductList(@Body cat_id: RequestBody): Call<RetrofitResponse>

    @POST("update_user.php")
    fun updateUser(@Body user: User): Call<RetrofitResponse>

    @POST("update_single_user.php")
    fun updateSingleUser(@Body user: User): Call<RetrofitResponse>

    @Multipart
    @POST("update_user_password.php")
    fun updateUserPassword(
        @Part("user_id") user_id: RequestBody,
        @Part("password") password: RequestBody
    ): Call<RetrofitResponse>

    @Multipart
    @POST("reset_user_password.php")
    fun resetUserPassword(
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody
    ): Call<RetrofitResponse>

    @GET("user.php")
    fun getUser(): Call<RetrofitUser>

    @GET("get_admin_commission.php")
    fun getAdminCommission(): Call<RetrofitUser>

    @GET("get_user_commission.php")
    fun getUserCommission(
        @Query("user_id") user_id: String
    ): Call<RetrofitMyCommission>

    @Multipart
    @POST("search_user_by_parent.php")
    fun getUserByParent(@Part("parent") parent: RequestBody): Call<RetrofitUser>

    @Multipart
    @POST("search_user_by_mobile.php")
    fun getUserByMobile(@Part("mobile") mobile: RequestBody): Call<RetrofitUser>

    @Multipart
    @POST("search_user_by_id.php")
    fun getUserById(@Part("user_id") user_id: RequestBody): Call<RetrofitUser>


    @POST("cart.php")
    fun setCart(@Body cart: CartItem): Call<RetrofitResponse>

    @Multipart
    @POST("get_cart.php")
    fun getCart(@Part("user_id") user_id: RequestBody): Call<RetrofitCartItem>

    @Multipart
    @POST("update_cart_quantity.php")
    fun updateCartQuantity(
        @Part("item_id") item_id: RequestBody,
        @Part("user_id") user_id: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("total_price") total_price: RequestBody
    ): Call<RetrofitResponse>

    @Multipart
    @POST("update_cart_delete.php")
    fun deleteCart(
        @Part("item_id") item_id: RequestBody,
        @Part("user_id") user_id: RequestBody
    ): Call<RetrofitResponse>

    @Multipart
    @POST("update_cart_delete_whole.php")
    fun deleteWholeCart(@Part("user_id") user_id: RequestBody): Call<RetrofitResponse>


    @POST("order.php")
    fun setOrder(@Body order: Order): Call<RetrofitResponse>

    @Multipart
    @POST("get_order.php")
    fun getOrderByUser(@Part("user_id") user_id: RequestBody): Call<RetrofitOrder>

    @GET("order.php")
    fun getOrder(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("userId") userId: String
    ): Call<RetrofitOrder>

    @GET("transaction_report.php")
    fun getTransactionReport(
        @Query("user_id") user_id: String,
        @Query("for_user") for_user: String
    ): Call<RetrofitTransaction>

    @GET("admin_transaction_report.php")
    fun adminTransactionReport(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Call<RetrofitTransaction>

    @GET("get_transaction_by_id.php")
    fun getTransactionById(
        @Query("user_id") user_id: String,
        @Query("for_user") for_user: String
    ): Call<RetrofitTransaction>

    @POST("transaction_report.php")
    fun sendTransactionReport(@Body transactionModel: TransactionModel): Call<RetrofitResponse>

    @Multipart
    @POST("update_order.php")
    fun updateOrderStatus(
        @Part("order_id") order_id: RequestBody,
        @Part("status") status: RequestBody
    ): Call<RetrofitResponse>


    @POST("order_item.php")
    fun setOrderItems(@Body orderItem: OrderItem): Call<RetrofitResponse>

    @Multipart
    @POST("get_order_item.php")
    fun getOrderItems(@Part("order_id") order_id: RequestBody): Call<RetrofitOrderItem>

    @GET("gallery_category.php")
    fun getImageCategoryId(): Call<CategoryIdResp>



}