package com.mytodokart.app.network;


import com.mytodokart.app.models.CancelSubscriptionData;
import com.mytodokart.app.models.NumberLoginResponse;
import com.mytodokart.app.models.OfferData;
import com.mytodokart.app.models.SlotData;
import com.mytodokart.app.models.SubscriptionData;
import com.mytodokart.app.models.SubscriptionHistory;
import com.mytodokart.app.models.Subscription_history_data;
import com.mytodokart.app.models.address_model.AddressData;
import com.mytodokart.app.models.address_model.Countries;
import com.mytodokart.app.models.address_model.Zones;
import com.mytodokart.app.models.banner_model.BannerData;
import com.mytodokart.app.models.category_model.CategoryData;
import com.mytodokart.app.models.contact_model.ContactUsData;
import com.mytodokart.app.models.currency_model.CurrencyModel;
import com.mytodokart.app.models.device_model.AppSettingsData;
import com.mytodokart.app.models.filter_model.get_filters.FilterData;
import com.mytodokart.app.models.googleMap.GoogleAPIResponse;
import com.mytodokart.app.models.language_model.LanguageData;
import com.mytodokart.app.models.news_model.all_news.NewsData;
import com.mytodokart.app.models.news_model.news_categories.NewsCategoryData;
import com.mytodokart.app.models.pages_model.PagesData;
import com.mytodokart.app.models.pay_tm.Checksum;
import com.mytodokart.app.models.product_model.GetAllProducts;
import com.mytodokart.app.models.coupons_model.CouponsData;
import com.mytodokart.app.models.payment_model.PaymentMethodsData;
import com.mytodokart.app.models.product_model.GetStock;
import com.mytodokart.app.models.product_model.ProductStock;
import com.mytodokart.app.models.ratings.GetRatings;
import com.mytodokart.app.models.ratings.GiveRating;
import com.mytodokart.app.models.shipping_model.PostTaxAndShippingData;
import com.mytodokart.app.models.order_model.OrderData;
import com.mytodokart.app.models.payment_model.GetBrainTreeToken;
import com.mytodokart.app.models.order_model.PostOrder;
import com.mytodokart.app.models.product_model.ProductData;
import com.mytodokart.app.models.search_model.SearchData;
import com.mytodokart.app.models.shipping_model.ShippingRateData;
import com.mytodokart.app.models.uploadimage.UploadImageModel;
import com.mytodokart.app.models.user_model.UserData;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * APIRequests contains all the Network Request Methods with relevant API Endpoints
 **/

public interface APIRequests {

    //******************** User Data ********************//

    @Multipart
    @POST("processregistration")
    Call<UserData> processRegistration(
            @Part("customers_firstname") RequestBody customers_firstname,
            @Part("customers_lastname") RequestBody customers_lastname,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("country_code") RequestBody country_code,
            @Part("customers_telephone") RequestBody customers_picture);

/*
    @Multipart
    @POST("processregistration")
    Call<UserData> processRegistration(
            @Part("customers_firstname") RequestBody customers_firstname,
            @Part("customers_lastname") RequestBody customers_lastname,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("country_code") RequestBody country_code,
            @Part("customers_gender") RequestBody customers_gender,
            @Part("customers_telephone") RequestBody customers_picture,
            @Part("customers_dob") RequestBody customers_dob);
*/

    @FormUrlEncoded
    @POST("processlogin")
    Call<UserData> processLogin(@Field("email") String customers_email_address,
                                @Field("password") String customers_password);

    @FormUrlEncoded
    @POST("checkuser")
    Call<NumberLoginResponse> processPhoneLogin(@Field("phone")String phone);

    @FormUrlEncoded
    @POST("getuser")
    Call<UserData> getUserDataPhoneLogin(@Field("phone")String phone);

