package com.mytodokart.app.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.mytodokart.app.R;
import com.mytodokart.app.adapters.CustomAdapter;
import com.mytodokart.app.app.App;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.models.banner_model.BannerDetails;
import com.mytodokart.app.models.category_model.CategoryDetails;
import com.mytodokart.app.network.StartAppRequests;
import com.mytodokart.app.utils.Utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewHomePage_1 extends Fragment {

    View rootView;
    ViewPager viewPager;
    ViewPager viewPager1;
    RecyclerView categoriesRV;
    TabLayout tabLayout;
    StartAppRequests startAppRequests;
    List<BannerDetails> bannerImages = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();
    FragmentManager fragmentManager;
    RecentlyViewed recentlyViewed;
    Products productsFragment;
    FlashSale flashSale;
    Top_Seller topSeller;
    Special_Deals specialDeals;
    NewCategoriesAdapter adapter;
    Most_Liked mostLiked;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    int currentPage = 0;
    int NUM_PAGES = 4;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    LinearLayoutManager HorizontalLayout;


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (topSeller != null && specialDeals != null && mostLiked != null && flashSale != null && recentlyViewed != null) {
                topSeller.invalidateProducts();
                specialDeals.invalidateProducts();
                mostLiked.invalidateProducts();
                flashSale.invalidateProducts();
                //recentlyViewed.invalidateProducts();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.newhomepage_1, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(ConstantValues.APP_HEADER);
        /*NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        noInternetDialog.show();*/
        startAppRequests = new StartAppRequests(getContext());
        bannerImages = ((App) getContext().getApplicationContext()).getBannersList();
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();
        viewPager = rootView.findViewById(R.id.viewpager);
        categoriesRV = rootView.findViewById(R.id.categoriesRV);
        fragmentManager = getFragmentManager();

        // Add RecentlyViewed Fragment to specified FrameLayout
        recentlyViewed = new RecentlyViewed();
        fragmentManager.beginTransaction().replace(R.id.recently_viewed_fragment, recentlyViewed).commit();

        // Add FlashSale Fragment to specific FrameLayout.
        Bundle flashBundle = new Bundle();
        flashSale = new FlashSale();
        flashBundle.putBoolean("isHeaderVisible", true);
        flashSale.setArguments(flashBundle);
        fragmentManager.beginTransaction().replace(R.id.flash_sale_fragment, flashSale).commit();

        if (bannerImages.isEmpty() || allCategoriesList.isEmpty())
            new MyTask().execute();
        else
            continueSetup();
        return rootView;
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (Utilities.hasActiveInternetConnection(getContext())) {
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

    public void continueSetup() {
        bannerImages = ((App) getContext().getApplicationContext()).getBannersList();
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();

        adapter = new NewCategoriesAdapter(getActivity(),allCategoriesList,false);
        HorizontalLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        categoriesRV.setLayoutManager(HorizontalLayout);
        categoriesRV.setAdapter(adapter);

        PagerAdapter adapter = new CustomAdapter(getActivity(),bannerImages);
        viewPager.setAdapter(adapter);
        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES-1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

    }
}
