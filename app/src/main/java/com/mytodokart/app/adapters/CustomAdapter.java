package com.mytodokart.app.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.fragment.Products;
import com.mytodokart.app.models.banner_model.BannerDetails;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CustomAdapter extends PagerAdapter {

    private Activity activity;
    List<BannerDetails> bannerList;

    public CustomAdapter(Activity activity, List<BannerDetails> bannerList) {
        this.activity = activity;
        this.bannerList = bannerList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
        View viewItem = inflater.inflate(R.layout.image_item, container, false);
        ImageView imageView = (ImageView) viewItem.findViewById(R.id.imageView);
        Picasso.with(activity).load(ConstantValues.ECOMMERCE_URL+bannerList.get(position).getImage()).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        ((ViewPager)container).addView(viewItem);
        if(bannerList.get(position).getUrl()!=null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle categoryInfo = new Bundle();
                    categoryInfo.putInt("CategoryID", Integer.parseInt(bannerList.get(position).getUrl()));
                    categoryInfo.putString("CategoryName", bannerList.get(position).getTitle());
                    Fragment fragment;
                    fragment = new Products();
                    fragment.setArguments(categoryInfo);
                    FragmentManager fragmentManager = ((MainActivity) activity).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();
                }
            });
        }
        return viewItem;
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}