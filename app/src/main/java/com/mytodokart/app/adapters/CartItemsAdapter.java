package com.mytodokart.app.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.app.App;
import com.mytodokart.app.app.MyAppPrefsManager;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.databases.User_Cart_DB;
import com.mytodokart.app.databases.User_Recents_DB;
import com.mytodokart.app.fragment.My_Cart;
import com.mytodokart.app.fragment.Product_Description;
import com.mytodokart.app.fragment.Products;
import com.mytodokart.app.models.cart_model.CartProduct;
import com.mytodokart.app.models.cart_model.CartProductAttributes;
import com.mytodokart.app.models.device_model.AppSettingsDetails;
import com.mytodokart.app.models.product_model.Value;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CartItemsAdapter is the adapter class of RecyclerView holding List of Cart Items in My_Cart
 **/

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.MyViewHolder> {

    private final Context context;
    private final My_Cart cartFragment;
    private List<CartProduct> cartItemsList = new ArrayList<>();
    private final User_Cart_DB user_cart_db;
    private final User_Recents_DB recents_db;
    private String[] mStringArray;
    private String selected_plan = "Choose Subscription Plan";
    private ArrayList<String> plans = new ArrayList<>();
    private SpinnerAdapter spinnerAdapter;
    private ProductAttributeValuesAdapter attributesAdapter;
    private int days = 30;

    AtomicReference<Calendar> start = new AtomicReference<>(null);
    AtomicReference<Calendar> end = new AtomicReference<>(null);


    public CartItemsAdapter(Context context, List<CartProduct> cartItemsList, My_Cart cartFragment, ArrayList<String> plans) {
        this.context = context;
        this.cartItemsList = cartItemsList;
        this.cartFragment = cartFragment;
        this.plans = plans;
        user_cart_db = new User_Cart_DB();
        recents_db = new User_Recents_DB();
    }

    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == R.layout.layout_button) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_button, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_cart_items, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // spinnerAdapter = new SpinnerAdapter(context, plans);
        if (position == cartItemsList.size()) {
            holder.custom_button.setText(context.getString(R.string.explore));
            holder.custom_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isSubFragment", false);
                    // Navigate to Products Fragment
                    Fragment fragment = new Products();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(context.getString(R.string.actionCart)).commit();
                }
            });
        } else {
            // Get the data model based on Position
            final CartProduct cartProduct = cartItemsList.get(position);
            // Set Product Image on ImageView with Glide Library
            Glide.with(context).load(ConstantValues.ECOMMERCE_URL + cartProduct.getCustomersBasketProduct().getProductsImage()).into(holder.cart_item_cover);
            holder.cart_item_title.setText(cartProduct.getCustomersBasketProduct().getProductsName());
            holder.cart_item_category.setText(cartProduct.getCustomersBasketProduct().getCategoryNames());
            holder.cart_item_size.setText(cartProduct.getCustomersBasketProduct().getProductsWeight() + " " + cartProduct.getCustomersBasketProduct().getProductsWeightUnit());
            holder.cart_item_quantity.setText("" + cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity());
            holder.cart_item_base_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(cartProduct.getCustomersBasketProduct().getProductsPrice())));
            holder.cart_item_sub_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(cartProduct.getCustomersBasketProduct().getTotalPrice())));

            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);


            List<Value> selectedAttributeValues = new ArrayList<>();
            List<CartProductAttributes> productAttributes = new ArrayList<>();
            productAttributes = cartProduct.getCustomersBasketProductAttributes();
            for (int i = 0; i < productAttributes.size(); i++) {
                selectedAttributeValues.add(productAttributes.get(i).getValues().get(0));
            }
            // Initialize the ProductAttributeValuesAdapter for RecyclerView
            attributesAdapter = new ProductAttributeValuesAdapter(context, selectedAttributeValues);
            holder.attributes_recycler.setAdapter(attributesAdapter);
            holder.attributes_recycler.setLayoutManager(new LinearLayoutManager(context));
            attributesAdapter.notifyDataSetChanged();

            // Holds Product Quantity
            final int[] number = {1};
            number[0] = cartProduct.getCustomersBasketProduct().getCustomersBasketQuantity();

            // Decrease Product Quantity
            holder.cart_item_quantity_minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the Quantity is greater than the minimum Quantity
                    if (number[0] > 1) {
                        // Decrease Quantity by 1
                        number[0] = number[0] - 1;
                        holder.cart_item_quantity.setText("" + number[0]);
                        // Calculate Product Price with selected Quantity
                        double price = Double.parseDouble(cartProduct.getCustomersBasketProduct().getProductsFinalPrice()) * number[0];
                        //price = price * days;
                        // Set Final Price and Quantity
                        cartProduct.getCustomersBasketProduct().setTotalPrice("" + price);
                        cartProduct.getCustomersBasketProduct().setCustomersBasketQuantity(number[0]);

                        // Update CartItem in Local Database using static method of My_Cart
                        My_Cart.UpdateCartItem(cartProduct);
                        // Calculate Cart's Total Price Again
                        setCartTotal();
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });
            // Increase Product Quantity
            holder.cart_item_quantity_plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if the Quantity is less than the maximum or stock Quantity
                    if (number[0] < cartProduct.getCustomersBasketProduct().getProductsQuantity()) {
                        // Increase Quantity by 1
                        number[0] = number[0] + 1;
                        holder.cart_item_quantity.setText("" + number[0]);

                        // Calculate Product Price with selected Quantity
                        double price = Double.parseDouble(cartProduct.getCustomersBasketProduct().getProductsFinalPrice()) * number[0];
                        //price = price * days;

                        // Set Final Price and Quantity
                        cartProduct.getCustomersBasketProduct().setTotalPrice("" + price);
                        cartProduct.getCustomersBasketProduct().setCustomersBasketQuantity(number[0]);
                        // Update CartItem in Local Database using static method of My_Cart
                        My_Cart.UpdateCartItem(cartProduct);
                        // Calculate Cart's Total Price Again
                        setCartTotal();
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });


            // View Product Details
            holder.cart_item_viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get Product Info
                    Bundle itemInfo = new Bundle();
                    itemInfo.putInt("itemID", cartProduct.getCustomersBasketProduct().getProductsId());
                    // Navigate to Product_Description of selected Product
                    Fragment fragment = new Product_Description();
                    fragment.setArguments(itemInfo);
                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null)
                            .commit();

                    // Add the Product to User's Recently Viewed Products
                    if (!recents_db.getUserRecents().contains(cartProduct.getCustomersBasketProduct().getProductsId())) {
                        recents_db.insertRecentItem(cartProduct.getCustomersBasketProduct().getProductsId());
                    }
                }
            });

            // Delete relevant Product from Cart
            holder.cart_item_removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.cart_item_removeBtn.setClickable(false);
                    // Delete CartItem from Local Database using static method of My_Cart
                    My_Cart.DeleteCartItem(cartProduct.getCustomersBasketId());
                    // Calculate Cart's Total Price Again
                    if (cartProduct.getCustomersBasketProduct().getProducts_subscription().equals("1")) {
                        MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
                        prefsManager.setProductSubscription("");
                    }
                    setCartTotal();
                    // Remove CartItem from Cart List
                    cartItemsList.remove(holder.getAdapterPosition());
                    // Notify that item at position has been removed
                    notifyItemRemoved(holder.getAdapterPosition());
                    // Update Cart View from Local Database using static method of My_Cart
                    cartFragment.updateCartView(getItemCount());
                }
            });

