package com.mytodokart.app.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mytodokart.app.R;

import java.util.ArrayList;
import java.util.List;

import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.adapters.CategoryListAdapter_4;
import com.mytodokart.app.app.App;
import com.mytodokart.app.models.category_model.CategoryDetails;

import am.appwise.components.ni.NoInternetDialog;

public class Categories_4 extends Fragment {
    
    Boolean isMenuItem = true;
    Boolean isHeaderVisible = false;
    TextView emptyText, headerText;
    RecyclerView category_recycler;
    LinearLayoutManager HorizontalLayout;
    CategoryListAdapter_4 categoryListAdapter;
    List<CategoryDetails> allCategoriesList;
    List<CategoryDetails> mainCategoriesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categories, container, false);

        setHasOptionsMenu(true);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.actionCategories));
       // ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionCategories));
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (getArguments() != null) {
            if (getArguments().containsKey("isHeaderVisible")) {
                isHeaderVisible = getArguments().getBoolean("isHeaderVisible");
            }
        
            if (getArguments().containsKey("isMenuItem")) {
                isMenuItem = getArguments().getBoolean("isMenuItem", true);
            }
        }
    
    
        if (isMenuItem) {
            // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
            //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionCategories));
        }


        allCategoriesList = new ArrayList<>();

        // Get CategoriesList from ApplicationContext
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();


        // Binding Layout Views
        emptyText = (TextView) rootView.findViewById(R.id.empty_record_text);
        headerText = (TextView) rootView.findViewById(R.id.categories_header);
        category_recycler = (RecyclerView) rootView.findViewById(R.id.categories_recycler);
        NestedScrollView scroll_container = (NestedScrollView) rootView.findViewById(R.id.scroll_container);
        scroll_container.setNestedScrollingEnabled(true);
        category_recycler.setNestedScrollingEnabled(false);

        // Hide some of the Views
        emptyText.setVisibility(View.GONE);

        // Check if Header must be Invisible or not
        if (!isHeaderVisible) {
            // Hide the Header of CategoriesList
            headerText.setVisibility(View.GONE);
        } else {
            headerText.setText("Shop by categories");
        }

        mainCategoriesList= new ArrayList<>();

        for (int i=0;  i<allCategoriesList.size();  i++) {
            if (allCategoriesList.get(i).getParentId().equalsIgnoreCase("0")) {
                mainCategoriesList.add(allCategoriesList.get(i));
            }
        }

        if (mainCategoriesList.isEmpty()){
            emptyText.setVisibility(View.VISIBLE);
        }

        // Initialize the CategoryListAdapter for RecyclerView
        categoryListAdapter = new CategoryListAdapter_4(getContext(), mainCategoriesList, allCategoriesList, false);

        // Set the Adapter and LayoutManager to the RecyclerView
        HorizontalLayout = new GridLayoutManager(getActivity(), 2);
        category_recycler.setLayoutManager(HorizontalLayout);
        category_recycler.setAdapter(categoryListAdapter);
       // category_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryListAdapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar
        MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        cartItem.setVisible(false);
        searchItem.setVisible(true);
    }

}

