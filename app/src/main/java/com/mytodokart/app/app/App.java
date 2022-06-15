package com.mytodokart.app.app;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import androidx.multidex.MultiDexApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.libraries.places.api.Places;
import com.google.common.io.BaseEncoding;
import com.google.firebase.FirebaseApp;
import com.mytodokart.app.models.OfferDetails;
import com.onesignal.OneSignal;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.databases.DB_Handler;
import com.mytodokart.app.databases.DB_Manager;
import com.mytodokart.app.enums.AppEnvironment;
import com.mytodokart.app.models.address_model.AddressDetails;
import com.mytodokart.app.models.banner_model.BannerDetails;
import com.mytodokart.app.models.category_model.CategoryDetails;
import com.mytodokart.app.models.device_model.AppSettingsDetails;
import com.mytodokart.app.models.drawer_model.Drawer_Items;
import com.mytodokart.app.models.pages_model.PagesDetails;
import com.mytodokart.app.models.product_model.ProductDetails;
import com.mytodokart.app.models.shipping_model.ShippingService;


/**
 * App extending Application, is used to save some Lists and Objects with Application Context.
 **/


public class App extends MultiDexApplication {

    // Application Context
    private static Context context;
    private static DB_Handler db_handler;


    private List<Drawer_Items> drawerHeaderList;
    private Map<Drawer_Items, List<Drawer_Items>> drawerChildList;
    
    
    private AppSettingsDetails appSettingsDetails = null;
    private List<BannerDetails> bannersList = new ArrayList<>();
    private List<OfferDetails> offersList = new ArrayList<>();
    private List<CategoryDetails> categoriesList = new ArrayList<>();
    private List<PagesDetails> staticPagesDetails = new ArrayList<>();

    private String tax = "";
    private ShippingService shippingService = null;
    private AddressDetails shippingAddress = new AddressDetails();
    private AddressDetails billingAddress = new AddressDetails();
    private ProductDetails productDetails = new ProductDetails();

    AppEnvironment appEnvironment;


    @Override
    public void onCreate() {
        super.onCreate();

        // set App Context
        context = this.getApplicationContext();
        appEnvironment = AppEnvironment.SANDBOX;
        FirebaseApp.initializeApp(this);
        Places.initialize(this,"AIzaSyB6yZRuBLY1kkBDatR2k_6GpDUCHUF7d5Q");

        // initialize DB_Handler and DB_Manager
        db_handler = new DB_Handler();
        DB_Manager.initializeInstance(db_handler);
        String pkg_name = context.getPackageName();
        ConstantValues.PKG_NAME = pkg_name;
        ConstantValues.SHA1 = getSHA1(pkg_name);
    
        if (ConstantValues.DEFAULT_NOTIFICATION.equalsIgnoreCase("onesignal")) {
            
            OneSignal.sendTag("app", "AndroidEcommerceDemo2");

            try {
                // initialize OneSignal
                OneSignal.startInit(this)
                        .filterOtherGCMReceivers(true)
                        .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.InAppAlert)
                        .unsubscribeWhenNotificationsAreDisabled(false)
                        .init();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    //*********** Returns Application Context ********//

    public static Context getContext() {
        return context;
    }


    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

    public List<Drawer_Items> getDrawerHeaderList() {
        return drawerHeaderList;
    }

    public void setDrawerHeaderList(List<Drawer_Items> drawerHeaderList) {
        this.drawerHeaderList = drawerHeaderList;
    }

    public Map<Drawer_Items, List<Drawer_Items>> getDrawerChildList() {
        return drawerChildList;
    }

    public void setDrawerChildList(Map<Drawer_Items, List<Drawer_Items>> drawerChildList) {
        this.drawerChildList = drawerChildList;
    }

    public AppSettingsDetails getAppSettingsDetails() {
        return appSettingsDetails;
    }
    
    public void setAppSettingsDetails(AppSettingsDetails appSettingsDetails) {
        this.appSettingsDetails = appSettingsDetails;
    }
    
    public List<BannerDetails> getBannersList() {
        return bannersList;
    }
    
    public void setBannersList(List<BannerDetails> bannersList) {
        this.bannersList = bannersList;
    }

    public List<OfferDetails> getOffersList() {
        return offersList;
    }

    public void setOffersList(List<OfferDetails> offersList) {
        this.offersList = offersList;
    }

    public List<CategoryDetails> getCategoriesList() {
        return categoriesList;
    }
    
    public void setCategoriesList(List<CategoryDetails> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public List<PagesDetails> getStaticPagesDetails() {
        return staticPagesDetails;
    }
    
    public void setStaticPagesDetails(List<PagesDetails> staticPagesDetails) {
        this.staticPagesDetails = staticPagesDetails;
    }

    public String getTax() {
        return tax;
    }
    
    public void setTax(String tax) {
        this.tax = tax;
    }
    
    public ShippingService getShippingService() {
        return shippingService;
    }
    
    public void setShippingService(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public AddressDetails getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressDetails shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public AddressDetails getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AddressDetails billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    
    private String getSHA1(String packageName){
        try {
            Signature[] signatures = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature signature: signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                return BaseEncoding.base16().encode(md.digest());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ProductDetails getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ProductDetails productDetails) {
        this.productDetails = productDetails;
    }
}


