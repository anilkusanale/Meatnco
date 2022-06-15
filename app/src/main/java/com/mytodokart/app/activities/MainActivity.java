package com.mytodokart.app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.WrappedDrawable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.mytodokart.app.R;
import com.mytodokart.app.app.MyAppPrefsManager;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.customs.NotificationBadger;
import com.mytodokart.app.fragment.About;
import com.mytodokart.app.fragment.Billing_Address;
import com.mytodokart.app.fragment.Categories_1;
import com.mytodokart.app.fragment.Categories_2;
import com.mytodokart.app.fragment.Categories_3;
import com.mytodokart.app.fragment.Categories_4;
import com.mytodokart.app.fragment.Categories_5;
import com.mytodokart.app.fragment.Categories_6;
import com.mytodokart.app.fragment.CheckoutFinal;
import com.mytodokart.app.fragment.ContactUs;
import com.mytodokart.app.fragment.CurrencyFrag;
import com.mytodokart.app.fragment.ExtraSettings;
import com.mytodokart.app.fragment.HomePage_1;
import com.mytodokart.app.fragment.HomePage_2;
import com.mytodokart.app.fragment.Languages;
import com.mytodokart.app.fragment.MeFragment;
import com.mytodokart.app.fragment.My_Addresses;
import com.mytodokart.app.fragment.My_Cart;
import com.mytodokart.app.fragment.My_Orders;
import com.mytodokart.app.fragment.News;
import com.mytodokart.app.fragment.Product_Description;
import com.mytodokart.app.fragment.Products;
import com.mytodokart.app.fragment.SearchFragment;
import com.mytodokart.app.fragment.SettingsFragment;
import com.mytodokart.app.fragment.SettingsPannel;
import com.mytodokart.app.fragment.Shipping_Address;
import com.mytodokart.app.fragment.Shipping_Methods;
import com.mytodokart.app.fragment.SubscriptionPlans;
import com.mytodokart.app.fragment.Thank_You;
import com.mytodokart.app.fragment.Update_Account;
import com.mytodokart.app.fragment.WishList;
import com.mytodokart.app.models.GetDataOnClick;
import com.mytodokart.app.network.StartAppRequests;
import com.mytodokart.app.utils.GPSTracker;
import com.mytodokart.app.utils.LocaleHelper;
import com.mytodokart.app.utils.Utilities;
import com.razorpay.PaymentResultListener;
import com.toxsl.purchaseutils.FuelPurchaseActivity;

import org.json.JSONObject;
import org.jsoup.Jsoup;

public class MainActivity extends FuelPurchaseActivity implements PaymentResultListener {

    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    ActionBar actionBar;
    SharedPreferences sharedPreferences;
    MyAppPrefsManager myAppPrefsManager;
    LinearLayout homeLL, categoriesLL, ordersLL, moreLL;
    ImageView homeIV, categoresIV, ordersTV, moreIV;
    public static String mSelectedItem;
    private static final String SELECTED_ITEM_ID = "selected";
    //public ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean FLAG_YOUTUBE, FLAG_FAVORITE;
    private GPSTracker gpsTracker;
    public static String paymentNonceToken;
    public Fragment defaultHomeFragment;
    public Fragment currentFragment;
    //public HomePage_1 homePage_1;
    public HomePage_1 homePage_1;
    public Categories_1 categories_1;
    public Categories_2 categories_2;
    public Categories_3 categories_3;
    public Categories_4 categories_4;
    public Categories_5 categories_5;
    public Categories_6 categories_6;
    public Products newest;
    public Products topSellers;
    public Products superDeals;
    public Products mostLiked;
    public Update_Account update_account;
    public My_Orders myOrders;
    public My_Addresses myAddresses;
    public WishList myfavorites;
    public News news;
    public About about;
    public SubscriptionPlans subscriptionPlans;
    public ContactUs contactUs;
    public MeFragment meFragment;
    public ExtraSettings extraSettings;
    public SettingsPannel settingsPannel;
    private GetDataOnClick getDataOnClick;
    FragmentManager fragmentManager = getSupportFragmentManager();
    BottomNavigationView bottomNavigationView;

