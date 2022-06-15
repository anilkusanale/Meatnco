package com.mytodokart.app.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.mytodokart.app.activities.Referfriend;
import com.mytodokart.app.app.MyAppPrefsManager;
import com.mytodokart.app.models.SlotData;
import com.mytodokart.app.models.del_slots;
import com.mytodokart.app.models.user_model.UserData;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.razorpay.Checkout;
import com.mytodokart.app.R;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.adapters.DemoCouponsListAdapter;
import com.mytodokart.app.adapters.PaymentMethodAdapter;
import com.mytodokart.app.app.App;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.customs.DialogLoader;
import com.mytodokart.app.databases.User_Cart_DB;
import com.mytodokart.app.databases.User_Info_DB;
import com.mytodokart.app.adapters.CouponsAdapter;
import com.mytodokart.app.adapters.CheckoutItemsAdapter;
import com.mytodokart.app.enums.AppEnvironment;
import com.mytodokart.app.models.address_model.AddressDetails;
import com.mytodokart.app.models.cart_model.CartProduct;
import com.mytodokart.app.models.cart_model.CartProductAttributes;
import com.mytodokart.app.models.coupons_model.CouponsData;
import com.mytodokart.app.models.coupons_model.CouponsInfo;
import com.mytodokart.app.models.order_model.OrderData;
import com.mytodokart.app.models.pay_tm.Checksum;
import com.mytodokart.app.models.pay_tm.Paytm;
import com.mytodokart.app.models.payment_model.PaymentMethodsData;
import com.mytodokart.app.models.payment_model.PaymentMethodsInfo;
import com.mytodokart.app.models.order_model.PostOrder;
import com.mytodokart.app.models.order_model.PostProductsAttributes;
import com.mytodokart.app.models.order_model.PostProducts;
import com.mytodokart.app.models.product_model.Option;
import com.mytodokart.app.models.product_model.Value;
import com.mytodokart.app.models.shipping_model.ShippingService;
import com.mytodokart.app.models.user_model.UserDetails;
import com.mytodokart.app.customs.DividerItemDecoration;
import com.mytodokart.app.network.APIClient;
import com.mytodokart.app.utils.NotificationHelper;
import com.mytodokart.app.utils.ValidateInputs;
import com.mytodokart.app.utils.Utilities;
import org.json.JSONException;
import org.json.JSONObject;
import am.appwise.components.ni.NoInternetDialog;
import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;


public class CheckoutFinal extends Fragment {

    View rootView;
    AlertDialog demoCouponsDialog;
    private String[] mStringArray;
    boolean disableOtherCoupons = false;
    Call<SlotData> networkCall;
    String tax;
    String selD;
    String braintreeToken;
    String selectedPaymentMethod;
    public String paymentNonceToken = "";
    long paidOrderId = 0;
    String razorOrderId = "";
    double checkoutSubtotal, checkoutTax, packingCharges, checkoutShipping, checkoutShippingCost, checkoutDiscount, checkoutTotal = 0;
    Button checkout_paypal_btn;
    Button checkout_payment_btn;
    CardView card_details_layout,subDateCard,subTimeCard;
    CardView payment_details_layout;
    ProgressDialog progressDialog;
    NestedScrollView scroll_container;
    RecyclerView checkout_items_recycler;
    RecyclerView checkout_coupons_recycler;
    Button checkout_coupon_btn, checkout_order_btn, checkout_cancel_btn;
    ImageButton edit_billing_Btn, edit_shipping_Btn, edit_shipping_method_Btn;
    TextView checkout_subtotal, checkout_tax, checkout_shipping, checkout_discount, checkout_total,
            checkout_packing_charges, demo_coupons_text;
    TextView dateTV,slotTV,billing_name, billing_street, billing_address, shipping_name, shipping_street, shipping_address, shipping_method, payment_method;
    EditText payment_name, payment_email, payment_phone, checkout_coupon_code, checkout_comments, checkout_card_number, checkout_card_cvv, checkout_card_expiry;


    List<CouponsInfo> couponsList;
    List<CartProduct> checkoutItemsList;
    List<PaymentMethodsInfo> paymentMethodsList;
    List<String> bdate = new ArrayList<>();

    UserDetails userInfo;
    DialogLoader dialogLoader;
    AddressDetails billingAddress;
    AddressDetails shippingAddress;
    CouponsAdapter couponsAdapter;
    ShippingService shippingMethod;
    CheckoutItemsAdapter checkoutItemsAdapter;
    MyAppPrefsManager prefsManager;
    User_Cart_DB user_cart_db = new User_Cart_DB();
    User_Info_DB user_info_db = new User_Info_DB();

    private String PAYMENT_CURRENCY = "USD";
    private String PAYMENT_ENVIRONMENT = "Test";
    private String STRIPE_PUBLISHABLE_KEY = "";
    private String PAYPAL_PUBLISHABLE_KEY = "";
    private String INSTAMOJO_PUBLISHABLE_KEY = "";

    // Payumoney credentials
    private String PAYUMONEY_PUBLIC_KEY = "";
    private String PAYUMONEY_CLIENT_ID = "";
    private String PAYUMONEY_CLIENT_SECRET = "";

    //RozarPay Credential
    private String ROZARPAY_PUBLIC_KEY = "rzp_test_yR4RfwC7Xtoq4s";

    //PayTM Credentials
    public String PAYTM_M_ID = ""; //Paytm Merchand Id we got it in paytm credentials
    public String CHANNEL_ID = "WEB"; //Paytm Channel Id, got it in paytm credentials
    public String INDUSTRY_TYPE_ID = "Retail"; //Paytm industry type got it in paytm credential
    public String WEBSITE = "Ecommerce";
    public String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
    List<del_slots> slotDataList = new ArrayList<>();
    //private static PayPalConfiguration payPalConfiguration;
    private static final int SIMPLE_PAYPAL_REQUEST_CODE = 123;
    RelativeLayout discWrap;

    int FLAG_PAYMENT;

    Context context;

    private boolean isDisableExitConfirmation = false;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;


    List<PostProducts> orderProductList;
    //Add order products name to String array
    List<String> productsName;
    ArrayList<String> slots = new ArrayList<>();
    //Add order id for payment methods
    String orderID;
    My_Cart my_cart;
    private int mYear,mMonth,mDay;
    boolean payment_enable=false;
    boolean isSubscribed = false;
    String userCurrentMobile;
    String userCode = "";

    private CardView wallet_wrap;
    private LinearLayout user_wallet_box;
    private CheckBox wallet_checkbox;
    private TextView available_wallet;
    String userWallet = "0";
    boolean walletUsed = false;
    int usedWalletPints = 0;
    int useableWallet = 0;

    public CheckoutFinal(My_Cart my_cart) {
        this.my_cart = my_cart;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.checkout, container, false);
        prefsManager = new MyAppPrefsManager(context);
        // Set the Title of Toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.checkout));

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

        // Get selectedShippingMethod, billingAddress and shippingAddress from ApplicationContext
        tax = ((App) getContext().getApplicationContext()).getTax();
        shippingMethod = ((App) getContext().getApplicationContext()).getShippingService();
        billingAddress = ((App) getContext().getApplicationContext()).getBillingAddress();
        shippingAddress = ((App) getContext().getApplicationContext()).getShippingAddress();

