package com.mytodokart.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.constant.ConstantValues;

public class DrawerListFragment extends Fragment implements View.OnClickListener {

    View rootView;
    TextView my_account,myAddressesTV,mySubscriptionsTV,myFavouritesTV,introTV,newzTV,contactUsTV,aboutUsTV,shareAppTV
            ,rateAppTV,settingsTV,logoutTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_drawer_list, container, false);
        initUI();
        return rootView;
    }

    private void initUI() {
        my_account = rootView.findViewById(R.id.my_account);
        myAddressesTV = rootView.findViewById(R.id.myAddressesTV);
        mySubscriptionsTV = rootView.findViewById(R.id.mySubscriptionsTV);
        myFavouritesTV = rootView.findViewById(R.id.myFavouritesTV);
        introTV = rootView.findViewById(R.id.introTV);
        newzTV = rootView.findViewById(R.id.newzTV);
        contactUsTV = rootView.findViewById(R.id.contactUsTV);
        aboutUsTV = rootView.findViewById(R.id.aboutUsTV);
        shareAppTV = rootView.findViewById(R.id.shareAppTV);
        rateAppTV = rootView.findViewById(R.id.rateAppTV);
        settingsTV = rootView.findViewById(R.id.settingsTV);
        logoutTV = rootView.findViewById(R.id.logoutTV);
        my_account.setOnClickListener(this);
        myAddressesTV.setOnClickListener(this);
        mySubscriptionsTV.setOnClickListener(this);
        myFavouritesTV.setOnClickListener(this);
        introTV.setOnClickListener(this);
        newzTV.setOnClickListener(this);
        contactUsTV.setOnClickListener(this);
        aboutUsTV.setOnClickListener(this);
        shareAppTV.setOnClickListener(this);
        rateAppTV.setOnClickListener(this);
        settingsTV.setOnClickListener(this);
        logoutTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.my_account:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionAccount));
//                break;
//            case R.id.myAddressesTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionAddresses));
//                break;
//            case R.id.mySubscriptionsTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionSubscription));
//                break;
//            case R.id.myFavouritesTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionFavourites));
//                break;
//            case R.id.introTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionIntro));
//                break;
//            case R.id.newzTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionNews));
//                break;
//            case R.id.contactUsTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionContactUs));
//                break;
//            case R.id.aboutUsTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionAbout));
//                break;
//            case R.id.shareAppTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionShareApp));
//                break;
//            case R.id.rateAppTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionRateApp));
//                break;
//            case R.id.settingsTV:
//                ((MainActivity)getActivity()).drawerSelectedItemNavigation(getString(R.string.actionSettings));
//                break;
//            case R.id.logoutTV:
//                if (ConstantValues.IS_USER_LOGGED_IN) {
//                    ((MainActivity) getActivity()).drawerSelectedItemNavigation(getString(R.string.actionLogout));
//                } else {
//                    ((MainActivity) getActivity()).drawerSelectedItemNavigation(getString(R.string.actionLogin));
//                }
//                break;
//
//        }
    }
}
