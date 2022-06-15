package com.mytodokart.app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;


import com.mytodokart.app.activities.MainActivity;

import com.mytodokart.app.app.App;
import com.mytodokart.app.app.MyAppPrefsManager;
import com.mytodokart.app.databases.User_Cart_DB;
import com.mytodokart.app.fragment.HomePage_1;
import com.mytodokart.app.models.product_model.Option;
import com.mytodokart.app.models.product_model.Value;
import com.mytodokart.app.utils.Utilities;
import com.mytodokart.app.activities.Login;
import com.mytodokart.app.databases.User_Recents_DB;
import com.mytodokart.app.fragment.My_Cart;
import com.mytodokart.app.fragment.Product_Description;
import com.mytodokart.app.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.models.cart_model.CartProduct;
import com.mytodokart.app.models.cart_model.CartProductAttributes;
import com.mytodokart.app.models.product_model.ProductDetails;

/**
 * ProductAdapter is the adapter class of RecyclerView holding List of Products in All_Products and other Product relevant Classes
 **/

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context context;
    private String customerID;
    private Boolean isGridView = true;
    private Boolean isHorizontal;
    private Boolean isFlash;
    private User_Recents_DB recents_db;
    private List<ProductDetails> productList;
    int defaultSmLayoutId;
    int defaultLgLayoutId;

    public ProductAdapter(Context context, List<ProductDetails> productList, Boolean isHorizontal, Boolean isFlash) {
        this.context = context;
        this.productList = productList;
        this.isHorizontal = isHorizontal;
        this.isFlash = isFlash;
        recents_db = new User_Recents_DB();
        customerID = this.context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
        setupDefaultLayoutId();
    }

    public ProductAdapter(Context context, List<ProductDetails> productList, Boolean isHorizontal) {
        this.context = context;
        this.productList = productList;
        this.isHorizontal = isHorizontal;
        recents_db = new User_Recents_DB();
        customerID = this.context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).getString("userID", "");
        setupDefaultLayoutId();
    }

    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = null;

        // Check which Layout will be Inflated
        if (isHorizontal) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(defaultSmLayoutId, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(isGridView ? defaultLgLayoutId : R.layout.layout_product_0_list_lg, parent, false);
        }
        // Return a new holder instance
        return new MyViewHolder(itemView);
    }

    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int[] cartQnt = {0};
        CartProduct cartProduct = new CartProduct();
        if (position != productList.size()) {
            if(!isGridView) {
                holder.cart_edit_wrap.setVisibility(View.GONE);
            }

            // Get the data model based on Position
            final ProductDetails product = productList.get(position);
            // Check if the Product is already in the Cart
            if (My_Cart.checkCartHasProduct(product.getProductsId())) {
                cartProduct = My_Cart.GetCartProduct(product.getProductsId());
                if(!isGridView) {
                    cartQnt[0] = cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity();
                    String cq = String.valueOf(cartQnt[0]);
                    holder.cart_edit_qnt.setText(cq);
                    holder.cart_edit_wrap.setVisibility(View.VISIBLE);
                    holder.product_add_cart_btn.setVisibility(View.GONE);
                } else {
                    holder.product_checked.setVisibility(View.VISIBLE);
                }

            } else {

                if(!isGridView) {
                    holder.product_add_cart_btn.setVisibility(View.VISIBLE);
                } else {
                    holder.product_checked.setVisibility(View.GONE);
                }
            }
            MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
            prefsManager.setUserPincode(product.getProducts_zip_codes());
//            if(product.getProducts_subscription()!=null) {
//                prefsManager.setProductSubscription(product.getProducts_subscription());
//            } else {
//                prefsManager.setProductSubscription("");
//            }
            RequestOptions options = new RequestOptions()
                    .centerInside()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            // Set Product Image on ImageView with Glide Library
            Glide.with(context)
                    .setDefaultRequestOptions(options)
                    .load(ConstantValues.ECOMMERCE_URL + product.getProductsImage())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.shimmerProgress.setVisibility(View.GONE);
                            holder.shimmerProgress.stopShimmer();
                            return false;
                      }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.shimmerProgress.setVisibility(View.GONE);
                            holder.shimmerProgress.stopShimmer();
                            return false;
                        }
                    })
                    .into(holder.product_thumbnail);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.product_thumbnail.setClipToOutline(true);
                holder.product_tag_new.setClipToOutline(true);
            }
            holder.product_title.setText(product.getProductsName());
            holder.product_price_old.setPaintFlags(holder.product_price_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            String[] categoryIDs = new String[product.getCategories().size()];
            String[] categoryNames = new String[product.getCategories().size()];
            if (product.getCategories().size() > 0) {
                for (int i = 0; i < product.getCategories().size(); i++) {
                    categoryIDs[i] = String.valueOf(product.getCategories().get(i).getCategoriesId());
                    categoryNames[i] = product.getCategories().get(i).getCategoriesName();
                }
                product.setCategoryIDs(TextUtils.join(",", categoryIDs));
                product.setCategoryNames(TextUtils.join(",", categoryNames));
            } else {
                product.setCategoryIDs("");
                product.setCategoryNames("");
            }
            holder.product_category.setText(product.getCategoryNames());
            final String discount = Utilities.checkDiscount(product.getProductsPrice(), product.getDiscountPrice());
            if (discount != null) {
                holder.product_price_old.setVisibility(View.VISIBLE);
                holder.product_price_old.setText( context.getString(R.string.Rs) + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getProductsPrice())));
                holder.product_price_new.setText( context.getString(R.string.Rs) + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getDiscountPrice())));
                holder.product_tag_new.setVisibility(View.GONE);
                // Set Discount Tag and its Text
                holder.layoutSale.setVisibility(View.VISIBLE);
                holder.product_tag_discount_text.setText(discount);
            } else {
                // Check if the Product is Newly Added with the help of static method of Helper class
                if (Utilities.checkNewProduct(product.getProductsDateAdded())) {
                    // Set New Tag and its Text
                    holder.product_tag_new.setVisibility(View.VISIBLE);
                } else {
                    holder.product_tag_new.setVisibility(View.GONE);
                }
                // Hide Discount Text and Set Product's Price
                holder.layoutSale.setVisibility(View.GONE);
                holder.product_price_old.setVisibility(View.GONE);
/*
                NumberFormat nf = NumberFormat.getInstance(LocaleHelper.getSystemLocale( context.getResources().getConfiguration())); //or "nb","No" - for Norway
                String thisprice = nf.format(Double.parseDouble());
*/
                holder.product_price_new.setText(context.getString(R.string.Rs) + "" + new DecimalFormat("#0.00").format(Double.parseDouble(product.getProductsPrice())));
            }
            holder.product_like_btn.setOnCheckedChangeListener(null);
            // Check if Product is Liked
            if (product.getIsLiked().equalsIgnoreCase("1")) {
                holder.product_like_btn.setChecked(true);
            } else {
                holder.product_like_btn.setChecked(false);
            }

            if(!isGridView){

                holder.cart_edit_minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         CartProduct finalCartProduct = My_Cart.GetCartProduct(product.getProductsId());
                        if(cartQnt[0]>1) {
                            cartQnt[0] = cartQnt[0] - 1;
                            holder.cart_edit_qnt.setText("" + cartQnt[0]);
                            // Calculate Product Price with selected Quantity
                            double price = Double.parseDouble(finalCartProduct.getCustomersBasketProduct().getProductsFinalPrice()) * cartQnt[0];
                            // Set Final Price and Quantity
                            finalCartProduct.getCustomersBasketProduct().setTotalPrice("" + price);
                            finalCartProduct.getCustomersBasketProduct().setCustomersBasketQuantity(cartQnt[0]);

                            // Update CartItem in Local Database using static method of My_Cart
                            My_Cart.UpdateCartItem(finalCartProduct);
                        }
                    }
                });

                holder.cart_edit_plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CartProduct finalCartProduct = My_Cart.GetCartProduct(product.getProductsId());
                        cartQnt[0] = cartQnt[0] + 1;
                        holder.cart_edit_qnt.setText(""+ cartQnt[0]);
                        // Calculate Product Price with selected Quantity
                        double price = Double.parseDouble(finalCartProduct.getCustomersBasketProduct().getProductsFinalPrice()) * cartQnt[0];
                        // Set Final Price and Quantity
                        finalCartProduct.getCustomersBasketProduct().setTotalPrice(""+ price);
                        finalCartProduct.getCustomersBasketProduct().setCustomersBasketQuantity(cartQnt[0]);

                        // Update CartItem in Local Database using static method of My_Cart
                        My_Cart.UpdateCartItem(finalCartProduct);
                    }
                });
            }

            // Handle the Click event of product_like_btn ToggleButton
            holder.product_like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the User is Authenticated
                    if (ConstantValues.IS_USER_LOGGED_IN) {
                        if (holder.product_like_btn.isChecked()) {
                            product.setIsLiked("1");
                            holder.product_like_btn.setChecked(true);

                            // Like the Product for the User with the static method of Product_Description
                            Product_Description.LikeProduct(product.getProductsId(), customerID, context, view);
                        } else {
                            product.setIsLiked("0");
                            holder.product_like_btn.setChecked(false);

                            // Unlike the Product for the User with the static method of Product_Description
                            Product_Description.UnlikeProduct(product.getProductsId(), customerID, context, view);
                        }

                    } else {
                        // Keep the Like Button Unchecked
                        holder.product_like_btn.setChecked(false);

                        // Navigate to Login Activity
                        Intent i = new Intent(context, Login.class);
                        context.startActivity(i);
                        ((MainActivity) context).finish();
                        ((MainActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                    }
                }
            });
            

            // Handle the Click event of product_thumbnail ImageView
            holder.product_thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get Product Info
                    Bundle itemInfo = new Bundle();
                    itemInfo.putParcelable("productDetails", product);

                    // Save the AddressDetails
                    ((App) context.getApplicationContext()).setProductDetails(product);
                    // Navigate to Product_Description of selected Product
                    Fragment fragment = new Product_Description(holder.product_checked);
                    fragment.setArguments(itemInfo);
                    //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .hide(((MainActivity) context).currentFragment)
                            .add(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();


                    // Add the Product to User's Recently Viewed Products
                    if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                        recents_db.insertRecentItem(product.getProductsId());
                    }
                }
            });




            // Handle the Click event of product_checked ImageView
            holder.product_checked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get Product Info