//            if (cartProduct.getCustomersBasketProduct().getProducts_subscription().equals("1")) {
//                MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
//                selected_plan = prefsManager.getPlan();
//                //holder.subscriptionPlanRL.setVisibility(View.VISIBLE);
//               // holder.subscription_text.setText(selected_plan);
//              //  holder.llStartEndDate.setVisibility(View.VISIBLE);
//            } else {
//                holder.subscriptionPlanRL.setVisibility(View.GONE);
//                holder.llStartEndDate.setVisibility(View.GONE);
//            }

//            holder.startDate.setOnClickListener(v -> setDate(true, (date, calendar) -> {
//                start.set(calendar);
//                holder.startDate.setText(date);
//
//                calculateNoOfDays(start.get(), end.get(), holder.noOfDays, cartProduct, holder);
//
//
//                cartProduct.getCustomersBasketProduct().setSubscriptionStart(String.valueOf(start.get().getTimeInMillis()));
//                if (end.get() != null)
//                    cartProduct.getCustomersBasketProduct().setSubscriptionEnd(String.valueOf(end.get().getTimeInMillis()));
//
//                My_Cart.UpdateCartSubscriptionStartEndDateItem(cartProduct);
//                // Calculate Cart's Total Price Again
//                setCartTotal();
//                notifyItemChanged(holder.getAdapterPosition());
//            }));
//            holder.endDate.setOnClickListener(v -> setDate(false, (date, calendar) -> {
//                end.set(calendar);
//                holder.endDate.setText(date);
//                calculateNoOfDays(start.get(), end.get(), holder.noOfDays, cartProduct, holder);
//
//                if (start.get() != null)
//                    cartProduct.getCustomersBasketProduct().setSubscriptionStart(String.valueOf(start.get().getTimeInMillis()));
//                cartProduct.getCustomersBasketProduct().setSubscriptionEnd(String.valueOf(end.get().getTimeInMillis()));
//                My_Cart.UpdateCartSubscriptionStartEndDateItem(cartProduct);
//                // Calculate Cart's Total Price Again
//                setCartTotal();
//                notifyItemChanged(holder.getAdapterPosition());
//            }));

            if (cartProduct.getCustomersBasketProduct().getSubscriptionStart() != null &&
                    cartProduct.getCustomersBasketProduct().getSubscriptionEnd() != null) {
                //calculateNoOfDays(startTime, endTime, holder.noOfDays, cartProduct, holder);
            }
        }
    }

    private void calculateNoOfDays(Calendar start, Calendar end, TextView view, CartProduct cartProduct1, final MyViewHolder holder1) {
        if (start == null) return;
        if (end == null) return;
        long diff = start.getTimeInMillis() - end.getTimeInMillis();
        days = (int) Math.abs(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        days++;
        view.setText(String.format(Locale.getDefault(), "%d Days", days));

        double price = Double.parseDouble(cartProduct1.getCustomersBasketProduct().getProductsFinalPrice()) * 1;
        price = price * days;

        // Set Final Price and Quantity
        cartProduct1.getCustomersBasketProduct().setTotalPrice("" + price);
        cartProduct1.getCustomersBasketProduct().setCustomersBasketQuantity(1);
        cartProduct1.getCustomersBasketProduct().setManufacturersName(selected_plan);
        // Update CartItem in Local Database using static method of My_Cart
        My_Cart.UpdateCartItem(cartProduct1);
        // Calculate Cart's Total Price Again
        setCartTotal();
        notifyItemChanged(holder1.getAdapterPosition());
    }

    private void showAlertDialog(final TextView subscription_text, final CartProduct cartProduct1, final MyViewHolder holder1) {
        mStringArray = new String[plans.size()];
        mStringArray = plans.toArray(mStringArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose item");

        builder.setItems(mStringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyAppPrefsManager prefsManager = new MyAppPrefsManager(context);
                prefsManager.setPlan(mStringArray[which]);
                Toast.makeText(context, mStringArray[which] + " plan is saved.", Toast.LENGTH_LONG).show();
                // subscription_text.setText(mStringArray[which]);
                selected_plan = mStringArray[which];
                System.out.println("SELE " + selected_plan);
                int iqnt = 1;
                days = 1;
                if (selected_plan.equalsIgnoreCase("DAILY")) {
                    //iqnt = 30;
                    days = 30;
                }
                if (selected_plan.equalsIgnoreCase("EVERY 3 DAY")) {
                    //iqnt = 10;
                    days = 10;
                }
                if (selected_plan.equalsIgnoreCase("ALTERNATE DAY")) {
                    //iqnt = 15;
                    days = 15;
                }
                if (selected_plan.equalsIgnoreCase("WEEKLY")) {
                    //iqnt = 5;
                    days = 5;
                }

                double price = Double.parseDouble(cartProduct1.getCustomersBasketProduct().getProductsFinalPrice()) * iqnt;
                price = price * days;

                // Set Final Price and Quantity
                cartProduct1.getCustomersBasketProduct().setTotalPrice("" + price);
                cartProduct1.getCustomersBasketProduct().setCustomersBasketQuantity(iqnt);
                cartProduct1.getCustomersBasketProduct().setManufacturersName(selected_plan);
                // Update CartItem in Local Database using static method of My_Cart
                My_Cart.UpdateCartItem(cartProduct1);
                // Calculate Cart's Total Price Again
                setCartTotal();
                notifyItemChanged(holder1.getAdapterPosition());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    int mYear;
    int mMonth;
    int mDay;

    //start - end date
    public void setDate(boolean startPick, DatePicked datePicked) {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(context,
                (datepicker, selectedyear, selectedmonth, selectedday) -> {
                    Calendar myCalendar = Calendar.getInstance();
                    myCalendar.set(Calendar.YEAR, selectedyear);
                    myCalendar.set(Calendar.MONTH, selectedmonth);
                    myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                    String myFormat = "dd-MMM-yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                    //dateTV.setText(sdf.format(myCalendar.getTime()));
                    if (datePicked != null) {
                        datePicked.onDatePicked(sdf.format(myCalendar.getTime()), myCalendar);
                    }
                    String myFormat1 = "yyyy-MM-dd";
                    SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1, Locale.ENGLISH);
                    String selD = sdf1.format(myCalendar.getTime());

                    mDay = selectedday;
                    mMonth = selectedmonth;
                    mYear = selectedyear;
                }, mYear, mMonth, mDay);
        //mDatePicker.setTitle("Select date");
        mDatePicker.getDatePicker().setMinDate(startPick ? (System.currentTimeMillis() - 1000) : start.get().getTimeInMillis());
        mDatePicker.show();
    }

    private interface DatePicked {
        void onDatePicked(String date, Calendar calendar);
    }

    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return (cartItemsList.size() == 0) ? cartItemsList.size() : cartItemsList.size() + 1;
    }


    //********** Return the view type of the item at position for the purposes of view recycling *********//

    @Override
    public int getItemViewType(int position) {
        return (position == cartItemsList.size()) ? R.layout.layout_button : R.layout.layout_card_cart_items;
    }

    //*********** Calculate and Set the Cart's Total Price ********//

    public void setCartTotal() {
        double finalPrice = 0;
        boolean subs = false;
        List<CartProduct> cartItemsList = user_cart_db.getCartItems();
        for (int i = 0; i < cartItemsList.size(); i++) {
            // Update the Cart's Total Price
            if (cartItemsList.get(i).getCustomersBasketProduct().getProducts_subscription().equalsIgnoreCase("1")) {
                subs = true;
            }
            finalPrice += Double.parseDouble(cartItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
        }
        AppSettingsDetails cartSetting = ((App) App.getContext().getApplicationContext()).getAppSettingsDetails();
        String minPrice = cartSetting.getMinPrice();
        Double mp = Double.parseDouble(minPrice);
        if (finalPrice < mp && !subs) {
            cartFragment.cart_total_wrap.setVisibility(View.GONE);
            cartFragment.cart_mp_wraning.setVisibility(View.VISIBLE);
        } else {
            cartFragment.cart_total_wrap.setVisibility(View.VISIBLE);
            cartFragment.cart_mp_wraning.setVisibility(View.GONE);
        }
        cartFragment.cart_total_price.setText(context.getString(R.string.total) + " : " + ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(finalPrice));
        cartFragment.cart_item_total_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(finalPrice));
        cartFragment.cart_item_discount_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(0.0));
        cartFragment.cart_item_subtotal_price.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(finalPrice));
    }

    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final Button custom_button;
        private final Button cart_item_viewBtn;
