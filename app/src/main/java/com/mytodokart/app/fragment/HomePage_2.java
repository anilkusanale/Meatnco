package com.mytodokart.app.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.tabs.TabLayout;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.adapters.ViewPagerSimpleAdapter;
import com.mytodokart.app.app.App;
import com.mytodokart.app.app.MyAppPrefsManager;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.customs.DialogLoader;
import com.mytodokart.app.databases.User_Cart_DB;
import com.mytodokart.app.models.banner_model.BannerDetails;
import com.mytodokart.app.models.category_model.CategoryDetails;
import com.mytodokart.app.network.StartAppRequests;
import com.mytodokart.app.utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import ru.nikartm.support.ImageBadgeView;


public class HomePage_2 extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    EditText  edittextSearchZipcode;
    String postalCode;
    String globalZipcode = "";
    FragmentManager fragmentManager;
    static ImageBadgeView imageBadgeView;

    StartAppRequests startAppRequests;

    List<BannerDetails> bannerImages = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();
    List<CategoryDetails> allSubCategoriesList = new ArrayList<>();
    ViewPagerSimpleAdapter viewPagerAdapter;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (viewPagerAdapter != null)
                viewPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homepage_2, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(ConstantValues.APP_HEADER);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        // noInternetDialog.show();

        startAppRequests = new StartAppRequests(getContext());

        // Get BannersList from ApplicationContext
        bannerImages = ((App) getContext().getApplicationContext()).getBannersList();

        // Get CategoriesList from AppContext
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();


        // Binding Layout Views
        viewPager = (ViewPager) rootView.findViewById(R.id.myViewPager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        edittextSearchZipcode = rootView.findViewById(R.id.etSearchZipcode);
        imageBadgeView = rootView.findViewById(R.id.imageBadgeView);


        edittextSearchZipcode.setFocusable(false);
        final List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG,
                Place.Field.NAME);

        edittextSearchZipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEnterPincode();
            }
        });


        fragmentManager = getFragmentManager();
        if (bannerImages.isEmpty() || allCategoriesList.isEmpty())
            new MyTask().execute();
        else
            continueSetup();

        if (MainActivity.currentPostalCode.equals("")) {
            //locationEnableCheck();
            dialogEnterPincode();
        } else {
            edittextSearchZipcode.setText(MainActivity.fulladdress);
            getFirstTimeData(MainActivity.currentPostalCode);
            //  dialogEnterPincode();
        }

        rootView.findViewById(R.id.ivSearchHome).setOnClickListener(new View.OnClickListener() {
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

        return rootView;

    }

    public static void cartCount() {

        if (new User_Cart_DB().getCartItems().size() > 0) {
            imageBadgeView.setVisibility(View.VISIBLE);
            imageBadgeView.setBadgeValue(new User_Cart_DB().getCartItems().size());
        } else {
            imageBadgeView.setVisibility(View.GONE);
        }

    }

    void getFirstTimeData(String currentPostalCode) {
        new MyAppPrefsManager(getContext()).getUserZipcode();
//            String codes=new MyAppPrefsManager(getContext()).getPincode();
        String pincode = "590001";

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

    void dialogEnterPincode() {
        final AlertDialog builder = new AlertDialog.Builder(getActivity()).create();
        builder.setTitle("");
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
                    //continueSetup();
                    zipcodeSearch();
                    // Setup ViewPagers
                    setupViewPager(viewPager);
                    setupCustomTabs();
                }
            }
        });
        builder.show();
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

    private void continueSetup() {
        // Get BannersList from ApplicationContext
        bannerImages = ((App) getContext().getApplicationContext()).getBannersList();

        // Get CategoriesList from AppContext
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();

        allSubCategoriesList = new ArrayList<>();

        // Get SubCategoriesList from AllCategoriesList
        for (int i = 0; i < allCategoriesList.size(); i++) {
            if (!allCategoriesList.get(i).getParentId().equalsIgnoreCase("0")) {
                allSubCategoriesList.add(allCategoriesList.get(i));
            }
        }

        if (allSubCategoriesList.isEmpty())
            allSubCategoriesList = allCategoriesList;

        // Setup BannerSlider
        if (bannerImages != null && !bannerImages.isEmpty())
        setupBannerSlider(bannerImages);


        // Add corresponding ViewPagers to TabLayouts
        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);
        // Setup CustomTabs for all the Categories
        setupCustomTabs();


    }


    //*********** Setup the given ViewPager ********//

    private void setupCustomTabs() {

        // Initialize New View for custom Tab
        View tabOne = (View) LayoutInflater.from(getContext()).inflate(R.layout.layout_tabs_custom, null);

        // Set Text of custom Tab
        TextView tabText1 = (TextView) tabOne.findViewById(R.id.myTabs_text);
        tabText1.setText(getString(R.string.all));

        // Set Icon of custom Tab
        ImageView tabIcon1 = (ImageView) tabOne.findViewById(R.id.myTabs_icon);
        tabIcon1.setImageResource(R.drawable.ic_list);

        // Add tabOne to TabLayout at index 0
        tabLayout.getTabAt(0).setCustomView(tabOne);


        for (int i = 0; i < allSubCategoriesList.size(); i++) {

            // Initialize New View for custom Tab
            View tabNew = (View) LayoutInflater.from(getContext()).inflate(R.layout.layout_tabs_custom, null);

            // Set Text of custom Tab
            TextView tabText2 = (TextView) tabNew.findViewById(R.id.myTabs_text);
            tabText2.setText(allSubCategoriesList.get(i).getName());

            // Set Icon of custom Tab
            ImageView tabIcon2 = (ImageView) tabNew.findViewById(R.id.myTabs_icon);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            Glide.with(getActivity())
                    .setDefaultRequestOptions(options)
                    .asBitmap()
                   // .load(ConstantValues.ECOMMERCE_URL + allSubCategoriesList.get(i).getIcon())
                    .load("https://meatnco.in/images/media/2022/01/Zh9oU18307.png")
                    .into(tabIcon2);


            // Add tabTwo to TabLayout at specified index
            tabLayout.getTabAt(i + 1).setCustomView(tabNew);
        }
    }


    //*********** Setup the given ViewPager ********//

    private void setupViewPager(ViewPager viewPager) {

        // Initialize ViewPagerAdapter with ChildFragmentManager for ViewPager
        viewPagerAdapter = new ViewPagerSimpleAdapter(getChildFragmentManager());

        // Add the Fragments to the ViewPagerAdapter with TabHeader
        viewPagerAdapter.addFragment(new All_Products(), getString(R.string.all));


        for (int i = 0; i < allSubCategoriesList.size(); i++) {

            // Add CategoryID to new Bundle for Fragment arguments
            Bundle categoryInfo = new Bundle();
            categoryInfo.putInt("CategoryID", Integer.parseInt(allSubCategoriesList.get(i).getId()));

            // Initialize Category_Products Fragment with specified arguments
            Fragment fragment = new Category_Products();
            fragment.setArguments(categoryInfo);

            // Add the Fragments to the ViewPagerAdapter with TabHeader
            viewPagerAdapter.addFragment(fragment, allSubCategoriesList.get(i).getName());
        }

        // Set the number of pages that should be retained to either side of the current page
        viewPager.setOffscreenPageLimit(0);
        // Attach the ViewPagerAdapter to given ViewPager
        viewPager.setAdapter(viewPagerAdapter);
    }


    //*********** Setup the BannerSlider with the given List of BannerImages ********//

    private void setupBannerSlider(final List<BannerDetails> bannerImages) {
        FragmentManager fragmentManager = getFragmentManager();

        Fragment bannerStyle = null;

        switch (ConstantValues.DEFAULT_BANNER_STYLE) {
            case 0:
                bannerStyle = new BannerStyle1(bannerImages, allCategoriesList);
                break;
            case 1:
                bannerStyle = new BannerStyle1(bannerImages, allCategoriesList);
                break;
            case 2:
                bannerStyle = new BannerStyle2(bannerImages, allCategoriesList);
                break;
            case 3:
                bannerStyle = new BannerStyle3(bannerImages, allCategoriesList);
                break;
            case 4:
                bannerStyle = new BannerStyle4(bannerImages, allCategoriesList);
                break;
            case 5:
                bannerStyle = new BannerStyle5(bannerImages, allCategoriesList);
                break;
            case 6:
                bannerStyle = new BannerStyle6(bannerImages, allCategoriesList);
                break;
        }

        if (bannerStyle != null)
            fragmentManager.beginTransaction().replace(R.id.bannerFrameHome2, bannerStyle).commit();
    }



    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            // Check for Internet Connection from the static method of Helper class
            if (Utilities.hasActiveInternetConnection(getContext())) {

                // Call the method of StartAppRequests class to process App Startup Requests
                startAppRequests.RequestBanners();
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
        }

    }

}