    @FormUrlEncoded
    @POST("facebookregistration")
    Call<UserData> facebookRegistration(@Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("googleregistration")
    Call<UserData> googleRegistration(@Field("idToken") String idToken,
                                      @Field("customers_id") String userId,
                                      @Field("givenName") String givenName,
                                      @Field("familyName") String familyName,
                                      @Field("email") String email,
                                      @Field("imageUrl") String imageUrl);

    @FormUrlEncoded
    @POST("processforgotpassword")
    Call<UserData> processForgotPassword(@Field("email") String customers_email_address);

    @FormUrlEncoded
    @POST("updatecustomerinfo")
    Call<UserData> updateCustomerInfo(@Field("customers_id") String customers_id,
                                      @Field("customers_firstname") String customers_firstname,
                                      @Field("customers_lastname") String customers_lastname,
                                      @Field("customers_gender") String customers_gender,
                                      @Field("customers_telephone") String customers_telephone,
                                      @Field("customers_email") String customers_email,
                                      @Field("current_email") String current_email,
                                      /*@Field("customers_dob") String customers_dob,*/
                                      @Field("image_id") String image_id);


    //******************** Address Data ********************//

    @POST("getcountries")
    Call<Countries> getCountries();

    @FormUrlEncoded
    @POST("getzones")
    Call<Zones> getZones(@Field("zone_country_id") String zone_country_id);

    @FormUrlEncoded
    @POST("getalladdress")
    Call<AddressData> getAllAddress(@Field("customers_id") String customers_id);

    @FormUrlEncoded
    @POST("addshippingaddress")
    Call<AddressData> addUserAddress(@Field("customers_id") String customers_id,
                                     @Field("entry_firstname") String entry_firstname,
                                     @Field("entry_lastname") String entry_lastname,
                                     @Field("entry_street_address") String entry_street_address,
                                     @Field("entry_postcode") String entry_postcode,
                                     @Field("entry_city") String entry_city,
                                     @Field("entry_country_id") String entry_country_id,
                                     @Field("entry_zone_id") String entry_zone_id,
                                     @Field("is_default") String customers_default_address_id,
                                     @Field("entry_latitude") String latitude,
                                     @Field("entry_longitude") String longitude);

    @FormUrlEncoded
    @POST("updateshippingaddress")
    Call<AddressData> updateUserAddress(@Field("customers_id") String customers_id,
                                        @Field("address_id") String address_id,
                                        @Field("entry_firstname") String entry_firstname,
                                        @Field("entry_lastname") String entry_lastname,
                                        @Field("entry_street_address") String entry_street_address,
                                        @Field("entry_postcode") String entry_postcode,
                                        @Field("entry_city") String entry_city,
                                        @Field("entry_country_id") String entry_country_id,
                                        @Field("entry_zone_id") String entry_zone_id,
                                        @Field("is_default") String customers_default_address_id,
                                        @Field("entry_latitude") String latitude,
                                        @Field("entry_longitude") String longitude);

    @FormUrlEncoded
    @POST("updatedefaultaddress")
    Call<AddressData> updateDefaultAddress(@Field("customers_id") String customers_id,
                                           @Field("address_book_id") String address_book_id);

    @FormUrlEncoded
    @POST("deleteshippingaddress")
    Call<AddressData> deleteUserAddress(@Field("customers_id") String customers_id,
                                        @Field("address_book_id") String address_book_id);


    //******************** OrderProductCategory Data ********************//

    @FormUrlEncoded
    @POST("allcategories")
    Call<CategoryData> getAllCategories(@Field("language_id") int language_id);

    //******************** Product Data ********************//

    @POST("getallproducts")
    Call<ProductData> getAllProducts(@Body GetAllProducts getAllProducts);

    @POST("getPlans")
    Call<SubscriptionData> getPlans();

    @FormUrlEncoded
    @POST("getSlots")
    Call<SlotData> getSlots(@Field("date") String date);

    @POST("getsubscriptions")
    Call<SubscriptionHistory> getSubscriptionHistory(@Body Subscription_history_data data);

    @POST("cancelsubscription")
    Call<SubscriptionHistory> cancelSubscription(@Body CancelSubscriptionData data);

    @POST("lastestProducts")
    Call<ProductData> getNewProducts(@Body GetAllProducts getAllProducts);

    @POST("getquantity")
    Call<ProductStock> getProductStock(@Body GetStock getStock);

    @FormUrlEncoded
    @POST("likeproduct")
    Call<ProductData> likeProduct(@Field("liked_products_id") int liked_products_id,
                                  @Field("liked_customers_id") String liked_customers_id);

    @FormUrlEncoded
    @POST("unlikeproduct")
    Call<ProductData> unlikeProduct(@Field("liked_products_id") int liked_products_id,
                                    @Field("liked_customers_id") String liked_customers_id);

    @FormUrlEncoded
    @POST("getfilters")
    Call<FilterData> getFilters(@Field("categories_id") String categories_id,
                                @Field("language_id") int language_id);

    @FormUrlEncoded
    @POST("getsearchdata")
    Call<SearchData> getSearchData(@Field("searchValue") String searchValue,
                                   @Field("language_id") int language_id,
                                   @Field("currency_code") String currency_code,
                                   @Field("user_zip")String zipcode);

    //******************** News Data ********************//

    @FormUrlEncoded
    @POST("getallnews")
    Call<NewsData> getAllNews(@Field("language_id") int language_id,
                              @Field("page_number") int page_number,
                              @Field("is_feature") int is_feature,
                              @Field("categories_id") String categories_id);

    @FormUrlEncoded
    @POST("allnewscategories")
    Call<NewsCategoryData> allNewsCategories(@Field("language_id") int language_id,
                                             @Field("page_number") int page_number);

    //******************** Order Data ********************//

    @POST("addtoorder")
    Call<OrderData> addToOrder(@Body PostOrder postOrder);

    @POST("updatepayment")
    Call<OrderData> updatePaymentStatus(@Query("paymentId") String paymentId,
                                        @Query("orderId") long orderId);

    @FormUrlEncoded
    @POST("getorders")
    Call<OrderData> getOrders(@Field("customers_id") String customers_id,
                              @Field("language_id") int language_id,
                              @Field("currency_code") String currency_code);

    @FormUrlEncoded
    @POST("updatestatus")
    Call<OrderData> updatestatus(@Field("customers_id") String customers_id,
                                 @Field("orders_id") int orders_id);

    @FormUrlEncoded
    @POST("getcoupon")
    Call<CouponsData> getCouponInfo(@Field("code") String code,
                                    @Field("cart_amount") String cart_amount);

    @FormUrlEncoded
    @POST("getpaymentmethods")
    Call<PaymentMethodsData> getPaymentMethods(@Field("language_id") int language_id);

    @GET("generatebraintreetoken")
    Call<GetBrainTreeToken> generateBraintreeToken();

    //******************** Banner Data ********************//

    @GET("getbanners")
    Call<OfferData> getOffers(@Query("languages_id") int languages_id);
    @GET("getHomeSlider")
    Call<BannerData> getBanners(@Query("languages_id") int languages_id);

    //******************** Tax & Shipping Data ********************//

    @POST("getrate")
    Call<ShippingRateData> getShippingMethodsAndTax(@Body PostTaxAndShippingData postTaxAndShippingData);

    //******************** Contact Us Data ********************//

    @FormUrlEncoded
    @POST("contactus")
    Call<ContactUsData> contactUs(@Field("name") String name,
                                  @Field("email") String email,
                                  @Field("message") String message);

    //******************** Languages Data ********************//

    @GET("getlanguages")
    Call<LanguageData> getLanguages();

    //******************** App Settings Data ********************//

    @GET("sitesetting")
    Call<AppSettingsData> getAppSetting();


    //******************** Static Pages Data ********************//

    @FormUrlEncoded
    @POST("getallpages")
    Call<PagesData> getStaticPages(@Field("language_id") int language_id);

    //******************** Notifications Data ********************//

    @FormUrlEncoded
    @POST("registerdevices")
    Call<UserData> registerDeviceToFCM(@Field("device_id") String device_id,
                                       @Field("device_type") String device_type,
                                       @Field("ram") String ram,
                                       @Field("processor") String processor,
                                       @Field("device_os") String device_os,
                                       @Field("location") String location,
                                       @Field("device_model") String device_model,
                                       @Field("manufacturer") String manufacturer,
                                       @Field("customers_id") String customers_id);

    @FormUrlEncoded
    @POST("notify_me")
    Call<ContactUsData> notify_me(@Field("is_notify") String is_notify,
                                  @Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("givereview")
    Call<GiveRating> giveRating(@FieldMap Map<String, String> stringMap);

    @GET("getreviews")
    Call<GetRatings> getProductReviews(@Query("products_id") String product_id,
                                       @Query("languages_id") String languages_id);

    // This Api will give us City bounds
    @GET("getlocation")
    Call<GoogleAPIResponse> getCityBounds(@Query(value = "address", encoded = true) String address);

    // Upload Image
    @Multipart
    @POST("uploadimage")
    Call<UploadImageModel> uploadImage(@Part MultipartBody.Part filePart);

    // Update Password
    @GET("updatepassword")
    Call<UserData> updatePassword(@Query("oldpassword") String oldPassword,
                                  @Query("newpassword") String newPassword,
                                  @Query("customers_id") String customers_id);

    //Change Currency
    @GET("getcurrencies")
    Call<CurrencyModel> getCurrency();

    @GET("generatpaytmhashes")
    Call<Checksum> getPayTMChecksum(
            @Query("customers_id") String customerID,
            @Query("amount") String totalAmount
    );

}
