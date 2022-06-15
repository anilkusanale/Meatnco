package com.mytodokart.app.fragment;

import static android.app.Activity.RESULT_OK;
import static com.google.android.libraries.places.widget.AutocompleteActivity.RESULT_ERROR;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.adapters.CustomAdapter;
import com.mytodokart.app.adapters.HomeOffersAdapter;
import com.mytodokart.app.adapters.ViewPagerCustomAdapter;
import com.mytodokart.app.app.App;
import com.mytodokart.app.app.MyAppPrefsManager;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.customs.DialogLoader;
import com.mytodokart.app.databases.User_Cart_DB;
import com.mytodokart.app.models.OfferDetails;
import com.mytodokart.app.models.banner_model.BannerDetails;
import com.mytodokart.app.models.category_model.CategoryDetails;
import com.mytodokart.app.network.StartAppRequests;
import com.mytodokart.app.utils.Utilities;
import com.rd.PageIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import am.appwise.components.ni.NoInternetDialog;
import ru.nikartm.support.ImageBadgeView;

public class HomePage_1 extends Fragment {

    View rootView;
    ViewPager viewPager1;
    StartAppRequests startAppRequests;
    List<BannerDetails> bannerImages = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();
    List<CategoryDetails> allCategoriesListMain = new ArrayList<>();
    ShimmerFrameLayout shimmerFrame;
    PageIndicatorView pageIndicatorView;
    FragmentManager fragmentManager;
    RecentlyViewed recentlyViewed;
    FlashSale flashSale;
    RecyclerView categoriesRV, offerRecyclerview;
    Top_Seller topSeller;
    EditText searchProductET, edittextSearchZipcode;
    ImageView searchIV;
    public Categories_4 categories_4;
    Special_Deals specialDeals;
    NewCategoriesAdapter adapter;
    Timer timer;
    final long DELAY_MS = 700;
    final long PERIOD_MS = 4000;
    HomePage_1 homePage_1;
    Fragment currentFragment;
    int currentPage = 0;
    LinearLayoutManager HorizontalLayout;
    Most_Liked mostLiked;
    String postalCode;
    ProgressBar progressBar;
    static ImageBadgeView imageBadgeView;
    String globalZipcode = "";
    List<OfferDetails> imagesList = new ArrayList<>();
    LinearLayout feaTab, newTab, topTab;
    DialogLoader dialogLoader;

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (topSeller != null && specialDeals != null && mostLiked != null && flashSale != null && recentlyViewed != null) {
                topSeller.invalidateProducts();
                // specialDeals.invalidateProducts();
                mostLiked.invalidateProducts();
                flashSale.invalidateProducts();
                // recentlyViewed.invalidateProducts();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homepage_1, container, false);
        progressBar = rootView.findViewById(R.id.progress_bar_home);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(ConstantValues.APP_HEADER);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        startAppRequests = new StartAppRequests(getContext());
        bannerImages = ((App) getContext().getApplicationContext()).getBannersList();
        allCategoriesListMain = ((App) getContext().getApplicationContext()).getCategoriesList();
        allCategoriesList.clear();
        int mc = 0;
        for (mc = 0; mc < allCategoriesListMain.size(); mc++) {
            if (allCategoriesListMain.get(mc).getParentId().matches("0")) {
                allCategoriesList.add(allCategoriesListMain.get(mc));
            }
        }

        viewPager1 = rootView.findViewById(R.id.viewpager);
        categoriesRV = rootView.findViewById(R.id.categoriesRV);
        offerRecyclerview = rootView.findViewById(R.id.offers_recyclerview);
        shimmerFrame = rootView.findViewById(R.id.shimmerFrame);
        pageIndicatorView = rootView.findViewById(R.id.pageIndicatorView);
        edittextSearchZipcode = rootView.findViewById(R.id.etSearchZipcode);
        imageBadgeView = rootView.findViewById(R.id.imageBadgeView);

        dialogLoader = new DialogLoader(getContext());

        edittextSearchZipcode.setFocusable(false);
        final List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG,
                Place.Field.NAME);

        edittextSearchZipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEnterPincode();
            }
        });

        fragmentManager = getFragmentManager();
        if (bannerImages.isEmpty() || allCategoriesList.isEmpty())
            new MyTask().execute();
        else
            continueSetup();

        rootView.findViewById(R.id.hmofferbanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundleInfo = new Bundle();
                bundleInfo.putString("sortBy", "special");
                bundleInfo.putBoolean("hideBottomBar", true);
                Fragment fragment;
                fragment = new All_Products();
                fragment.setArguments(bundleInfo);
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }
        });

//        edittextSearchZipcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    zipcodeSearch();
//                }
//                return false;
//            }
//        });

        /*
         * search by products click
         * */
        rootView.findViewById(R.id.searchProductET).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment, new SearchFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
            }
        });

        rootView.findViewById(R.id.cart_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment, new My_Cart())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }
        });

        cartCount();


        // offerRecyclerview.setVisibility(View.GONE);

        if (MainActivity.currentPostalCode.equals("")) {
            //locationEnableCheck();
            dialogEnterPincode();
        } else {
            edittextSearchZipcode.setText(MainActivity.fulladdress);
            getFirstTimeData(MainActivity.currentPostalCode);
            //  dialogEnterPincode();
        }


        return rootView;
    }


    void getFirstTimeData(String currentPostalCode) {
        new MyAppPrefsManager(getContext()).getUserZipcode();
//            String codes=new MyAppPrefsManager(getContext()).getPincode();
        String pincode = "590019";

        final DialogLoader dialogLoader = new DialogLoader(getContext());
        dialogLoader.showProgressDialog();

        MyAppPrefsManager prefsManager = new MyAppPrefsManager(getContext());
        prefsManager.setUserZipcode(currentPostalCode);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogLoader.hideProgressDialog();

            }
        }, 1000);

    }

    public static void cartCount() {

        if (new User_Cart_DB().getCartItems().size() > 0) {
            imageBadgeView.setVisibility(View.VISIBLE);
            imageBadgeView.setBadgeValue(new User_Cart_DB().getCartItems().size());
        } else {
            imageBadgeView.setVisibility(View.GONE);
        }

    }

    private void zipcodeSearch() {

        if (edittextSearchZipcode.getVisibility() == View.GONE) {
            edittextSearchZipcode.setVisibility(View.VISIBLE);
            edittextSearchZipcode.requestFocus();
        } else {
            try {
                hideSoftKeyboard(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }

            final DialogLoader dialogLoader = new DialogLoader(getContext());
            dialogLoader.showProgressDialog();
            MyAppPrefsManager prefsManager = new MyAppPrefsManager(getContext());
            prefsManager.setUserZipcode(globalZipcode);
            //progressBar.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialogLoader.hideProgressDialog();
//                            edittextSearchZipcode.setVisibility(View.GONE);
                }
            }, 1000);

        }

    }

    public void continueSetup() {
        bannerImages = ((App) getContext().getApplicationContext()).getBannersList();
        allCategoriesListMain = ((App) getContext().getApplicationContext()).getCategoriesList();
        int mc = 0;
        allCategoriesList.clear();
        for (mc = 0; mc < allCategoriesListMain.size(); mc++) {
            if (allCategoriesListMain.get(mc).getParentId().matches("0")) {
                allCategoriesList.add(allCategoriesListMain.get(mc));
            }
        }
        adapter = new NewCategoriesAdapter(getActivity(), allCategoriesList, false);
        HorizontalLayout = new GridLayoutManager(getActivity(), 2);
        categoriesRV.setLayoutManager(HorizontalLayout);
        categoriesRV.setAdapter(adapter);
        categoriesRV.setNestedScrollingEnabled(false);

        PagerAdapter adapter = new CustomAdapter(getActivity(), bannerImages);
        viewPager1.setAdapter(adapter);
        viewPager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == bannerImages.size()) {
                    currentPage = 0;
                }
                viewPager1.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
        imagesList = ((App) getContext().getApplicationContext()).getOffersList();

        HomeOffersAdapter homeOffersAdapter = new HomeOffersAdapter(getContext(), imagesList);
        offerRecyclerview.setAdapter(homeOffersAdapter);


    }

    //*********** Setup the given ViewPager ********//

    private void setupViewPagerOne(ViewPager viewPager) {

        // Initialize new Bundle for Fragment arguments
        Bundle bundle = new Bundle();
        bundle.putBoolean("isHeaderVisible", false);

        // Initialize Fragments
        topSeller = new Top_Seller();
        specialDeals = new Special_Deals();
        mostLiked = new Most_Liked();

        topSeller.setArguments(bundle);
        specialDeals.setArguments(bundle);
        mostLiked.setArguments(bundle);

        // Initialize ViewPagerAdapter with ChildFragmentManager for ViewPager
        ViewPagerCustomAdapter viewPagerCustomAdapter = new ViewPagerCustomAdapter(getChildFragmentManager());

        // Add the Fragments to the ViewPagerAdapter with TabHeader
        viewPagerCustomAdapter.addFragment(topSeller, getString(R.string.topSeller));
        viewPagerCustomAdapter.addFragment(specialDeals, getString(R.string.super_deals));
        viewPagerCustomAdapter.addFragment(mostLiked, getString(R.string.most_liked));
        viewPager.setOffscreenPageLimit(2);
        // Attach the ViewPagerAdapter to given ViewPager
        viewPager.setAdapter(viewPagerCustomAdapter);

    }
    //*********** Setup the BannerSlider with the given List of BannerImages ********//

    private void setupBannerSlider(final List<BannerDetails> bannerImages) {
        Fragment bannerStyle = null;
        bannerStyle = new BannerStyle1(bannerImages, allCategoriesList);

        if (bannerStyle != null)
            fragmentManager.beginTransaction().replace(R.id.bannerFrameHome1, bannerStyle).commit();
    }

    //*********** Handle the Click Listener on BannerImages of Slider ********//

    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            shimmerFrame.setVisibility(View.VISIBLE);
            shimmerFrame.startShimmer();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (Utilities.hasActiveInternetConnection(getContext())) {
                startAppRequests.RequestStaticPagesData();
                startAppRequests.RequestBanners();
                startAppRequests.RequestOffers();
                startAppRequests.RequestAllCategories();

                return "1";
            } else {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equalsIgnoreCase("1")) {
                continueSetup();
            }

            shimmerFrame.stopShimmer();
            shimmerFrame.setVisibility(View.GONE);
        }

    }

    private Boolean getLocationEnableStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Boolean gps_enabled = false;
        Boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }

        try {
            //assert lm != null
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }

        return !(!gps_enabled && !network_enabled);

    }

    private void locationEnableCheck() {

        if (getLocationEnableStatus()) {
            // location();
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                    Place.Field.LAT_LNG, Place.Field.ADDRESS);
            System.out.println("autolocation" + fields);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).setCountry("IN").build(getActivity());
            getActivity().startActivityForResult(intent, 500);

        } else {
            locationDialog();
            autoEnableLocation();
        }
    }

    private void location() {
        final LocationManager[] locationManager = {(LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE)};

        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                // Called when a new location is found by the network location provider.
                Toast.makeText(getContext(), "location is:" + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_LONG).show();
                if (postalCode.isEmpty()) {
                    geocoder(location.getLatitude(), location.getLongitude());
                } else
                    locationManager[0] = null;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e(">>>>", "chnged");
            }

            public void onProviderEnabled(String provider) {
                Log.e(">>>>", "enable");
            }

            public void onProviderDisabled(String provider) {
                locationDialog();
            }
        };

        locationManager[0].requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void locationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("")
                .setMessage("Location not enable")
                .setCancelable(false)
                .setPositiveButton("open location settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    void geocoder(Double latitude, Double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            new MyAppPrefsManager(getContext()).setUserPincode(postalCode);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void hideSoftKeyboard(Activity activity) {

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = getActivity().getCurrentFocus();
        /*
         * If no view is focused, an NPE will be thrown
         *
         * Maxim Dmitriev
         */
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 500) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    geo(place.getLatLng().latitude, place.getLatLng().longitude);
                }
            } else if (MainActivity.currentPostalCode.equals("")) {
//            locationEnableCheck();
                dialogEnterPincode();
            }
        } else if (resultCode == RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    void geo(double latitude, double longitude) {
//         DialogLoader loader=new DialogLoader(get);
//         loader.showProgressDialog();
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext().getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String postalCode = addresses.get(0).getPostalCode();

            if (postalCode == null) {
                Toast toast = Toast.makeText(getContext(), "Enter correct pincode", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                List<Place.Field> fields = Arrays.asList(Place.Field.PLUS_CODE
                        , Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN").build(getActivity());
                getActivity().startActivityForResult(intent, 500);
            } else {
                edittextSearchZipcode.setText(postalCode + "," + address);
                globalZipcode = postalCode;
                //loader.hideProgressDialog();
                MainActivity.currentPostalCode = postalCode;
                MainActivity.fulladdress = address + "," + postalCode;
                zipcodeSearch();
            }
        } else {
            Toast toast = Toast.makeText(getContext(), "No internet access", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            List<Place.Field> fields = Arrays.asList(Place.Field.PLUS_CODE
                    , Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN").build(getActivity());
            getActivity().startActivityForResult(intent, 500);
        }
    }

    void dialogEnterPincode() {
        final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();
        builder.setTitle("");
        // .setMessage("Kindly enter your area pincode for which you want to search products")
//                 .setCancelable(false);
//                 .setPositiveButton("Enter pincode", new DialogInterface.OnClickListener() {
//                     public void onClick(DialogInterface dialog, int which) {
//
//                         List<Place.Field> fields = Arrays.asList(Place.Field.PLUS_CODE
//                                 ,Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,Place.Field.ADDRESS);
//
//
//
//                         Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN").build(getActivity());
//                         getActivity().startActivityForResult(intent, 500);
//
//                        //    locationEnableCheck();
//
//                         dialog.dismiss();
//                     }
//                 })
//                 .setIcon(android.R.drawable.ic_dialog_alert)
//                 .show();
        builder.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View cv = inflater.inflate(R.layout.pincode_layout, null, false);
        builder.setView(cv);
        final EditText pincode = cv.findViewById(R.id.pincode);
        final TextView errMsg = cv.findViewById(R.id.errorMsg);
        Button submitBtn = cv.findViewById(R.id.pincode_submit);
        errMsg.setVisibility(View.GONE);
        pincode.setText(MainActivity.currentPostalCode);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errMsg.setVisibility(View.GONE);
                String epin = pincode.getText().toString();
                if (epin.equalsIgnoreCase("") || epin.length() < 6) {
                    errMsg.setText("Enter valid pincode");
                    errMsg.setVisibility(View.VISIBLE);
                } else {
                    builder.dismiss();
                    postalCode = epin;
                    edittextSearchZipcode.setText(epin);
                    globalZipcode = epin;
                    //loader.hideProgressDialog();
                    MainActivity.currentPostalCode = epin;
                    MainActivity.fulladdress = epin;
                    zipcodeSearch();
                }
            }
        });
        builder.show();
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
//                            Toast.makeText(getContext(),"onConnected",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            //    Toast.makeText(getContext(),"onConnectionSuspended",Toast.LENGTH_SHORT).show();
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {

                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });


    }

    void autoEnableLocation() {
        getActivity().setFinishOnTouchOutside(true);

        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getContext())) {
            Toast.makeText(getContext(), "Gps already enabled", Toast.LENGTH_SHORT).show();
        }

        if (!hasGPSDevice(getContext())) {
            Toast.makeText(getContext(), "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(getContext())) {
            Log.e("TAG", "Gps already enabled");
            Toast.makeText(getContext(), "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
            Log.e("TAG", "Gps already enabled");
            Toast.makeText(getContext(), "Gps already enabled", Toast.LENGTH_SHORT).show();
        }


    }
}