//        private final Button checkAvailabilityBT;
//        private final LinearLayout llStartEndDate;
//        private final Button startDate, endDate;
//        private final TextView noOfDays;
        private final ImageView cart_item_cover;
        private final RecyclerView attributes_recycler;
//        private final EditText postcodeET;
        private final ImageButton cart_item_quantity_minusBtn;
        private final ImageButton cart_item_quantity_plusBtn;
        private final TextView cart_item_removeBtn;
        private final TextView text;
//        private final RelativeLayout subscriptionPlanRL;
//        private final TextView subscription_text;
        private final TextView cart_item_title;
        private final TextView cart_item_size;
        private final TextView cart_item_category;
        private final TextView cart_item_base_price;
        private final TextView cart_item_sub_price;
        private final TextView cart_item_quantity;


        public MyViewHolder(final View itemView) {
            super(itemView);

            custom_button = itemView.findViewById(R.id.custom_button);
//            checkAvailabilityBT = itemView.findViewById(R.id.checkAvailabilityBT);
//            postcodeET = itemView.findViewById(R.id.postcodeET);
//            subscriptionPlanRL = itemView.findViewById(R.id.subscriptionPlanRL);
//
//            llStartEndDate = itemView.findViewById(R.id.ll_start_end_date);
//            startDate = itemView.findViewById(R.id.btn_from_date);
//            endDate = itemView.findViewById(R.id.btn_to_date);
//            noOfDays = itemView.findViewById(R.id.tv_no_days);
//
//            subscription_text = itemView.findViewById(R.id.subscription_text);
            cart_item_title = itemView.findViewById(R.id.cart_item_title);
            text = itemView.findViewById(R.id.text);
            cart_item_base_price = itemView.findViewById(R.id.cart_item_base_price);
            cart_item_sub_price = itemView.findViewById(R.id.cart_item_sub_price);
            cart_item_quantity = itemView.findViewById(R.id.cart_item_quantity);
            cart_item_category = itemView.findViewById(R.id.cart_item_category);
            cart_item_size = itemView.findViewById(R.id.cart_item_size);
            cart_item_cover = itemView.findViewById(R.id.cart_item_cover);
            cart_item_viewBtn = itemView.findViewById(R.id.cart_item_viewBtn);
            cart_item_removeBtn = itemView.findViewById(R.id.cart_item_removeBtn);
            cart_item_quantity_plusBtn = itemView.findViewById(R.id.cart_item_quantity_plusBtn);
            cart_item_quantity_minusBtn = itemView.findViewById(R.id.cart_item_quantity_minusBtn);

            attributes_recycler = itemView.findViewById(R.id.cart_item_attributes_recycler);
        }

    }

}