        // Get userInfo from Local Databases User_Info_DB
        userInfo = user_info_db.getUserData(getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", null));
        userCurrentMobile = userInfo.getPhone();
        userCode = getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userUniqueID", null);

        // Binding Layout Views
        available_wallet = (TextView) rootView.findViewById(R.id.available_wallet);
        wallet_checkbox = (CheckBox) rootView.findViewById(R.id.wallet_checkbox);
        user_wallet_box = (LinearLayout) rootView.findViewById(R.id.user_wallet_box);
        wallet_wrap = (CardView) rootView.findViewById(R.id.wallet_wrap);

        dateTV = (TextView) rootView.findViewById(R.id.dateTV);
        slotTV = (TextView) rootView.findViewById(R.id.slotTV);
        checkout_order_btn = (Button) rootView.findViewById(R.id.checkout_order_btn);
        checkout_cancel_btn = (Button) rootView.findViewById(R.id.checkout_cancel_btn);
        checkout_coupon_btn = (Button) rootView.findViewById(R.id.checkout_coupon_btn);
        edit_billing_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_billing);
        edit_shipping_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_shipping);
        edit_shipping_method_Btn = (ImageButton) rootView.findViewById(R.id.checkout_edit_shipping_method);
        shipping_method = (TextView) rootView.findViewById(R.id.shipping_method);
        payment_method = (TextView) rootView.findViewById(R.id.payment_method);
        checkout_subtotal = (TextView) rootView.findViewById(R.id.checkout_subtotal);
        checkout_tax = (TextView) rootView.findViewById(R.id.checkout_tax);
        checkout_packing_charges = (TextView) rootView.findViewById(R.id.checkout_packing_charges);
        checkout_shipping = (TextView) rootView.findViewById(R.id.checkout_shipping);
        checkout_discount = (TextView) rootView.findViewById(R.id.checkout_discount);
        checkout_total = (TextView) rootView.findViewById(R.id.checkout_total);
        shipping_name = (TextView) rootView.findViewById(R.id.shipping_name);
        shipping_street = (TextView) rootView.findViewById(R.id.shipping_street);
        shipping_address = (TextView) rootView.findViewById(R.id.shipping_address);
        billing_name = (TextView) rootView.findViewById(R.id.billing_name);
        billing_street = (TextView) rootView.findViewById(R.id.billing_street);
        billing_address = (TextView) rootView.findViewById(R.id.billing_address);
        demo_coupons_text = (TextView) rootView.findViewById(R.id.demo_coupons_text);
        checkout_coupon_code = (EditText) rootView.findViewById(R.id.checkout_coupon_code);
        checkout_comments = (EditText) rootView.findViewById(R.id.checkout_comments);
        checkout_items_recycler = (RecyclerView) rootView.findViewById(R.id.checkout_items_recycler);
        checkout_coupons_recycler = (RecyclerView) rootView.findViewById(R.id.checkout_coupons_recycler);
        discWrap = (RelativeLayout) rootView.findViewById(R.id.discWrap);

        subDateCard = (CardView) rootView.findViewById(R.id.subDateCard);
        subTimeCard = (CardView) rootView.findViewById(R.id.subTimeCard);
        card_details_layout = (CardView) rootView.findViewById(R.id.card_details_layout);
        payment_details_layout = (CardView) rootView.findViewById(R.id.payment_details_layout);
        checkout_paypal_btn = (Button) rootView.findViewById(R.id.checkout_paypal_btn);
        checkout_payment_btn = (Button) rootView.findViewById(R.id.checkout_payment_btn);
        payment_name = (EditText) rootView.findViewById(R.id.payment_name);
        payment_email = (EditText) rootView.findViewById(R.id.payment_email);
        payment_phone = (EditText) rootView.findViewById(R.id.payment_phone);
        checkout_card_number = (EditText) rootView.findViewById(R.id.checkout_card_number);
        checkout_card_cvv = (EditText) rootView.findViewById(R.id.checkout_card_cvv);
        checkout_card_expiry = (EditText) rootView.findViewById(R.id.checkout_card_expiry);
        scroll_container = (NestedScrollView) rootView.findViewById(R.id.scroll_container);
//        braintreeSupportedCards = (SupportedCardTypesView) rootView.findViewById(R.id.supported_card_types);
        PAYMENT_CURRENCY = ConstantValues.CURRENCY_CODE;
//        braintreeSupportedCards.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
      //  checkout_order_btn.setEnabled(false);
        card_details_layout.setVisibility(View.GONE);
        checkout_paypal_btn.setVisibility(View.GONE);
        payment_details_layout.setVisibility(View.GONE);
        checkout_items_recycler.setNestedScrollingEnabled(false);
        checkout_coupons_recycler.setNestedScrollingEnabled(false);
        checkout_card_expiry.setKeyListener(null);
        dialogLoader = new DialogLoader(getContext());
        couponsList = new ArrayList<>();
        checkoutItemsList = new ArrayList<>();
        paymentMethodsList = new ArrayList<>();
        wallet_wrap.setVisibility(View.GONE);
        getUserDataPhoneLogin();

        // Get checkoutItems from Local Databases User_Cart_DB
        checkoutItemsList = user_cart_db.getCartItems();

        //ProductsName Array intialize
        productsName = new ArrayList<>();

        // Add orders to orderProductList

        addProductsToList();
        getSlots("OnlyDate");
        // Adding products name to array
        for (int i = 0; i < orderProductList.size(); i++) {
            System.out.println("SUB "+orderProductList.get(i).getproducts_subscription());
            productsName.add(orderProductList.get(i).getProductsName());
            if(orderProductList.get(i).getproducts_subscription().equalsIgnoreCase("1")){
                isSubscribed = true;
            }
        }

//        if(isSubscribed){
//            subDateCard.setVisibility(View.VISIBLE);
//            subTimeCard.setVisibility(View.VISIBLE);
//        } else {
//            subDateCard.setVisibility(View.GONE);
//            subTimeCard.setVisibility(View.GONE);
//        }

        // Request Payment Methods
        RequestPaymentMethods();
        /*RazorPayInitialization*/
        /**
         * Preload payment resources
         */
        Checkout.preload(getContext().getApplicationContext());
        // Initialize the CheckoutItemsAdapter for RecyclerView
        checkoutItemsAdapter = new CheckoutItemsAdapter(getContext(), checkoutItemsList);
        // Set the Adapter, LayoutManager and ItemDecoration to the RecyclerView
        checkout_items_recycler.setAdapter(checkoutItemsAdapter);
        checkout_items_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        checkout_items_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        // Initialize the CouponsAdapter for RecyclerView
        couponsAdapter = new CouponsAdapter(getContext(), couponsList, true, CheckoutFinal.this);
        // Set the Adapter, LayoutManager and ItemDecoration to the RecyclerView
        checkout_coupons_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        checkout_coupons_recycler.setAdapter(couponsAdapter);
        couponsAdapter.notifyDataSetChanged();
        checkoutTax = Double.parseDouble(tax);
        packingCharges = Double.parseDouble("" + ConstantValues.PACKING_CHARGE);
        shipping_method.setText(shippingMethod.getName());
        checkoutShipping = checkoutShippingCost = Double.parseDouble(shippingMethod.getRate() == null || shippingMethod.getRate().isEmpty() ? "0.0" : shippingMethod.getRate())
                + Double.parseDouble(shippingAddress.getDelivery_cost() == null || shippingAddress.getDelivery_cost().isEmpty() ? "0.0" : shippingAddress.getDelivery_cost());

        // Set Billing Details
        shipping_name.setText(shippingAddress.getFirstname() + " " + shippingAddress.getLastname());
        shipping_address.setText(shippingAddress.getZoneName() + ", " + shippingAddress.getCountryName());
        shipping_street.setText(shippingAddress.getStreet());

        // Set Billing Details
        billing_name.setText(billingAddress.getFirstname() + " " + billingAddress.getLastname());
        billing_address.setText(billingAddress.getZoneName() + ", " + billingAddress.getCountryName());
        billing_street.setText(billingAddress.getStreet());
        payment_name.setText(billingAddress.getFirstname() + " " + billingAddress.getLastname());
        payment_phone.setText(billingAddress.getPhone());
        payment_email.setText(userInfo.getEmail());

        // Set CheckoutFinal Total
        setCheckoutTotal();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.processing));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        slotTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        user_wallet_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walletUsed = !walletUsed;
                if(walletUsed){
                    usedWalletPints = useableWallet;
                    wallet_checkbox.setChecked(true);
                } else {
                    //usedWalletPints = 0;
                    wallet_checkbox.setChecked(false);
                }
                setCheckoutTotal();
            }
        });

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd-MM-yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        dateTV.setText(sdf.format(myCalendar.getTime()));
                        String myFormat1 = "yyyy-MM-dd";
                        SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1, Locale.ENGLISH);
                        selD = sdf1.format(myCalendar.getTime());
                        if(bdate.contains(selD)){
                            alertError("Delivery slot not available for selected date. Kindly select next available date");
                            dateTV.setText("Select Delivery Date");
                        } else {
                            slotTV.setText("Select Slot Timing");
                            getSlots(dateTV.getText().toString());
                        }

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                //mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                mDatePicker.show();
            }
        });
        // Handle the Click event of edit_payment_method_Btn
        payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(getContext(), paymentMethodsList);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

                dialog_button.setVisibility(View.GONE);

                dialog_title.setText(getString(R.string.payment_method));
                dialog_list.setAdapter(paymentMethodAdapter);


                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        PaymentMethodsInfo userSelectedPaymentMethod = paymentMethodAdapter.getItem(position);

                        payment_method.setText(userSelectedPaymentMethod.getName());
                        selectedPaymentMethod = userSelectedPaymentMethod.getMethod();

                        payment_enable=true;