//                    Bundle itemInfo = new Bundle();
//                    itemInfo.putParcelable("productDetails", product);
//
//                    // Navigate to Product_Description of selected Product
//                    Fragment fragment = new Product_Description(holder.product_checked);
//                    fragment.setArguments(itemInfo);
//                    //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .hide(((MainActivity) context).currentFragment)
//                            .add(R.id.main_fragment, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(null).commit();
//
//
//                    // Add the Product to User's Recently Viewed Products
//                    if (!recents_db.getUserRecents().contains(product.getProductsId())) {
//                        recents_db.insertRecentItem(product.getProductsId());
//                    }



                }
            });


            // Check the Button's Visibility
            if (ConstantValues.IS_ADD_TO_CART_BUTTON_ENABLED) {

               // holder.product_add_cart_btn.setVisibility(View.VISIBLE);
                holder.product_add_cart_btn.setOnClickListener(null);

                if (product.getProductsType() != 0) {
                    holder.product_add_cart_btn.setText(context.getString(R.string.view_product));
                    holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
                } else {
                    if (product.getProductsDefaultStock() < 1) {
                        holder.product_add_cart_btn.setText(context.getString(R.string.outOfStock));
                        holder.product_add_cart_btn.setTextColor(context.getResources().getColor(R.color.meatnco));
                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.red_border_white));
                    } else {
                        if(product.getProducts_subscription().equalsIgnoreCase("1")){
                            holder.product_add_cart_btn.setText("Subscribe");
                        } else {
                            holder.product_add_cart_btn.setText("ADD TO CART");
                        }
                        holder.product_add_cart_btn.setTextColor(Color.WHITE);
                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));


                    }
                }


                if (isFlash) {
                    holder.product_like_btn.setVisibility(View.GONE);

                    holder.start = Long.parseLong(product.getFlashStartDate()) * 1000L;
                    holder.end = Long.parseLong(product.getFlashExpireDate()) * 1000L;

                    holder.server = Long.parseLong(product.getServerTime()) * 1000L;

                    //  remainingTime =  end-server ;

                    if (holder.server > holder.start) {
                        holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getFlashPrice())));
                        holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getProductsPrice())));

                        if (holder.mCountDownTimer != null){
                            holder.mCountDownTimer.cancel();
                        }

                        holder.mCountDownTimer = new CountDownTimer(holder.end, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                                holder.server = holder.server - 1;
                                Long serverUptimeSeconds =
                                        (millisUntilFinished - holder.server) / 1000;

                                String daysLeft = String.format("%d", serverUptimeSeconds / 86400);
                                String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                                String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);
                                String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                                holder.product_add_cart_btn.setText(daysLeft + "D:" + hoursLeft + "H:" + minutesLeft + "M:" + secondsLeft + "S");

                            }

                            @Override
                            public void onFinish() {
                                holder.product_add_cart_btn.setText(context.getResources().getString(R.string.upcoming));
                                holder.product_add_cart_btn.setBackgroundResource(R.drawable.rounded_corners_button_red);
                            }
                        }.start();
                    } else {
                        holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getFlashPrice())));
                        holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getProductsPrice())));
                        holder.product_add_cart_btn.setText(context.getResources().getString(R.string.upcoming));
                        holder.product_add_cart_btn.setBackgroundResource(R.drawable.rounded_corners_button_red);
                    }

                    holder.product_price_old.setVisibility(View.VISIBLE);
                }

                holder.product_add_cart_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (product.getProductsType() != 0) {

                            // Get Product Info
                            Bundle itemInfo = new Bundle();
                            itemInfo.putParcelable("productDetails", product);

                            // Navigate to Product_Description of selected Product
                            Fragment fragment = new Product_Description(holder.product_checked);
                            fragment.setArguments(itemInfo);
                            //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .hide(((MainActivity) context).currentFragment)
                                    .add(R.id.main_fragment, fragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null).commit();

                            // Add the Product to User's Recently Viewed Products
                            if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                                recents_db.insertRecentItem(product.getProductsId());
                            }
                        } else {

                            if (isFlash) {
                                if (holder.start > holder.server) {
                                    Snackbar.make(view, context.getString(R.string.cannot_add_upcoming), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Utilities.animateCartMenuIcon(context, (MainActivity) context);
                                    // Add Product to User's Cart
                                    if(addProductToCart(product)) {
                                        if (isGridView) {
                                            holder.product_checked.setVisibility(View.VISIBLE);
                                        } else {
                                            holder.cart_edit_wrap.setVisibility(View.VISIBLE);
                                            holder.product_add_cart_btn.setVisibility(View.GONE);
                                        }

                                        Snackbar.make(view, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            } else {

                                if (product.getProductsDefaultStock() < 1) {

                                    Snackbar.make(view, context.getString(R.string.outOfStock), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Utilities.animateCartMenuIcon(context, (MainActivity) context);
                                    // Add Product to User's Cart
                                    if(addProductToCart(product)) {
                                        if (isGridView) {
                                            holder.product_checked.setVisibility(View.VISIBLE);
                                        } else {
                                            holder.cart_edit_wrap.setVisibility(View.VISIBLE);
                                            holder.product_add_cart_btn.setVisibility(View.GONE);
                                        }

                                        Snackbar.make(view, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }
                    }
                });

                holder.product_card_img_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (product.getProductsType() != 0) {

                            // Get Product Info
                            Bundle itemInfo = new Bundle();
                            itemInfo.putParcelable("productDetails", product);

                            // Navigate to Product_Description of selected Product
                            Fragment fragment = new Product_Description(holder.product_checked);
                            fragment.setArguments(itemInfo);
                            //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .hide(((MainActivity) context).currentFragment)
                                    .add(R.id.main_fragment, fragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(null).commit();

                            // Add the Product to User's Recently Viewed Products
                            if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                                recents_db.insertRecentItem(product.getProductsId());
                            }
                        }
                        else {

                            if (isFlash) {

                                if (holder.start > holder.server) {
                                    Snackbar.make(v, context.getString(R.string.cannot_add_upcoming), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Utilities.animateCartMenuIcon(context, (MainActivity) context);
                                    // Add Product to User's Cart
                                    if(addProductToCart(product)) {
                                        holder.product_checked.setVisibility(View.VISIBLE);
                                        Snackbar.make(v, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                if (product.getProductsDefaultStock() < 1) {
                                    Snackbar.make(v, context.getString(R.string.outOfStock), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Utilities.animateCartMenuIcon(context, (MainActivity) context);
                                    // Add Product to User's Cart
                                    if(addProductToCart(product)) {
                                        holder.product_checked.setVisibility(View.VISIBLE);
                                        Snackbar.make(v, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }
                        HomePage_1.cartCount();
                    }
                });

               holder.product_checked.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       List<CartProduct> cartProduct=new User_Cart_DB().getCartItems();
                       removeCartItem(cartProduct,product.getProductsName(),holder.product_checked);
                       holder.product_checked.setVisibility(View.GONE);
                       HomePage_1.cartCount();
                       Utilities.animateCartMenuIcon(context, (MainActivity) context);
                       ((MainActivity) context).invalidateOptionsMenu();

                   }
               });

            } else {
                // Make the Button Invisible
                holder.product_add_cart_btn.setVisibility(View.GONE);
            }

        }

        if ( productList.get(position).getProductsWeight() !=null ) {
            holder.itemWeight.setText(productList.get(position).getProductsWeight()+" "
                                      +productList.get(position).getProductsWeightUnit());
        }


    }

    void removeCartItem(List<CartProduct> cartProduct, String productsName, final View view)
    {
        for (int i=0;i<cartProduct.size();i++) {

            ProductDetails productDetails=cartProduct.get(i).getCustomersBasketProduct();

            if (productsName.equals(productDetails.getProductsName())) {
                 new User_Cart_DB().deleteCartItem(cartProduct.get(i).getCustomersBasketId());
                 break;
            }
        }

    }


    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return productList.size();
    }


    //********** Toggles the RecyclerView LayoutManager *********//

    public void toggleLayout(Boolean isGridView) {
        this.isGridView = isGridView;
    }


    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        long start;
        long end;
        long server;
        CountDownTimer mCountDownTimer;


        ImageView product_checked;
        Button product_add_cart_btn;
        ImageButton product_card_img_btn;
        ToggleButton product_like_btn;
        ImageView product_thumbnail, product_tag_new;
        TextView product_title, product_category, product_price_old,
                product_price_new, product_tag_discount_text,itemWeight,cart_edit_minus,cart_edit_qnt,cart_edit_plus;
        LinearLayout layoutSale,cart_edit_wrap;
        ShimmerFrameLayout shimmerProgress;

        public MyViewHolder(final View itemView) {
            super(itemView);

            product_checked = (ImageView) itemView.findViewById(R.id.product_checked);

            product_add_cart_btn = (Button) itemView.findViewById(R.id.product_card_Btn);
            product_card_img_btn = itemView.findViewById(R.id.product_card_img_btn);
            product_like_btn = (ToggleButton) itemView.findViewById(R.id.product_like_btn);
            product_title = (TextView) itemView.findViewById(R.id.product_title);
            product_category = (TextView) itemView.findViewById(R.id.product_category);
            product_price_old = (TextView) itemView.findViewById(R.id.product_price_old);
            product_price_new = (TextView) itemView.findViewById(R.id.product_price_new);
            itemWeight = (TextView) itemView.findViewById(R.id.itemWeight);
            product_thumbnail = (ImageView) itemView.findViewById(R.id.product_cover);
            product_tag_new = (ImageView) itemView.findViewById(R.id.product_item_tag_new);
            product_tag_discount_text = (TextView) itemView.findViewById(R.id.productItemTagOff);
            layoutSale = itemView.findViewById(R.id.saleLayout);
            shimmerProgress = itemView.findViewById(R.id.shimmerFrame);
            cart_edit_wrap = itemView.findViewById(R.id.cart_edit_wrap);
            cart_edit_plus = itemView.findViewById(R.id.cart_edit_plus);
            cart_edit_minus = itemView.findViewById(R.id.cart_edit_minus);
            cart_edit_qnt = itemView.findViewById(R.id.cart_edit_qnt);

        }

    }

    //********** Adds the Product to User's Cart *********//

    private boolean addProductToCart(ProductDetails product) {

        CartProduct cartProduct = new CartProduct();

        double productBasePrice, productFinalPrice = 0.0, attributesPrice = 0;
        List<CartProductAttributes> selectedAttributesList = new ArrayList<>();

        String subscriptionPrice = "0";
        if (product.getProducts_subscription()!=null && product.getProducts_subscription()!=""){
            subscriptionPrice = product.getProducts_subscription();
           // subscriptionPrice = "1";
        }

        // Check Discount on Product with the help of static method of Helper class
        final String discount = Utilities.checkDiscount(product.getProductsPrice(), product.getDiscountPrice());

        // Get Product's Price based on Discount
        if (discount != null) {
            product.setIsSaleProduct("1");
            productBasePrice = Double.parseDouble(product.getDiscountPrice());
        } else {
            product.setIsSaleProduct("0");
            productBasePrice = Double.parseDouble(product.getProductsPrice());
        }


        // Get Default Attributes from AttributesList
        for (int i = 0; i < product.getAttributes().size(); i++) {

            CartProductAttributes productAttribute = new CartProductAttributes();

            // Get Name and First Value of current Attribute
            Option option = product.getAttributes().get(i).getOption();
            Value value = product.getAttributes().get(i).getValues().get(0);


            // Add the Attribute's Value Price to the attributePrices
            String attrPrice = value.getPricePrefix() + value.getPrice();
            attributesPrice += Double.parseDouble(attrPrice);


            // Add Value to new List
            List<Value> valuesList = new ArrayList<>();
            valuesList.add(value);


            // Set the Name and Value of Attribute
            productAttribute.setOption(option);
            productAttribute.setValues(valuesList);


            // Add current Attribute to selectedAttributesList
            selectedAttributesList.add(i, productAttribute);
        }

        if (isFlash) {
            productFinalPrice = Double.parseDouble(product.getFlashPrice()) + attributesPrice;
        } else {
            // Add Attributes Price to Product's Final Price
            productFinalPrice = productBasePrice + attributesPrice;
        }
        //productFinalPrice = productFinalPrice*qnt;
        // Set Product's Price and Quantity
        product.setProductsBasePrice(String.valueOf(product.getProductsPrice()));

        product.setCustomersBasketQuantity(1);
        product.setProductsPrice(String.valueOf(productBasePrice));
        product.setAttributesPrice(String.valueOf(attributesPrice));
        product.setProductsFinalPrice(String.valueOf(productFinalPrice));
        int quantity = product.getProductsDefaultStock();
        product.setProductsQuantity(product.getProductsDefaultStock());
        // Set Product's OrderProductCategory Info
        String[] categoryIDs = new String[product.getCategories().size()];
        String[] categoryNames = new String[product.getCategories().size()];
        if (product.getCategories().size() > 0) {

            for (int i = 0; i < product.getCategories().size(); i++) {
                categoryIDs[i] = String.valueOf(product.getCategories().get(i).getCategoriesId());
                categoryNames[i] = product.getCategories().get(i).getCategoriesName();
            }

            product.setCategoryIDs(TextUtils.join(",", categoryIDs));
            product.setCategoryNames(TextUtils.join(",", categoryNames));
        } else {
            product.setCategoryIDs("");
            product.setCategoryNames("");
        }
        // product.setCategoryNames(product.getCategoryNames());

        product.setTotalPrice(String.valueOf(productFinalPrice));

        product.setProducts_subscription(subscriptionPrice);
        // Set Customer's Basket Product and selected Attributes Info
        cartProduct.setCustomersBasketProduct(product);
        cartProduct.setCustomersBasketProductAttributes(selectedAttributesList);
        int cc = My_Cart.getCartSize();
        // Add the Product to User's Cart with the help of static method of My_Cart class
        if(product.getProducts_subscription().equalsIgnoreCase("1")){

            if(My_Cart.checkSubscribed("0")){
                msgDailog("You have regular items in your cart, kindly finish the regular items order and then begin placing order for subscription items.");
                return false;
            } else {
                product.setProducts_subscription("1");
                product.setCustomersBasketQuantity(1);
                product.setTotalPrice(String.valueOf(productFinalPrice * 30));
                cartProduct.setCustomersBasketProduct(product);
                cartProduct.setCustomersBasketProductAttributes(selectedAttributesList);

               // My_Cart.ClearCart();
                MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
                prefsManager.setPlan("DAILY");
                prefsManager.setProductSubscription(cartProduct.getCustomersBasketProduct().getProducts_subscription());
                My_Cart.AddCartItem(cartProduct);

                Fragment fragment = new My_Cart();
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .hide(((MainActivity) context).currentFragment)
                        .add(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }

        } else {
            MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
            String checkSub = prefsManager.getProductSubscription();
            if(My_Cart.checkSubscribed("1")){
                msgDailog("Please Complete subscription orders in cart, and then place order for regular items.");
                return false;
            } else {
                prefsManager.setProductSubscription("");
                My_Cart.AddCartItem(cartProduct);
            }
        }

        // Recreate the OptionsMenu
        ((MainActivity) context).invalidateOptionsMenu();
        return true;
    }

    private void msgDailog(String msg){
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setupDefaultLayoutId() {
        System.out.println("Product Card "+ConstantValues.DEFAULT_PRODUCT_CARD_STYLE);
        switch (ConstantValues.DEFAULT_PRODUCT_CARD_STYLE) {
            case 0:
                defaultSmLayoutId = R.layout.layout_product_0_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_0_grid_lg;
                break;
            case 1:
                defaultSmLayoutId = R.layout.layout_product_1_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_1_grid_lg;
                break;
            case 2:
                defaultSmLayoutId = R.layout.layout_product_2_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_2_grid_lg;
                break;
            case 3:
                defaultSmLayoutId = R.layout.layout_product_3_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_3_grid_lg;
                break;
            case 4:
                defaultSmLayoutId = R.layout.layout_product_4_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_4_grid_lg;
                break;
            case 5:
                defaultSmLayoutId = R.layout.layout_product_5_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_5_grid_lg;
                break;
            case 6:
                defaultSmLayoutId = R.layout.layout_product_6_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_6_grid_lg;
                break;
            case 7:
                defaultSmLayoutId = R.layout.layout_product_0_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_0_grid_lg;
                break;
            case 8:
                defaultSmLayoutId = R.layout.layout_product_8_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_8_grid_lg;
                break;
            case 9:
                defaultSmLayoutId = R.layout.layout_product_9_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_9_grid_lg;
                break;
            case 10:
                defaultSmLayoutId = R.layout.layout_product_10_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_10_grid_lg;
                break;
            case 11:
                defaultSmLayoutId = R.layout.layout_product_11_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_11_grid_lg;
                break;
            case 12:
                defaultSmLayoutId = R.layout.layout_product_12_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_12_grid_lg;
                break;
            case 13:
                defaultSmLayoutId = R.layout.layout_product_13_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_13_grid_lg;
                break;
            case 14:
                defaultSmLayoutId = R.layout.layout_product_14_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_14_grid_lg;
                break;
            case 15:
                defaultSmLayoutId = R.layout.layout_product_15_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_15_grid_lg;
                break;
            case 16:
                defaultSmLayoutId = R.layout.layout_product_16_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_16_grid_lg;
                break;
            case 17:
                defaultSmLayoutId = R.layout.layout_product_17_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_17_grid_lg;
                break;
            case 18:
                defaultSmLayoutId = R.layout.layout_product_18_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_18_grid_lg;
                break;
            case 19:
                defaultSmLayoutId = R.layout.layout_product_19_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_19_grid_lg;
                break;
            case 20:
                defaultSmLayoutId = R.layout.layout_product_20_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_20_grid_lg;
                break;
            case 21:
                defaultSmLayoutId = R.layout.layout_product_21_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_21_grid_lg;
                break;
            case 22:
                defaultSmLayoutId = R.layout.layout_product_22_grid_sm;
                defaultLgLayoutId = R.layout.layout_product_22_grid_lg;
                break;
        }
    }

    public String nFormate(double d) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(10);
        String st = nf.format(d);
        return st;
    }
}
