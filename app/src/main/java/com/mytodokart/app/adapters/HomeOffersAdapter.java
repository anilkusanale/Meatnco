package com.mytodokart.app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.fragment.Products;
import com.mytodokart.app.models.OfferDetails;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class HomeOffersAdapter extends RecyclerView.Adapter<HomeOffersAdapter.ViewHolder> {

    Context context;
    List<OfferDetails> imagesList;

    public HomeOffersAdapter(Context context, List<OfferDetails> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ViewHolder holder;
        holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_offer_home,parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(context).load(ConstantValues.ECOMMERCE_URL+imagesList.get(position).getPath()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
      //  Glide.with(context).load(imagesList.get(position)).into(holder.imageView);
        holder.offerTitle.setText(imagesList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView offerTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ivOffer);
            offerTitle=itemView.findViewById(R.id.offerTitle);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle categoryInfo = new Bundle();
                    categoryInfo.putInt("CategoryID", Integer.parseInt(imagesList.get(getAdapterPosition()).getUrl()));
                    categoryInfo.putString("CategoryName", imagesList.get(getAdapterPosition()).getCat());
                    Fragment fragment;
                    fragment = new Products();
                    fragment.setArguments(categoryInfo);
                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();
                }
            });
        }
    }
}