//                        checkout_order_btn.setEnabled(true);
//                        checkout_order_btn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentGreen));


                        // Check the selected Payment Method
                        switch (userSelectedPaymentMethod.getMethod()) {

                            // Change the Visibility of some Views based on selected Payment Method
                            case "cod":
                                checkout_order_btn.setEnabled(true);
                                checkout_order_btn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentGreen));
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                payment_details_layout.setVisibility(View.GONE);
                                break;

                            case "paypal":
                                checkout_paypal_btn.setVisibility(View.VISIBLE);
                                card_details_layout.setVisibility(View.GONE);
                                payment_details_layout.setVisibility(View.GONE);
                                break;

                            case "instamojo":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                payment_details_layout.setVisibility(View.VISIBLE);
                                FLAG_PAYMENT = 0;
                                break;

                            case "payumoney":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                payment_details_layout.setVisibility(View.VISIBLE);
                                FLAG_PAYMENT = 1;
                                break;
                            case "razorpay":
                                checkout_order_btn.setEnabled(true);
                                checkout_order_btn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentGreen));
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                payment_details_layout.setVisibility(View.VISIBLE);
                                FLAG_PAYMENT = 2;
                                break;

                            case "stripe":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.VISIBLE);
                                payment_details_layout.setVisibility(View.GONE);

                                checkout_card_number.setText("4242424242424242");
                                checkout_card_cvv.setText("123");
                                checkout_card_expiry.setText("");
                                break;

                            case "paytm":
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                payment_details_layout.setVisibility(View.GONE);
                                break;

                            default:
                                checkout_paypal_btn.setVisibility(View.GONE);
                                card_details_layout.setVisibility(View.GONE);
                                payment_details_layout.setVisibility(View.GONE);
                                break;
                        }

                        scroll_container.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll_container.fullScroll(scroll_container.FOCUS_DOWN);
                            }
                        });


                        alertDialog.dismiss();

                    }
                });

            }
        });


        // Handle Touch event of input_dob EditText
        checkout_card_expiry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Get Calendar instance
                    final Calendar calendar = Calendar.getInstance();

                    // Initialize DateSetListener of DatePickerDialog
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            // Set the selected Date Info to Calendar instance
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);

                            // Set Date Format
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy", Locale.US);

                            // Set Date in input_dob EditText
                            checkout_card_expiry.setText(dateFormat.format(calendar.getTime()));
                        }
                    };


                    // Initialize DatePickerDialog
                    DatePickerDialog datePicker = new DatePickerDialog
                            (
                                    getContext(),
                                    date,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            );

                    // Show datePicker Dialog
                    datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePicker.show();
                }

                return false;
            }
        });


        // Handle the Click event of checkout_paypal_btn Button
        checkout_paypal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        // Handle the Click event of checkout_instamojo_btn Button
        checkout_payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validatePaymentInfo()) {

                    if (FLAG_PAYMENT == 0) {
                        startPaymentInstaMojo();

                    } else if (FLAG_PAYMENT == 1) {
                        // PayUMoney Function
                        launchPayUMoneyFlow();

                    } else if (FLAG_PAYMENT == 2) {
                        if (slotTV.getText().toString().equals("Select Slot Timing")) {
                            alertError("Please choose time slot");
                        } else if (dateTV.getText().toString().equals("Select Delivery Date")) {
                            alertError("Please choose delivery date");
                        } else  {
                            progressDialog.show();
                            proceedOrder();
                        }
                        //startPaymentRazorPay();
                       // updatePayment("");
                    }
                }
            }
        });


        // Handle the Click event of edit_billing_Btn Button
        edit_billing_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate to Billing_Address Fragment to Edit BillingAddress
                Fragment fragment = new Billing_Address(my_cart);
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });


        // Handle the Click event of edit_shipping_Btn Button
        edit_shipping_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate to Shipping_Address Fragment to Edit ShippingAddress
                Fragment fragment = new Shipping_Address(my_cart);
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });


        // Handle the Click event of edit_shipping_method_Btn Button
        edit_shipping_method_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate to Shipping_Methods Fragment to Edit ShippingMethod
                Fragment fragment = new Shipping_Methods(my_cart);
                Bundle args = new Bundle();
                args.putBoolean("isUpdate", true);
                fragment.setArguments(args);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                        .addToBackStack(null).commit();
            }
        });


        if (!ConstantValues.IS_CLIENT_ACTIVE) {
            setupDemoCoupons();
        } else {
            demo_coupons_text.setVisibility(View.GONE);
        }


        // Handle the Click event of checkout_coupon_btn Button
        checkout_coupon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(checkout_coupon_code.getText().toString())) {
                    if(!checkout_coupon_code.getText().toString().equalsIgnoreCase(userCode)) {
                        GetCouponInfo(checkout_coupon_code.getText().toString());
                        dialogLoader.showProgressDialog();
                    } else {
                        Toast.makeText(getContext(), "Invalid Coupon", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Handle the Click event of checkout_cancel_btn Button
        checkout_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel the Order and Navigate back to My_Cart Fragment
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack(getString(R.string.actionCart), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        });

        // Handle the Click event of checkout_order_btn Button
        checkout_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (slotTV.getText().toString().equals("Select Slot Timing")) {
                    alertError("Please choose time slot");
                } else if (dateTV.getText().toString().equals("Select Delivery Date")) {
                    alertError("Please choose delivery date");
                } else if (!payment_enable) {
                    alertError("Choose payment option must be compulsory");
                } else {
                    addProductsToList();
                    if (selectedPaymentMethod.equalsIgnoreCase("cod")) {
                        // Proceed Order
                        proceedOrder();
                        progressDialog.show();

                    } else if (selectedPaymentMethod.equalsIgnoreCase("braintree_paypal")
                            || selectedPaymentMethod.equalsIgnoreCase("paypal")) {
                        // Setup Payment Method
                        validateSelectedPaymentMethod();
                        progressDialog.show();

                        // Delay of 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!"".equalsIgnoreCase(paymentNonceToken)) {
                                    // Proceed Order
                                    proceedOrder();
                                } else {
                                    progressDialog.dismiss();
                                    Snackbar.make(view, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }, 2000);

                    } else if (selectedPaymentMethod.equalsIgnoreCase("stripe")
                            || selectedPaymentMethod.equalsIgnoreCase("braintree_card")) {
                        if (validatePaymentCard()) {
                            // Setup Payment Method
                            validateSelectedPaymentMethod();
                            progressDialog.show();

                            // Delay of 2 seconds
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!"".equalsIgnoreCase(paymentNonceToken)) {
                                        // Proceed Order
                                        proceedOrder();
                                    } else {
                                        progressDialog.dismiss();
                                        Snackbar.make(view, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }, 2000);
                        }

                    } else if (selectedPaymentMethod.equalsIgnoreCase("instamojo")) {
                        // Setup Payment Method
                        validateSelectedPaymentMethod();
                        progressDialog.show();
                        // Delay of 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!"".equalsIgnoreCase(paymentNonceToken)) {
                                    // Proceed Order
                                    proceedOrder();
                                } else {
                                    progressDialog.dismiss();
                                    Snackbar.make(view, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }, 2000);

                    } else if (selectedPaymentMethod.equalsIgnoreCase("razorpay")) {

                        // Setup Payment Method
                        validateSelectedPaymentMethod();
                        progressDialog.show();

                        // Delay of 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!"".equalsIgnoreCase(MainActivity.paymentNonceToken)) {
                                    paymentNonceToken = MainActivity.paymentNonceToken;
                                    // Proceed Order
                                    proceedOrder();

                                } else {
                                    progressDialog.dismiss();
                                    Snackbar.make(view, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }, 2000);

                    } else if (selectedPaymentMethod.equalsIgnoreCase("payumoney")) {

                        // Setup Payment Method
                        validateSelectedPaymentMethod();
                        progressDialog.show();

                        // Delay of 2 seconds
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!"".equalsIgnoreCase(paymentNonceToken)) {
                                    // Proceed Order
                                    proceedOrder();
                                } else {
                                    progressDialog.dismiss();
                                    Snackbar.make(view, getString(R.string.invalid_payment_token), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }, 2000);

                    }
                }
            }
        });


        return rootView;
    }

    private void getUserDataPhoneLogin() {
        final String number = userCurrentMobile;
        dialogLoader.showProgressDialog();
        Call<UserData> call = APIClient.getInstance().getUserDataPhoneLogin(number);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful())
                {
                    dialogLoader.hideProgressDialog();
                    UserDetails userDetails = response.body().getData().get(0);
                    userWallet = userDetails.getWalletAmount();
                    System.out.println("Wallet "+userWallet);
                    if(userWallet.equalsIgnoreCase("0")){
                        wallet_wrap.setVisibility(View.GONE);
                    } else {
                        wallet_wrap.setVisibility(View.VISIBLE);
                        setCheckoutTotal();
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"something went wrong",Toast.LENGTH_SHORT).show();
                    dialogLoader.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                dialogLoader.hideProgressDialog();
            }
        });


    }

    private void getSlots(String date) {
        slots.clear();
        dialogLoader.showProgressDialog();
            networkCall = APIClient.getInstance().getSlots(date);
            networkCall.enqueue(new Callback<SlotData>() {
                @Override
                public void onResponse(Call<SlotData> call, retrofit2.Response<SlotData> response) {
                    if (response.isSuccessful()) {
                        dialogLoader.hideProgressDialog();
                        if(response.body().success.equalsIgnoreCase("1")) {
                            slotDataList = response.body().del_slots;
                            for(int i=0;i<slotDataList.size();i++) {
                                if(slotDataList.get(i).slot_id.equalsIgnoreCase("TIME")) {
                                    slots.add(slotDataList.get(i).slot_label);
                                }
                                if(slotDataList.get(i).slot_id.equalsIgnoreCase("DATE")) {
                                    bdate.add(slotDataList.get(i).slot_label);
                                }
                            }
                        }
                        else if (response.body().success.equalsIgnoreCase("0")) {
                        }
                    }
                }

                @Override
                public void onFailure(Call<SlotData> call, Throwable t) {
                    if (!networkCall.isCanceled()) {
                        Toast.makeText(App.getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                    }
                }
            });



    }

    private void startPaymentInstaMojo() {
        JSONObject pay = new JSONObject();
        Activity activity = getActivity();
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        getActivity().registerReceiver(instamojoPay, filter);

        try {
            pay.put("id", INSTAMOJO_PUBLISHABLE_KEY);
            pay.put("name", payment_name.getText().toString());
            pay.put("email", payment_email.getText().toString());
            //pay.put("phone", payment_phone.getText().toString());
            pay.put("phone", "+913070684955");
            pay.put("amount", String.valueOf(checkoutTotal));
            pay.put("currency", PAYMENT_CURRENCY);
            pay.put("purpose", "shopping");
            pay.put("send_sms", false);
            pay.put("send_email", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        InstapayListener listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                String[] responseArray = response.split(":");
                orderID = responseArray[1].substring(responseArray[1].indexOf("=") + 1);
                String paymentId = responseArray[3].substring(responseArray[3].indexOf("=") + 1);
                String token = responseArray[4].substring(responseArray[4].indexOf("=") + 1);
                Log.i("VC_Shop", "[Instamojo] > response=" + response);
                selectedPaymentMethod = "instamojo";
                paymentNonceToken = paymentId;
            }

            @Override
            public void onFailure(int code, String reason) {
                int i = code;
                Toast.makeText(context, "Error: " + reason, Toast.LENGTH_LONG).show();
                Log.i("VC_Shop", "[Instamojo] > reason=" + reason);
            }
        };

        instamojoPay.start(activity, pay, listener);
    }

    public void startPaymentRazorPay() {
        /**
         * Instantiate Checkout
         */
        Checkout co = new Checkout();

        /**
         * Set your logo here
         */
        co.setImage(R.drawable.toolbar_logo);


        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {

            //String txnId = "TXNID720431525261327973";
            String phone = payment_phone.getText().toString().trim();
            String email = payment_email.getText().toString().trim();
            String name = payment_name.getText().toString();

            JSONObject options = new JSONObject();
            // options.put("description", "Order #123456");
            options.put("currency", PAYMENT_CURRENCY);
            options.put("name", "MeatnCo ");
            options.put("order_id",razorOrderId);
            //You can omit the image option to fetch the image from dashboard
            String str1 = new DecimalFormat("#0.00").format(checkoutTotal);
            String finalstr = str1.replace(".", "");

            options.put("amount", Integer.valueOf(finalstr));
            options.put("key", ROZARPAY_PUBLIC_KEY);


            JSONObject preFill = new JSONObject();
            /**
             * Description can be anything
             * eg: Order #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            preFill.put("name", name);
            preFill.put("email", email);
            preFill.put("contact", phone);


            options.put("prefill", preFill);
            co.setKeyID(ROZARPAY_PUBLIC_KEY);
            // co.setPublicKey(ROZARPAY_PUBLIC_KEY);

            co.open(getActivity(), options);

        } catch (Exception e) {
            Log.e("Payment", "Error in starting Razorpay Checkout", e);
        }
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */
    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Done");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle(ConstantValues.APP_HEADER);

        payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try {
            amount = checkoutTotal;

        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        //String txnId = "TXNID720431525261327973";
        String phone = payment_phone.getText().toString().trim();
        String email = payment_email.getText().toString().trim();
        String name = payment_name.getText().toString();
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((App) getContext().getApplicationContext()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setEmail(email)
                .setProductName("" + productsName)
                .setFirstName(name)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(PAYUMONEY_PUBLIC_KEY)
                .setMerchantId(PAYUMONEY_CLIENT_ID);

        try {
            mPaymentParams = builder.build();
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, getActivity(), R.style.AppTheme_default, true);

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            checkout_payment_btn.setEnabled(true);
        }
    }

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        //  AppEnvironment appEnvironment = ((App)  getContext().getApplicationContext()).getAppEnvironment();
        stringBuilder.append(PAYUMONEY_CLIENT_SECRET);

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }


    private String getOrderID() {

        if ("".equalsIgnoreCase(orderID)) {
            Calendar c = Calendar.getInstance();
            double mseconds = c.get(Calendar.MILLISECOND);
            orderID = String.valueOf(mseconds);
        }
        return orderID;
    }

    //*********** Called when the fragment is no longer in use ********//

    @Override
    public void onResume() {
        super.onResume();

    }


    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            // Result Code is -1 send from Payumoney activity
            Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
            if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == getActivity().RESULT_OK && data !=
                    null) {
                TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                        .INTENT_EXTRA_TRANSACTION_RESPONSE);

                ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

                // Check which object is non-null
                if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                    if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                        //Success Transaction

                        paymentNonceToken = transactionResponse.getPayuResponse();


                    } else {

                        Snackbar.make(rootView, transactionResponse.getMessage(), Snackbar.LENGTH_LONG).show();
                        //Failure Transaction
                    }

                } else if (resultModel != null && resultModel.getError() != null) {
                    Log.d("VC_Shop", "Error response : " + resultModel.getError().getTransactionResponse());
                } else {
                    Log.d("VC_Shop", "Both objects are null!");
                }
            } else if (requestCode == SIMPLE_PAYPAL_REQUEST_CODE) {

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("VC_Shop", "[paypal] > The user canceled.");
        }
    }


    //*********** Validate Payment method Details according to the selectedPaymentMethod ********//

    private void validateSelectedPaymentMethod() {

        if (selectedPaymentMethod.equalsIgnoreCase("paypal")) {
            return;

        } else if (selectedPaymentMethod.equalsIgnoreCase("instamojo")) {
            return;

        } else {
            return;
        }

    }


    //*********** Returns Final Price of User's Cart ********//

    private double getProductsSubTotal() {

        double finalPrice = 0;

        for (int i = 0; i < checkoutItemsList.size(); i++) {
            // Add the Price of each Cart Product to finalPrice
            finalPrice += Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
        }

        return finalPrice;
    }
    private double getDiscountTotal() {

        double finalPrice = 0;

        for (int i = 0; i < checkoutItemsList.size(); i++) {
            if(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsBasePrice()!=null) {
                // Add the Price of each Cart Product to finalPrice
                double pprice = 0;
                double bprice = 0;
                double dd = 0;
                int qnt = 0;
                pprice = Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsPrice());
                bprice = Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsBasePrice());
                qnt = Integer.valueOf(checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                if (bprice > pprice) {
                    dd = bprice - pprice;
                    dd = dd * qnt;
                    finalPrice += dd;
                }
            }
           // finalPrice += Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
        }

        return finalPrice;
    }


    //*********** Set CheckoutFinal's Subtotal, Tax, ShippingCost, Discount and Total Prices ********//

    private void setCheckoutTotal() {

        // Get Cart Total
        checkoutSubtotal = getProductsSubTotal();
        // Calculate CheckoutFinal Total
        checkoutSubtotal = checkoutSubtotal-checkoutTax;
        int uwallet = Integer.parseInt(userWallet);
        if(uwallet>0){
            if(checkoutSubtotal<=250){
                if(uwallet>50){
                    useableWallet = 50;
                } else {
                    useableWallet = uwallet;
                }
            }
            if(checkoutSubtotal>250 && checkoutSubtotal<=500){
                if(uwallet>100){
                    useableWallet = 100;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>500 && checkoutSubtotal<=750){
                if(uwallet>150){
                    useableWallet = 150;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>750 && checkoutSubtotal<=1000){
                if(uwallet>200){
                    useableWallet = 200;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>1000 && checkoutSubtotal<=1250){
                if(uwallet>250){
                    useableWallet = 250;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>1250 && checkoutSubtotal<=1500){
                if(uwallet>300){
                    useableWallet = 300;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>1500 && checkoutSubtotal<=1750){
                if(uwallet>350){
                    useableWallet = 350;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>1750 && checkoutSubtotal<=2000){
                if(uwallet>400){
                    useableWallet = 400;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>2000 && checkoutSubtotal<=2250){
                if(uwallet>450){
                    useableWallet = 450;
                } else {
                    useableWallet = uwallet;
                }
            }

            if(checkoutSubtotal>2250){
                if(uwallet>500){
                    useableWallet = 500;
                } else {
                    useableWallet = uwallet;
                }
            }
            available_wallet.setText(useableWallet+"");
        }
        if(walletUsed){
            checkoutDiscount +=usedWalletPints;
        } else {
            checkoutDiscount =checkoutDiscount-usedWalletPints;
        }
        checkoutTotal = checkoutSubtotal + checkoutTax + packingCharges + checkoutShipping - checkoutDiscount;
        checkoutTotal = Math.ceil(checkoutTotal);
        //if(checkoutDiscount.) {
            checkoutDiscount += getDiscountTotal();
       // }

        if(checkoutDiscount<1){
            discWrap.setVisibility(View.GONE);
        } else {
            discWrap.setVisibility(View.VISIBLE);
        }
        // Set CheckoutFinal Details
        checkout_tax.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTax));
        checkout_shipping.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutShipping));
        checkout_discount.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutDiscount));

        checkout_packing_charges.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(packingCharges));
        checkout_subtotal.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutSubtotal));
        checkout_total.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(checkoutTotal));

    }

    private void showAlertDialog() {
        mStringArray = new String[slots.size()];
        mStringArray = slots.toArray(mStringArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose One Slot");

        builder.setItems(mStringArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                slotTV.setText(mStringArray[which]);
                prefsManager.setSlot(mStringArray[which]);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void addProductsToList() {
        orderProductList = new ArrayList<>();
        for (int i = 0; i < checkoutItemsList.size(); i++) {
            PostProducts orderProduct = new PostProducts();
            // Get current Product Details
            orderProduct.setProductsId(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId());
            orderProduct.setProductsName(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsName());
            orderProduct.setModel(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsModel());
            orderProduct.setImage(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsImage());
            orderProduct.setWeight(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsWeight());
            orderProduct.setUnit(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsWeightUnit());
            orderProduct.setManufacture(checkoutItemsList.get(i).getCustomersBasketProduct().getManufacturersName());
            orderProduct.setCategoriesId(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());
            orderProduct.setCategoriesName(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryNames());
            orderProduct.setPrice(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsPrice());
            orderProduct.setFinalPrice(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice());
            orderProduct.setSubtotal(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
            orderProduct.setTotal(checkoutItemsList.get(i).getCustomersBasketProduct().getTotalPrice());
            orderProduct.setproducts_subscription(prefsManager.getProductSubscription());
            orderProduct.setsubs_freq(prefsManager.getPlan());
            orderProduct.setSlot(prefsManager.getSlot());
            orderProduct.setDel_date(dateTV.getText().toString().trim());
            orderProduct.setCustomersBasketQuantity(checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());

           // orderProduct.setOnSale(checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1"));
            List<PostProductsAttributes> productAttributes = new ArrayList<>();

            for (int j = 0; j < checkoutItemsList.get(i).getCustomersBasketProductAttributes().size(); j++) {
                CartProductAttributes cartProductAttributes = checkoutItemsList.get(i).getCustomersBasketProductAttributes().get(j);
                Option attributeOption = cartProductAttributes.getOption();
                Value attributeValue = cartProductAttributes.getValues().get(0);

                PostProductsAttributes attribute = new PostProductsAttributes();
                attribute.setProductsOptionsId(String.valueOf(attributeOption.getId()));
                attribute.setProductsOptions(attributeOption.getName());
                attribute.setProductsOptionsValuesId(String.valueOf(attributeValue.getId()));
                attribute.setProductsOptionsValues(attributeValue.getValue());
                attribute.setOptionsValuesPrice(attributeValue.getPrice());
                attribute.setPricePrefix(attributeValue.getPricePrefix());
                attribute.setAttributeName(attributeValue.getValue() + " " + attributeValue.getPricePrefix() + attributeValue.getPrice());

                productAttributes.add(attribute);
            }

            orderProduct.setAttributes(productAttributes);


            // Add current Product to orderProductList
            orderProductList.add(orderProduct);
        }
    }


    //*********** Set Order Details to proceed CheckoutFinal ********//


    public void proceedOrder() {
        PostOrder orderDetails = new PostOrder();

        // Set Customer Info
        orderDetails.setCustomersId(Integer.parseInt(userInfo.getId()));
        orderDetails.setCustomersName(userInfo.getFirstName());
        orderDetails.setCustomersTelephone(shippingAddress.getPhone());
        orderDetails.setEmail(userInfo.getEmail());

        // Set Shipping  Info
        orderDetails.setDeliveryFirstname(shippingAddress.getFirstname());
        orderDetails.setDeliveryLastname(shippingAddress.getLastname());
        orderDetails.setDeliveryStreetAddress(shippingAddress.getStreet());
        orderDetails.setDeliveryPostcode(shippingAddress.getPostcode());
        orderDetails.setDeliveryPhone(shippingAddress.getPhone());
        orderDetails.setDeliverySuburb(shippingAddress.getSuburb());
        orderDetails.setDeliveryCity(shippingAddress.getCity());
        orderDetails.setDeliveryZone(shippingAddress.getZoneName());
        orderDetails.setDeliveryState(shippingAddress.getZoneName());
        orderDetails.setDeliverySuburb(shippingAddress.getZoneName());
        orderDetails.setDeliveryCountry(shippingAddress.getCountryName());
        orderDetails.setDeliveryZoneId(String.valueOf(shippingAddress.getZoneId()));
        orderDetails.setDeliveryCountryId(String.valueOf(shippingAddress.getCountriesId()));
       // orderDetails.setDeliveryTime("" + shippingAddress.getDelivery_time());
        orderDetails.setDeliveryTime(dateTV.getText().toString());
        orderDetails.setDeliveryBatch(prefsManager.getSlot());
        orderDetails.setDeliveryCost("" + checkoutShipping);
        orderDetails.setPackingChargeTax(ConstantValues.PACKING_CHARGE);

        // LatLang
        orderDetails.setLatitude(shippingAddress.getLatitude());
        orderDetails.setLongitude(shippingAddress.getLongitude());

        // Set Billing Info
        orderDetails.setBillingFirstname(billingAddress.getFirstname());
        orderDetails.setBillingLastname(billingAddress.getLastname());
        orderDetails.setBillingStreetAddress(billingAddress.getStreet());
        orderDetails.setBillingPostcode(billingAddress.getPostcode());
        orderDetails.setBillingPhone(billingAddress.getPhone());
        orderDetails.setBillingSuburb(billingAddress.getSuburb());
        orderDetails.setBillingCity(billingAddress.getCity());
        orderDetails.setBillingZone(billingAddress.getZoneName());
        orderDetails.setBillingState(billingAddress.getZoneName());
        orderDetails.setBillingSuburb(billingAddress.getZoneName());
        orderDetails.setBillingCountry(billingAddress.getCountryName());
        orderDetails.setBillingZoneId(String.valueOf(billingAddress.getZoneId()));
        orderDetails.setBillingCountryId(String.valueOf(billingAddress.getCountriesId()));

        orderDetails.setLanguageId(ConstantValues.LANGUAGE_ID);

        orderDetails.setTaxZoneId(shippingAddress.getZoneId());
        orderDetails.setTotalTax(checkoutTax);
        orderDetails.setShippingCost(checkoutShipping);
        orderDetails.setShippingMethod(shippingMethod.getName());
        orderDetails.setComments(checkout_comments.getText().toString().trim());

        if (couponsList.size() > 0) {
            orderDetails.setIsCouponApplied(1);
        } else {
            orderDetails.setIsCouponApplied(0);
        }
        orderDetails.setCouponAmount(checkoutDiscount);
        orderDetails.setCoupons(couponsList);

        if(walletUsed){
            orderDetails.setWalletUsed("1");
            orderDetails.setWalletAmount(usedWalletPints);
        } else {
            orderDetails.setWalletUsed("0");
            orderDetails.setWalletAmount(0);
        }

      //  Toast.makeText(getContext(),""+orderDetails.getCouponAmount(),Toast.LENGTH_LONG).show();

        // Set PaymentNonceToken and PaymentMethod
        orderDetails.setNonce(paymentNonceToken);
        orderDetails.setPaymentMethod(selectedPaymentMethod);

        // Set CheckoutFinal Price and Products
        orderDetails.setProductsTotal(checkoutSubtotal);
        orderDetails.setTotalPrice(checkoutTotal);
        orderDetails.setProducts(orderProductList);
        orderDetails.setOrder_payment_id(getOrderID());
        orderDetails.setCurrency(PAYMENT_CURRENCY);

        PlaceOrderNow(orderDetails);
    }


    //*********** Request the Server to Generate BrainTreeToken ********//

    private void RequestPaymentMethods() {
        dialogLoader.showProgressDialog();
        Call<PaymentMethodsData> call = APIClient.getInstance()
                .getPaymentMethods(ConstantValues.LANGUAGE_ID);
        call.enqueue(new Callback<PaymentMethodsData>() {
            @Override
            public void onResponse(Call<PaymentMethodsData> call, retrofit2.Response<PaymentMethodsData> response) {

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        String strGson = new Gson().toJson(response.body());
                        for (int i = 0; i < response.body().getData().size(); i++) {

                            PaymentMethodsInfo paymentMethodsInfo = response.body().getData().get(i);

                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("cod")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);
                            }

                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("instamojo")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);

                                PAYMENT_ENVIRONMENT = paymentMethodsInfo.getEnvironment();
//                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();
                                INSTAMOJO_PUBLISHABLE_KEY = paymentMethodsInfo.getPublicKey();
                            }

                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("payumoney")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);

                                PAYMENT_ENVIRONMENT = paymentMethodsInfo.getEnvironment();
//                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();
                                PAYUMONEY_PUBLIC_KEY = paymentMethodsInfo.getPublicKey();
                                PAYUMONEY_CLIENT_ID = paymentMethodsInfo.getClient_id();
                                PAYUMONEY_CLIENT_SECRET = paymentMethodsInfo.getClient_secret();
                            }

                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("razorpay")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);

                                ROZARPAY_PUBLIC_KEY = paymentMethodsInfo.getPublicKey();
                                PAYMENT_ENVIRONMENT = paymentMethodsInfo.getEnvironment();
//                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();

                            }
                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("stripe")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);
                                PAYMENT_ENVIRONMENT = paymentMethodsInfo.getEnvironment();
