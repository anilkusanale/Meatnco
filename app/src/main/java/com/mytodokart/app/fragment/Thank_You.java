package com.mytodokart.app.fragment;


import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/*
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
*/
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;

import am.appwise.components.ni.NoInternetDialog;


public class Thank_You extends Fragment {
    
    //AdView mAdView;
    FrameLayout banner_adView;
    Button order_status_btn, continue_shopping_btn;
    ActionBar actionBar;
    My_Cart my_cart;

    public Thank_You(My_Cart my_cart) {
        this.my_cart = my_cart;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.thank_you, container, false);

//        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
//        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//
//        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.order_confirmed));
////        ((MainActivity)getActivity()).getActionBar().setDisplayHomeAsUpEnabled(false);
////        ((MainActivity)getActivity()).getActionBar().setHomeButtonEnabled(false);
////        ((MainActivity)getActivity()).getActionBar().setDisplayShowHomeEnabled(false);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
       // noInternetDialog.show();

        // Binding Layout Views
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        order_status_btn = (Button) rootView.findViewById(R.id.order_status_btn);
        continue_shopping_btn = (Button) rootView.findViewById(R.id.continue_shopping_btn);
    
/*
        if (ConstantValues.IS_ADMOBE_ENABLED) {
            // Initialize Admobe
            mAdView = new AdView(getContext());
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(ConstantValues.AD_UNIT_ID_BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            banner_adView.addView(mAdView);
            mAdView.loadAd(adRequest);
        }
*/


        // Binding Layout Views
        order_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    
                // Navigate to My_Orders Fragment
                Fragment fragment = new My_Orders();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
                
            }
        });


        // Binding Layout Views
        continue_shopping_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to HomePage Fragment
                //getActivity().getSupportFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
               // ((MainActivity)getActivity()).onBackPressed();
//                for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()) {
//                    if (fragment instanceof HomePage_1) {
//                        continue;
//                    }
//                    else
////                    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//                    {
//                        getActivity().onBackPressed();
//                    }
//                }
                getActivity().finish();
                startActivity(new Intent(getContext(),MainActivity.class));

//                Fragment fragment = new HomePage_1();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().add(R.id.main_fragment, fragment).commit();



            }
        });

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if (item.getItemId() == android.R.id.home) {
            getFragmentManager().popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



}