    ImageView ivHome, ivCategory, ivOrder, ivMore;
    TextView tvHome, tvCatgeory, tvOrder, tvMore;

    public static String currentPostalCode = "";
    public static String fulladdress = "";


    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (myAppPrefsManager.isFirstTimeLaunch()) {
            StartAppRequests.RegisterDeviceForFCM(MainActivity.this);
        }

        myAppPrefsManager.setFirstTimeLaunch(false);
    }

    //*********** Called when the Activity is first Created ********//

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setTheme(ConstantValues.THEME_ID);
        setContentView(R.layout.activity_main);

        ivHome = findViewById(R.id.homeIV);
        ivCategory = findViewById(R.id.categoresIV);
        ivOrder = findViewById(R.id.ordersTV);
        ivMore = findViewById(R.id.moreIV);

        tvHome = findViewById(R.id.tvHome);
        tvCatgeory = findViewById(R.id.tvCategory);
        tvOrder = findViewById(R.id.tvOrder);
        tvMore = findViewById(R.id.tvMore);

        myAppPrefsManager = new MyAppPrefsManager(MainActivity.this);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        // Binding Layout Views
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        homeLL = findViewById(R.id.homeLL);
        ordersLL = findViewById(R.id.ordersLL);
        categoriesLL = findViewById(R.id.categoriesLL);
        moreLL = findViewById(R.id.moreLL);
        categoresIV = findViewById(R.id.categoresIV);
        homeIV = findViewById(R.id.homeIV);
        ordersTV = findViewById(R.id.ordersTV);
        moreIV = findViewById(R.id.moreIV);
        getDataOnClick = GetDataOnClick.getInstance();
        // Get ActionBar and Set the Title and HomeButton of Toolbar
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(ConstantValues.APP_HEADER);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // Check BackStackEntryCount of FragmentManager
                FragmentManager fm = getSupportFragmentManager();
                HomePage_1.cartCount();
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                    getSupportActionBar().show();
                    actionBar.setHomeButtonEnabled(true);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(null);
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    findViewById(R.id.custom_bottom_nav).setVisibility(View.GONE);
//                    (Categories_4)getFragmentManager().findFragmentById(R.id.main_fragment).isVisible();
                } else if (getCurrentFragment() instanceof HomePage_1) {
                    getSupportActionBar().hide();
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_home_logo_foreground);
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    findViewById(R.id.custom_bottom_nav).setVisibility(View.VISIBLE);
                } else if (fm.findFragmentById(R.id.main_fragment) instanceof Categories_4
                        || fm.findFragmentById(R.id.main_fragment) instanceof My_Orders
                        || fm.findFragmentById(R.id.main_fragment) instanceof SettingsFragment && !(getCurrentFragment() instanceof HomePage_1)) {
                    actionBar.setHomeButtonEnabled(false);
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setHomeAsUpIndicator(null);
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    findViewById(R.id.custom_bottom_nav).setVisibility(View.VISIBLE);

                } else {
                    // Set DrawerToggle Indicator and default ToolbarNavigationClickListener
                    actionBar.setTitle(ConstantValues.APP_HEADER);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_home_logo_foreground);
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    findViewById(R.id.custom_bottom_nav).setVisibility(View.VISIBLE);

                }
                setupTitle();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                //  ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionCategories));
                if (fm.findFragmentById(R.id.main_fragment) instanceof My_Cart) {
                    fm.popBackStack();

                    getSupportActionBar().setTitle(getString(R.string.actionCategories));
                } else if (fm.findFragmentById(R.id.main_fragment) instanceof Products || fm.findFragmentById(R.id.main_fragment) instanceof SearchFragment) {
                    MainActivity.super.onBackPressed();
                } else if (fm.findFragmentById(R.id.main_fragment) instanceof Product_Description) {
                    getSupportActionBar().setTitle(getString(R.string.actionCategories));
                    MainActivity.super.onBackPressed();
                } else if (fm.findFragmentById(R.id.main_fragment) instanceof Categories_4
                        || fm.findFragmentById(R.id.main_fragment) instanceof My_Orders
                        || fm.findFragmentById(R.id.main_fragment) instanceof SettingsFragment) {
                    showHomePage();
                } else {
                    MainActivity.super.onBackPressed();
                }
            }
        });

        //setupDefaultHomePage();
        //new GetVersionCode().execute();
        checkUpdate();
        setupDefaultHomePage();

        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportActionBar().hide();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher_home_logo_foreground);

                if (homePage_1 == null) {
                    homePage_1 = new HomePage_1();
                    if (currentFragment == null)
                        fragmentManager.beginTransaction()
                                .add(R.id.main_fragment, homePage_1)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                    else
                        fragmentManager.beginTransaction()
                                .hide(currentFragment)
                                .add(R.id.main_fragment, homePage_1)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();

                } else {
                    fragmentManager.beginTransaction().hide(currentFragment).show(homePage_1).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                }
                currentFragment = homePage_1;

                homeIV.setImageResource(R.drawable.ic_home_red);
                ivCategory.setImageResource(R.drawable.ic_category);
                ivOrder.setImageResource(R.drawable.ic_order);
                ivMore.setImageResource(R.drawable.ic_more);

                tvHome.setTextColor(Color.parseColor("#FF3322"));
                tvCatgeory.setTextColor(Color.parseColor("#000000"));
                tvOrder.setTextColor(Color.parseColor("#000000"));
                tvMore.setTextColor(Color.parseColor("#000000"));

            }
        });

        categoriesLL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                actionBar.setHomeAsUpIndicator(null);


                if (categories_4 == null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isHeaderVisible", false);
                    categories_4 = new Categories_4();
                    categories_4.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .add(R.id.main_fragment, categories_4, getString(R.string.categoryStyle4))
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                } else {
                    fragmentManager.beginTransaction().hide(currentFragment).show(categories_4).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                }
                currentFragment = categories_4;
                //  drawerLayout.closeDrawers();
                actionBar.setTitle(getString(R.string.actionCategories));

                homeIV.setImageResource(R.drawable.ic_home);
                ivCategory.setImageResource(R.drawable.ic_category_red);
                ivOrder.setImageResource(R.drawable.ic_order);
                ivMore.setImageResource(R.drawable.ic_more);

                tvHome.setTextColor(Color.parseColor("#000000"));
                tvCatgeory.setTextColor(Color.parseColor("#FF3322"));
                tvOrder.setTextColor(Color.parseColor("#000000"));
                tvMore.setTextColor(Color.parseColor("#000000"));


            }
        });

        ordersLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                actionBar.setHomeAsUpIndicator(null);


                if (ConstantValues.IS_USER_LOGGED_IN) {
                    if (myOrders == null) {
                        myOrders = new My_Orders();
                        fragmentManager.beginTransaction()
                                .hide(currentFragment)
                                .add(R.id.main_fragment, myOrders)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                    } else {
                        fragmentManager.beginTransaction().hide(currentFragment).show(myOrders).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                    }
                    currentFragment = myOrders;
                    // drawerLayout.closeDrawers();
                    actionBar.setTitle(getString(R.string.actionOrders));


                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                }

                homeIV.setImageResource(R.drawable.ic_home);
                ivCategory.setImageResource(R.drawable.ic_category);
                ivOrder.setImageResource(R.drawable.ic_order_red);
                ivMore.setImageResource(R.drawable.ic_more);

                tvHome.setTextColor(Color.parseColor("#000000"));
                tvCatgeory.setTextColor(Color.parseColor("#000000"));
                tvOrder.setTextColor(Color.parseColor("#FF3322"));
                tvMore.setTextColor(Color.parseColor("#000000"));

            }
        });
        moreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                actionBar.setHomeAsUpIndicator(null);

                SettingsFragment settingsFragment = new SettingsFragment();

                getSupportFragmentManager().beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.main_fragment, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

                currentFragment = settingsFragment;

                homeIV.setImageResource(R.drawable.ic_home);
                ivCategory.setImageResource(R.drawable.ic_category);
                ivOrder.setImageResource(R.drawable.ic_order);
                ivMore.setImageResource(R.drawable.ic_more_red);

                tvHome.setTextColor(Color.parseColor("#000000"));
                tvCatgeory.setTextColor(Color.parseColor("#000000"));
                tvOrder.setTextColor(Color.parseColor("#000000"));
                tvMore.setTextColor(Color.parseColor("#FF3322"));

            }
        });

    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {

            String newVersion = null;
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            String currentVersion = "";
            Log.e(TAG, "onPostExecute: " + onlineVersion);
            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            currentVersion = currentVersion.replace(".", "");
            System.out.println("update : Current version " + currentVersion + " playstore version " + onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                //currentVersion = "113";
                onlineVersion = onlineVersion.replace(".", "");
                if (Integer.valueOf(currentVersion) < Integer.valueOf(onlineVersion)) {
                    dialogUpdateApp();
                } else {
                    setupDefaultHomePage();
                }
            }

        }
    }

    void dialogUpdateApp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New version available")
                .setMessage("Please update app to new version to continue.")
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mytodokart.app")));

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setupDefaultHomePage() {
        homePage_1 = new HomePage_1();
        getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, homePage_1, getString(R.string.homeStyle1)).commit();
        currentFragment = homePage_1;
        defaultHomeFragment = homePage_1;
    }

    private void showHomePage() {

        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher_home_logo_foreground);

        getSupportFragmentManager().beginTransaction().hide(currentFragment).show(defaultHomeFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        currentFragment = defaultHomeFragment;

        actionBar.setTitle(getString(R.string.app_name));

        homeIV.setImageResource(R.drawable.ic_home_red);
        ivCategory.setImageResource(R.drawable.ic_category);
        ivOrder.setImageResource(R.drawable.ic_order);
        ivMore.setImageResource(R.drawable.ic_more);

        tvHome.setTextColor(Color.parseColor("#FF3322"));
        tvCatgeory.setTextColor(Color.parseColor("#000000"));
        tvOrder.setTextColor(Color.parseColor("#000000"));
        tvMore.setTextColor(Color.parseColor("#000000"));

    }

    private void setupTitle() {
        Fragment curruntFrag = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (curruntFrag instanceof My_Cart) {
            actionBar.setTitle(getString(R.string.actionCart));
        } else if (curruntFrag instanceof Shipping_Address) {
            actionBar.setTitle(getString(R.string.shipping_address));
        } else if (curruntFrag instanceof Billing_Address) {
            actionBar.setTitle(getString(R.string.billing_address));
        } else if (curruntFrag instanceof Shipping_Methods) {
            actionBar.setTitle(getString(R.string.shipping_method));
        } else if (curruntFrag instanceof Update_Account) {
            actionBar.setTitle(getString(R.string.actionAccount));
        } else if (curruntFrag instanceof My_Orders) {
            actionBar.setTitle(getString(R.string.actionOrders));
        } else if (curruntFrag instanceof My_Addresses) {
            actionBar.setTitle(getString(R.string.actionAddresses));
        } else if (curruntFrag instanceof WishList) {
            actionBar.setTitle(getString(R.string.actionFavourites));
        } else if (curruntFrag instanceof News) {
            actionBar.setTitle(getString(R.string.actionNews));
        } else if (curruntFrag instanceof ContactUs) {
            actionBar.setTitle(getString(R.string.actionContactUs));
        } else if (curruntFrag instanceof About) {
            actionBar.setTitle(getString(R.string.actionAbout));
        } else if (curruntFrag instanceof SettingsFragment) {
            actionBar.setTitle(getString(R.string.actionSettings));
        } else if (curruntFrag instanceof SubscriptionPlans) {
            actionBar.setTitle(getString(R.string.actionSubscription));
        } else if (curruntFrag instanceof Product_Description) {
            actionBar.setTitle(R.string.product_description);
        } else if (curruntFrag instanceof Thank_You) {
            actionBar.setTitle(R.string.order_confirmed);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the Selected NavigationDrawer Item
        outState.putString(SELECTED_ITEM_ID, mSelectedItem);
    }

    //*********** Set the Base Context for the ContextWrapper ********//
    @Override
    protected void attachBaseContext(Context newBase) {
        String languageCode = ConstantValues.LANGUAGE_CODE;
        if ("".equalsIgnoreCase(languageCode))
            languageCode = ConstantValues.LANGUAGE_CODE = "en";
        super.attachBaseContext(LocaleHelper.wrapLocale(newBase, languageCode));
    }

    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantValues.PURCHASE_RESULTCODE) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            log("data=" + purchaseData);
            if (purchaseData != null) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String orderId = jo.getString("orderId");
                    if (!orderId.equals("")) {
                        getDataOnClick.sendDataFromMain(orderId);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to parse purchase data.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 500) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.homeStyle1));
            //Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            //Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.homeStyle1));
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

        }

    }


    //*********** Creates the Activity's OptionsMenu ********//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate toolbar_menu Menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Bind Menu Items
        MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
        MenuItem currencyItem = menu.findItem(R.id.toolbar_ic_currency);
        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
        languageItem.setVisible(false);
        currencyItem.setVisible(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView image = (ImageView) inflater.inflate(R.layout.layout_animated_ic_cart, null);

        Drawable itemIcon = cartItem.getIcon().getCurrent();
        image.setImageDrawable(itemIcon);
        cartItem.setActionView(image);
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to My_Cart Fragment
                Fragment fragment = new My_Cart();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }
        });
        Utilities.tintMenuIcon(MainActivity.this, languageItem, R.color.black);
        Utilities.tintMenuIcon(MainActivity.this, cartItem, R.color.black);
        return super.onCreateOptionsMenu(menu);
    }

    //*********** Prepares the OptionsMenu of Toolbar ********//
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (FLAG_FAVORITE) {
            MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
            MenuItem currencyItem = menu.findItem(R.id.toolbar_ic_currency);
            MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
            MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
            MenuItem youtubeItem = menu.findItem(R.id.toolbar_ic_favorite);

            youtubeItem.setVisible(true);
            languageItem.setVisible(false);
            searchItem.setVisible(false);
            cartItem.setVisible(true);
            currencyItem.setVisible(false);
        }

        if (FLAG_YOUTUBE) {
            MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
            MenuItem currencyItem = menu.findItem(R.id.toolbar_ic_currency);
            MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
            MenuItem youtubeItem = menu.findItem(R.id.toolbar_ic_favorite);
            MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
            youtubeItem.setVisible(true);
            languageItem.setVisible(false);
            currencyItem.setVisible(false);
            searchItem.setVisible(false);
            cartItem.setVisible(false);
            FLAG_YOUTUBE = false;
        } else {

            MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
            int cartSize = My_Cart.getCartSize();
            if (cartSize > 0) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_icon);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(1);
                cartItem.getActionView().startAnimation(animation);
                cartItem.getActionView().setAnimation(null);
                LayerDrawable icon = null;
                Drawable drawable = cartItem.getIcon();
                if (drawable instanceof DrawableWrapper) {
                    drawable = ((DrawableWrapper) drawable).getWrappedDrawable();
                } else if (drawable instanceof WrappedDrawable) {
                    drawable = ((WrappedDrawable) drawable).getWrappedDrawable();
                }

                if (drawable instanceof LayerDrawable) {
                    icon = (LayerDrawable) drawable;
                } else if (drawable instanceof DrawableWrapper) {
                    DrawableWrapper wrapper = (DrawableWrapper) drawable;
                    if (wrapper.getWrappedDrawable() instanceof LayerDrawable) {
                        icon = (LayerDrawable) wrapper.getWrappedDrawable();
                    }
                }

                if (icon != null)
                    NotificationBadger.setBadgeCount(this, icon, String.valueOf(cartSize));
            } else {
                cartItem.setIcon(R.drawable.ic_cart_empty);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
    //*********** Called whenever an Item in OptionsMenu is Selected ********//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof Thank_You) {
                    getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                break;
            case R.id.toolbar_ic_language:
                fragment = new Languages();
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;
            case R.id.toolbar_ic_currency:
                fragment = new CurrencyFrag();
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;
            case R.id.toolbar_ic_search:
                fragment = new SearchFragment();

                fragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
                break;
            case R.id.toolbar_ic_cart:
                // Navigate to My_Cart Fragment
                fragment = new My_Cart();
                fragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
                break;
            case R.id.toolbar_ic_favorite:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();

        HomePage_1.cartCount();
        if (fm.findFragmentById(R.id.main_fragment) instanceof Thank_You) {

            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (fm.findFragmentById(R.id.main_fragment) instanceof Product_Description && getCurrentFragment() instanceof Categories_4 ||
                fm.findFragmentById(R.id.main_fragment) instanceof My_Cart && getCurrentFragment() instanceof Categories_4) {
            getSupportActionBar().setTitle(getString(R.string.actionCategories));
            fm.popBackStack();
        } else if (fm.findFragmentById(R.id.main_fragment) instanceof Products && getCurrentFragment() instanceof Categories_4) {
           // getSupportActionBar().setTitle(getString(R.string.actionCategories));
            fm.popBackStack();
        } else if (fm.findFragmentById(R.id.main_fragment) instanceof My_Cart) {
            fm.popBackStack();
//            Top_Seller.refreshlist();
//            Most_Liked.refreshlist();
            HomePage_1.cartCount();
        } else if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            if (currentFragment == defaultHomeFragment)
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.super.onBackPressed();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            else
                showHomePage();

        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        paymentNonceToken = s;
        CheckoutFinal checkoutFinal = (CheckoutFinal) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        if (checkoutFinal != null && checkoutFinal.isVisible()) {
            checkoutFinal.paymentNonceToken = paymentNonceToken;
            // checkoutFinal.proceedOrder();
            System.out.println("Payment Id " + paymentNonceToken);
            checkoutFinal.updatePayment(paymentNonceToken);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast toast = Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private AppUpdateManager mAppUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private static final int RC_APP_UPDATE = 11;

    private void checkUpdate() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(@NonNull InstallState state) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                    popupSnackbarForCompleteUpdate();
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (mAppUpdateManager != null) {
                        mAppUpdateManager.unregisterListener(this);
                    }

                } else {
                    Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                }
            }
        };

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.e(TAG, "onSuccess: " + appUpdateInfo.updateAvailability());
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/)) {

                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, RC_APP_UPDATE);

                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                    popupSnackbarForCompleteUpdate();
                } else {
                    Log.e(TAG, "checkForAppUpdateAvailability: something else " + appUpdateInfo.availableVersionCode());
                }
            }
        });
    }

    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        ivCategory,
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAppUpdateManager != null) {
                    mAppUpdateManager.completeUpdate();
                }
            }
        });

        //snackbar.setActionTextColor(getResources().getColor(R.color.install_color));
        snackbar.show();
    }

}