//                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();
                                STRIPE_PUBLISHABLE_KEY = paymentMethodsInfo.getPublicKey();
                            }

                            if ((paymentMethodsInfo.getMethod().equalsIgnoreCase("braintree_card")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1"))
                                    ||
                                    (paymentMethodsInfo.getMethod().equalsIgnoreCase("braintree_paypal")
                                            && paymentMethodsInfo.getActive().equalsIgnoreCase("1"))) {
                                paymentMethodsList.add(paymentMethodsInfo);

                                PAYMENT_ENVIRONMENT = paymentMethodsInfo.getEnvironment();
//                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();

                            } else {
                                dialogLoader.hideProgressDialog();
                            }
                            if (paymentMethodsInfo.getMethod().equalsIgnoreCase("paytm")
                                    && paymentMethodsInfo.getActive().equalsIgnoreCase("1")) {
                                paymentMethodsList.add(paymentMethodsInfo);

                                PAYMENT_ENVIRONMENT = paymentMethodsInfo.getEnvironment();
//                                PAYMENT_CURRENCY = paymentMethodsInfo.getPaymentCurrency();
                                PAYTM_M_ID = paymentMethodsInfo.getPublicKey();
                            }
                        }


                    } else {
                        // Unexpected Response from Server
                        dialogLoader.hideProgressDialog();
                        Snackbar.make(rootView, getString(R.string.cannot_get_payment_methods), Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getContext(), getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dialogLoader.hideProgressDialog();
                    Toast.makeText(getContext(), getString(R.string.cannot_get_payment_methods), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentMethodsData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    //*********** Request the Server to Generate BrainTreeToken ********//


    //*********** Request the Server to Generate BrainTreeToken ********//

    private void GetCouponInfo(String coupon_code) {

        Call<CouponsData> call = APIClient.getInstance()
                .getCouponInfo
                        (
                                coupon_code,String.valueOf(checkoutSubtotal)
                        );


        call.enqueue(new Callback<CouponsData>() {
            @Override
            public void onResponse(Call<CouponsData> call, retrofit2.Response<CouponsData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        final CouponsInfo couponsInfo = response.body().getData().get(0);

                        if (couponsList.size() != 0 && couponsInfo.getIndividualUse().equalsIgnoreCase("1")) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                            dialog.setTitle(getString(R.string.add_coupon));
                            dialog.setMessage(getString(R.string.coupon_removes_other_coupons));

                            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent")) {
                                        if (validateCouponCart(couponsInfo)){
                                            System.out.println("Coupan valid");
                                            applyCoupon(couponsInfo);
                                        } else {
                                            System.out.println("Coupan not valid");
                                        }


                                    } else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                            || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product")) {
                                        if (validateCouponProduct(couponsInfo))
                                            applyCoupon(couponsInfo);
                                    }
                                }
                            });

                            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();

                        } else {
                            if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_cart")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent")) {
                                if (validateCouponCart(couponsInfo)){
                                    System.out.println("Coupan valid");
                                    applyCoupon(couponsInfo);
                                } else {
                                    System.out.println("Coupan not valid");
                                }

                            } else if (couponsInfo.getDiscountType().equalsIgnoreCase("fixed_product")
                                    || couponsInfo.getDiscountType().equalsIgnoreCase("percent_product")) {
                                if (validateCouponProduct(couponsInfo))
                                    applyCoupon(couponsInfo);
                            }
                        }

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        checkout_coupon_code.setError(response.body().getMessage());

                    } else {
                        // Unexpected Response from Server
                        Toast.makeText(getContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CouponsData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updatePayment(String payId){
        progressDialog.show();
        Call<OrderData> call = APIClient.getInstance().updatePaymentStatus(payId,paidOrderId);
        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, Response<OrderData> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Intent notificationIntent = new Intent(getContext(), MainActivity.class);
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Order has been placed Successfully
                    NotificationHelper.showNewNotification(getContext(), notificationIntent, getString(R.string.thank_you), response.body().getMessage(), null);
                    // Clear User's Cart
                    My_Cart.ClearCart();
                    // Clear User's Shipping and Billing info from AppContext
                    ((App) getContext().getApplicationContext()).setShippingAddress(new AddressDetails());
                    ((App) getContext().getApplicationContext()).setBillingAddress(new AddressDetails());

                    // Navigate to Thank_You Fragment
                    Fragment fragment = new Thank_You(my_cart);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment, fragment, "thank")
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Payment status not updated.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }
    //*********** Request the Server to Place User's Order ********//

    private void PlaceOrderNow(PostOrder postOrder) {
        String str = new Gson().toJson(postOrder);
        Call<OrderData> call = APIClient.getInstance().addToOrder(postOrder);
        call.enqueue(new Callback<OrderData>() {
            @Override
            public void onResponse(Call<OrderData> call, retrofit2.Response<OrderData> response) {
                progressDialog.dismiss();
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        System.out.println("Payment Method "+response.body().getData().get(0).getPaymentMethod());
                        if(response.body().getData().get(0).getPaymentMethod().equalsIgnoreCase("Online")){
                            System.out.println(response.body().getData().get(0).getOrdersId());
                            paidOrderId = response.body().getData().get(0).getOrdersId();
                            System.out.println(paidOrderId);
                            razorOrderId = response.body().getData().get(0).getOrdersStatus();
                            startPaymentRazorPay();
                        } else {
                            Intent notificationIntent = new Intent(getContext(), MainActivity.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            // Order has been placed Successfully
                            NotificationHelper.showNewNotification(getContext(), notificationIntent, getString(R.string.thank_you), response.body().getMessage(), null);
                            // Clear User's Cart
                            My_Cart.ClearCart();
                            // Clear User's Shipping and Billing info from AppContext
                            ((App) getContext().getApplicationContext()).setShippingAddress(new AddressDetails());
                            ((App) getContext().getApplicationContext()).setBillingAddress(new AddressDetails());

                            // Navigate to Thank_You Fragment
                            Fragment fragment = new Thank_You(my_cart);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.popBackStack(getString(R.string.actionHome), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentManager.beginTransaction()
                                    .add(R.id.main_fragment, fragment, "thank")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                    } else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    //*********** Apply given Coupon to checkout ********//

    public void applyCoupon(CouponsInfo coupon) {

        double discount = 0.0;
        System.out.println("Discount Applied "+discount);
        if (coupon.getDiscountType().equalsIgnoreCase("fixed_cart")) {
            discount = Double.parseDouble(coupon.getAmount());

        } else if (coupon.getDiscountType().equalsIgnoreCase("percent")) {
            discount = (checkoutSubtotal * Double.parseDouble(coupon.getAmount())) / 100;
            System.out.println("Discount Applied "+discount);
        } else if (coupon.getDiscountType().equalsIgnoreCase("fixed_product")) {

            for (int i = 0; i < checkoutItemsList.size(); i++) {

                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                int categoryID = Integer.parseInt(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());


                if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1") || !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                    if (!isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories()) || coupon.getExcludedProductCategories().size() == 0) {
                        if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds()) || coupon.getExcludeProductIds().size() == 0) {
                            if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories()) || coupon.getProductCategories().size() == 0) {
                                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds()) || coupon.getProductIds().size() == 0) {

                                    discount += (Double.parseDouble(coupon.getAmount()) * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                }
                            }
                        }
                    }
                }


            }

        } else if (coupon.getDiscountType().equalsIgnoreCase("percent_product")) {

            for (int i = 0; i < checkoutItemsList.size(); i++) {

                int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
                int categoryID = Integer.parseInt(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());


                if (!checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1") || !coupon.getExcludeSaleItems().equalsIgnoreCase("1")) {
                    if (!isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories()) || coupon.getExcludedProductCategories().size() == 0) {
                        if (!isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds()) || coupon.getExcludeProductIds().size() == 0) {
                            if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories()) || coupon.getProductCategories().size() == 0) {
                                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds()) || coupon.getProductIds().size() == 0) {

                                    double discountOnPrice = (Double.parseDouble(checkoutItemsList.get(i).getCustomersBasketProduct().getProductsFinalPrice()) * Double.parseDouble(coupon.getAmount())) / 100;
                                    discount += (discountOnPrice * checkoutItemsList.get(i).getCustomersBasketProduct().getCustomersBasketQuantity());
                                }
                            }
                        }
                    }
                }

            }
        }
        System.out.println("total amount "+discount);
        if ((checkoutDiscount + discount) >= getProductsSubTotal()) {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_be_applied));
        } else {
            if (coupon.getIndividualUse().equalsIgnoreCase("1")) {
                couponsList.clear();
                checkoutDiscount = 0.0;
                checkoutShipping = checkoutShippingCost;
                disableOtherCoupons = true;
                //setCheckoutTotal();
            }

                if (coupon.getFreeShipping().equalsIgnoreCase("1")) {
                    checkoutShipping = 0.0;
                }

                checkoutDiscount += discount;
                coupon.setDiscount(String.valueOf(discount));


                couponsList.add(coupon);
                checkout_coupon_code.setText("");
                couponsAdapter.notifyDataSetChanged();
                setCheckoutTotal();

        }

    }


    //*********** Remove given Coupon from checkout ********//

    public void removeCoupon(CouponsInfo coupon) {

        if (coupon.getIndividualUse().equalsIgnoreCase("1")) {
            disableOtherCoupons = false;
        }


        for (int i = 0; i < couponsList.size(); i++) {
            if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                couponsList.remove(i);
                couponsAdapter.notifyDataSetChanged();
            }
        }


        checkoutShipping = checkoutShippingCost;

        for (int i = 0; i < couponsList.size(); i++) {
            if (couponsList.get(i).getFreeShipping().equalsIgnoreCase("1")) {
                checkoutShipping = 0.0;
            }
        }


        double discount = Double.parseDouble(coupon.getDiscount());
        checkoutDiscount -= discount;

        checkoutDiscount -=getDiscountTotal();
        setCheckoutTotal();

    }


    //*********** Validate Cart type Coupon ********//

    private boolean validateCouponCart(CouponsInfo coupon) {

        int user_used_this_coupon_counter = 0;

        boolean coupon_already_applied = false;

        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = true;

        boolean valid_items_in_cart = false;
        boolean valid_category_items_in_cart = false;

        boolean no_excluded_item_in_cart = true;
        boolean no_excluded_category_item_in_cart = true;


        if (couponsList.size() != 0) {
            for (int i = 0; i < couponsList.size(); i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }


        for (int i = 0; i < coupon.getUsedBy().size(); i++) {
            if (userInfo.getId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }


        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getEmail(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        } else {
            valid_user_email_for_coupon = true;
        }




        for (int i = 0; i < checkoutItemsList.size(); i++) {

            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
          //  int categoryID = Integer.parseInt(checkoutItemsList.get(i).getCustomersBasketProduct().getCategoryIDs());
            int categoryID = 0;


            if (coupon.getExcludeSaleItems().equalsIgnoreCase("1") && checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = false;
            }


            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())) {
                    no_excluded_category_item_in_cart = false;
                }
            }

            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    no_excluded_item_in_cart = false;
                }
            }

            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())) {
                    valid_category_items_in_cart = true;
                }
            } else {
                valid_category_items_in_cart = true;
            }


            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    valid_items_in_cart = true;
                }
            } else {
                valid_items_in_cart = true;
            }

        }


        /////////////////////////////////////////////////////

        if (!disableOtherCoupons) {
            System.out.println("User counter "+coupon.getExpiryDate());
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    if (coupon.getUsageLimit() != null && coupon.getUsageLimitPerUser() != null) {
                        if (Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                            if (user_used_this_coupon_counter < Integer.parseInt(coupon.getUsageLimitPerUser())) {
                                System.out.println("User counter for this "+user_used_this_coupon_counter);
                                if (valid_user_email_for_coupon) {
                                    if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                        if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0 || checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                            if (valid_sale_items_in_for_coupon) {
                                                if (no_excluded_category_item_in_cart) {
                                                    if (no_excluded_item_in_cart) {
                                                        if (valid_category_items_in_cart) {
                                                            if (valid_items_in_cart) {

                                                                return true;

                                                            } else {
                                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                                return false;
                                                            }
                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                            return false;
                        }
                    } else {
                        showSnackBarForCoupon("Coupon Usage limit is Null");
                        return false;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }

    }


    //*********** Validate Product type Coupon ********//

    private boolean validateCouponProduct(CouponsInfo coupon) {

        int user_used_this_coupon_counter = 0;

        boolean coupon_already_applied = false;

        boolean valid_user_email_for_coupon = false;
        boolean valid_sale_items_in_for_coupon = false;

        boolean any_valid_item_in_cart = false;
        boolean any_valid_category_item_in_cart = false;

        boolean any_non_excluded_item_in_cart = false;
        boolean any_non_excluded_category_item_in_cart = false;


        if (couponsList.size() != 0) {
            for (int i = 0; i < couponsList.size(); i++) {
                if (coupon.getCode().equalsIgnoreCase(couponsList.get(i).getCode())) {
                    coupon_already_applied = true;
                }
            }
        }


        for (int i = 0; i < coupon.getUsedBy().size(); i++) {
            if (userInfo.getId().equalsIgnoreCase(coupon.getUsedBy().get(i))) {
                user_used_this_coupon_counter += 1;
            }
        }


        if (coupon.getEmailRestrictions().size() != 0) {
            if (isStringExistsInList(userInfo.getEmail(), coupon.getEmailRestrictions())) {
                valid_user_email_for_coupon = true;
            }
        } else {
            valid_user_email_for_coupon = true;
        }


        for (int i = 0; i < checkoutItemsList.size(); i++) {

            int productID = checkoutItemsList.get(i).getCustomersBasketProduct().getProductsId();
            int categoryID = checkoutItemsList.get(i).getCustomersBasketProduct().getLanguageId();


            if (!coupon.getExcludeSaleItems().equalsIgnoreCase("1") || !checkoutItemsList.get(i).getCustomersBasketProduct().getIsSaleProduct().equalsIgnoreCase("1")) {
                valid_sale_items_in_for_coupon = true;
            }


            if (coupon.getExcludedProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getExcludedProductCategories())) {
                    any_non_excluded_category_item_in_cart = true;
                }
            } else {
                any_non_excluded_category_item_in_cart = true;
            }

            if (coupon.getExcludeProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getExcludeProductIds())) {
                    any_non_excluded_item_in_cart = true;
                }
            } else {
                any_non_excluded_item_in_cart = true;
            }

            if (coupon.getProductCategories().size() != 0) {
                if (isStringExistsInList(String.valueOf(categoryID), coupon.getProductCategories())) {
                    any_valid_category_item_in_cart = true;
                }
            } else {
                any_valid_category_item_in_cart = true;
            }


            if (coupon.getProductIds().size() != 0) {
                if (isStringExistsInList(String.valueOf(productID), coupon.getProductIds())) {
                    any_valid_item_in_cart = true;
                }
            } else {
                any_valid_item_in_cart = true;
            }

        }


        /////////////////////////////////////////////////////

        if (!disableOtherCoupons) {
            if (!coupon_already_applied) {
                if (!Utilities.checkIsDatePassed(coupon.getExpiryDate())) {
                    if (coupon.getUsageLimit() != null && coupon.getUsageLimitPerUser() != null) {
                        if (Integer.parseInt(coupon.getUsageCount()) <= Integer.parseInt(coupon.getUsageLimit())) {
                            if (user_used_this_coupon_counter <= Integer.parseInt(coupon.getUsageLimitPerUser())) {
                                if (valid_user_email_for_coupon) {
                                    if (Double.parseDouble(coupon.getMinimumAmount()) <= checkoutTotal) {
                                        if (Double.parseDouble(coupon.getMaximumAmount()) == 0.0 || checkoutTotal <= Double.parseDouble(coupon.getMaximumAmount())) {
                                            if (valid_sale_items_in_for_coupon) {
                                                if (any_non_excluded_category_item_in_cart) {
                                                    if (any_non_excluded_item_in_cart) {
                                                        if (any_valid_category_item_in_cart) {
                                                            if (any_valid_item_in_cart) {

                                                                return true;

                                                            } else {
                                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_products));
                                                                return false;
                                                            }
                                                        } else {
                                                            showSnackBarForCoupon(getString(R.string.coupon_is_not_for_these_categories));
                                                            return false;
                                                        }
                                                    } else {
                                                        showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_products));
                                                        return false;
                                                    }
                                                } else {
                                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_excluded_categories));
                                                    return false;
                                                }
                                            } else {
                                                showSnackBarForCoupon(getString(R.string.coupon_is_not_for_sale_items));
                                                return false;
                                            }
                                        } else {
                                            showSnackBarForCoupon(getString(R.string.coupon_max_amount_is_less_than_order_total));
                                            return false;
                                        }
                                    } else {
                                        showSnackBarForCoupon(getString(R.string.coupon_min_amount_is_greater_than_order_total));
                                        return false;
                                    }
                                } else {
                                    showSnackBarForCoupon(getString(R.string.coupon_is_not_for_you));
                                    return false;
                                }
                            } else {
                                showSnackBarForCoupon(getString(R.string.coupon_used_by_you));
                                return false;
                            }
                        } else {
                            showSnackBarForCoupon(getString(R.string.coupon_used_by_all));
                            return false;
                        }
                    } else {
                        //showSnackBarForCoupon("Coupon Usage limit is Null");
                        return true;
                    }
                } else {
                    checkout_coupon_code.setError(getString(R.string.coupon_expired));
                    return false;
                }
            } else {
                showSnackBarForCoupon(getString(R.string.coupon_applied));
                return false;
            }
        } else {
            showSnackBarForCoupon(getString(R.string.coupon_cannot_used_with_existing));
            return false;
        }

    }


    //*********** Show SnackBar with given Message  ********//

    private void showSnackBarForCoupon(String msg) {
        final Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    //*********** Check if the given String exists in the given List ********//

    private boolean isStringExistsInList(String str, List<String> stringList) {
        boolean isExists = false;

        for (int i = 0; i < stringList.size(); i++) {
            if (stringList.get(i).equalsIgnoreCase(str)) {
                isExists = true;
            }
        }


        return isExists;
    }


    //*********** Setup Demo Coupons Dialog ********//

    private void setupDemoCoupons() {

        demo_coupons_text.setVisibility(View.VISIBLE);
        demo_coupons_text.setPaintFlags(demo_coupons_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        demo_coupons_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<CouponsInfo> couponsList = demoCouponsList();
                DemoCouponsListAdapter couponsListAdapter = new DemoCouponsListAdapter(getContext(), couponsList, CheckoutFinal.this);

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

                dialog_title.setText(getString(R.string.search) + " " + getString(R.string.coupon));
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(couponsListAdapter);

                demoCouponsDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        demoCouponsDialog.dismiss();
                    }
                });

                demoCouponsDialog.show();
            }
        });
    }


    //*********** Sets selected Coupon code from the Dialog ********//

    public void setCouponCode(String code) {
        checkout_coupon_code.setText(code);
        demoCouponsDialog.dismiss();
    }


    //*********** Demo Coupons List ********//

    private List<CouponsInfo> demoCouponsList() {
        List<CouponsInfo> couponsList = new ArrayList<>();

        CouponsInfo coupon1 = new CouponsInfo();
        coupon1.setCode("PercentProduct_10");
        coupon1.setAmount("10");
        coupon1.setDiscountType("Percent Product");
        coupon1.setDescription("For All Products");

        CouponsInfo coupon2 = new CouponsInfo();
        coupon2.setCode("FixedProduct_10");
        coupon2.setAmount("10");
        coupon2.setDiscountType("Fixed Product");
        coupon2.setDescription("For All Products");

        CouponsInfo coupon3 = new CouponsInfo();
        coupon3.setCode("PercentCart_10");
        coupon3.setAmount("10");
        coupon3.setDiscountType("Percent Cart");
        coupon3.setDescription("For All Products");

        CouponsInfo coupon4 = new CouponsInfo();
        coupon4.setCode("FixedCart_10");
        coupon4.setAmount("10");
        coupon4.setDiscountType("Fixed Cart");
        coupon4.setDescription("For All Products");

        CouponsInfo coupon5 = new CouponsInfo();
        coupon5.setCode("SingleCoupon_50");
        coupon5.setAmount("50");
        coupon5.setDiscountType("Fixed Cart");
        coupon5.setDescription("Individual Use");

        CouponsInfo coupon6 = new CouponsInfo();
        coupon6.setCode("FreeShipping_20");
        coupon6.setAmount("20");
        coupon6.setDiscountType("Fixed Cart");
        coupon6.setDescription("Free Shipping");

        CouponsInfo coupon7 = new CouponsInfo();
        coupon7.setCode("ExcludeSale_15");
        coupon7.setAmount("15");
        coupon7.setDiscountType("Fixed Cart");
        coupon7.setDescription("Not for Sale Items");

        CouponsInfo coupon8 = new CouponsInfo();
        coupon8.setCode("Exclude_Shoes_25");
        coupon8.setAmount("25");
        coupon8.setDiscountType("Fixed Cart");
        coupon8.setDescription("Not For Men Shoes");

        CouponsInfo coupon9 = new CouponsInfo();
        coupon9.setCode("Polo_Shirts_10");
        coupon9.setAmount("10");
        coupon9.setDiscountType("Percent Product");
        coupon9.setDescription("For Men Polo Shirts");

        CouponsInfo coupon10 = new CouponsInfo();
        coupon10.setCode("Jeans_10");
        coupon10.setAmount("10");
        coupon10.setDiscountType("Percent Cart");
        coupon10.setDescription("For Men Jeans");


        couponsList.add(coupon1);
        couponsList.add(coupon2);
        couponsList.add(coupon3);
        couponsList.add(coupon4);
        couponsList.add(coupon5);
        couponsList.add(coupon6);
        couponsList.add(coupon7);
        couponsList.add(coupon8);
        couponsList.add(coupon9);
        couponsList.add(coupon10);

        return couponsList;
    }

    //*********** Validate Payment Card Inputs ********//

    private boolean validatePaymentCard() {
        if (!ValidateInputs.isValidNumber(checkout_card_number.getText().toString().trim())) {
           // checkout_card_number.setError(getString(R.string.invalid_credit_card));
            alertError(getString(R.string.invalid_credit_card));
            return false;
        } else if (!ValidateInputs.isValidNumber(checkout_card_cvv.getText().toString().trim())) {
          //  checkout_card_cvv.setError(getString(R.string.invalid_card_cvv));
            alertError(getString(R.string.invalid_card_cvv));
            return false;
        } else if (TextUtils.isEmpty(checkout_card_expiry.getText().toString().trim())) {
            //checkout_card_expiry.setError(getString(R.string.select_card_expiry));
           // Toast.makeText(getContext(), getString(R.string.select_card_expiry), Toast.LENGTH_SHORT).show();
            alertError(getString(R.string.select_card_expiry));
            return false;
        } else {
            return true;
        }
    }

    //*********** Validate Payment Info Inputs ********//

    private boolean validatePaymentInfo() {
        if (!ValidateInputs.isValidName(payment_name.getText().toString().trim())) {
            payment_name.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidEmail(payment_email.getText().toString().trim())) {
            payment_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidPhoneNo(payment_phone.getText().toString().trim())) {
            payment_phone.setError(getString(R.string.invalid_contact));
            return false;
        } else {
            return true;
        }
    }

    void alertError(String message)
    {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}

