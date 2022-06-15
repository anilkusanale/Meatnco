package com.mytodokart.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.models.category_model.CategoryDetails;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;

public class NewCategoriesAdapter extends RecyclerView.Adapter<NewCategoriesAdapter.MyViewHolder> {
    
    LayoutInflater inflater;
    Activity activity;
    List<CategoryDetails> allCategoriesList;
    boolean isSubCategory;

    public NewCategoriesAdapter(Activity activity, List<CategoryDetails> allCategoriesList, boolean isSubCategory) {
        this.activity = activity;
        this.allCategoriesList = allCategoriesList;
        this.isSubCategory = isSubCategory;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public NewCategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View binding = inflater.inflate(R.layout.adapter_new_category, parent, false);
        return new NewCategoriesAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewCategoriesAdapter.MyViewHolder holder, final int position) {
            holder.categoryNameTV.setText(allCategoriesList.get(position).getName());
            Picasso.with(activity).load(ConstantValues.ECOMMERCE_URL + allCategoriesList.get(position).getImage()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.categoryIV);

    }

    @Override
    public int getItemCount() {
        return allCategoriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView categoryIV;
        private TextView categoryNameTV;
        private LinearLayout categoryLL;

        public MyViewHolder(View item) {
            super(item);
            categoryIV = item.findViewById(R.id.categoryIV);
            categoryNameTV = item.findViewById(R.id.categoryNameTV);
            categoryLL = item.findViewById(R.id.categoryLL);
            categoryLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get OrderProductCategory Info
                    Bundle categoryInfo = new Bundle();
                    categoryInfo.putInt("CategoryID", Integer.parseInt(allCategoriesList.get(getAdapterPosition()).getId()));
                    categoryInfo.putString("CategoryName", allCategoriesList.get(getAdapterPosition()).getName());
                    Fragment fragment;

                    if (isSubCategory) {
                        // Navigate to Products Fragment
                        fragment = new Products();
                    } else {
                        boolean haveCategorisies = false;
                        if(allCategoriesList.get(getAdapterPosition()).getHasChild().matches("1")){
                            fragment = new SubCategories_4();
                        } else {
                            fragment = new Products();
                        }
                    }

                    fragment.setArguments(categoryInfo);
                    FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();
                }
            });
        }
    }


}