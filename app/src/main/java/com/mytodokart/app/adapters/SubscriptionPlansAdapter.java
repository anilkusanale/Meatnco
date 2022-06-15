package com.mytodokart.app.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.mytodokart.app.R;
import com.mytodokart.app.databases.User_Info_DB;
import com.mytodokart.app.fragment.SubscriptionPlans;
import com.mytodokart.app.models.CancelSubscriptionData;
import com.mytodokart.app.models.ProductSubscription;
import com.mytodokart.app.models.SubscriptionHistory;
import com.mytodokart.app.models.user_model.UserDetails;
import com.mytodokart.app.network.APIClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import static com.mytodokart.app.app.App.getContext;

public class SubscriptionPlansAdapter extends RecyclerView.Adapter<SubscriptionPlansAdapter.MyViewHolder> {

    LayoutInflater inflater;
    Activity activity;
    Call<SubscriptionHistory> networkCall;
    List<ProductSubscription> subscriptionDataList;
    SubscriptionPlans subscriptionPlans;
    UserDetails userInfo;
    AlertDialog dialog;
    User_Info_DB user_info_db = new User_Info_DB();
    boolean isSubCategory;
    private int mYear,mMonth,mDay;

    public SubscriptionPlansAdapter(Activity activity, List<ProductSubscription> subscriptionDataList, boolean isSubCategory, SubscriptionPlans subscriptionPlans) {
        this.activity = activity;
        this.subscriptionPlans = subscriptionPlans;
        this.subscriptionDataList = subscriptionDataList;
        this.isSubCategory = isSubCategory;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public SubscriptionPlansAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View binding = inflater.inflate(R.layout.adapter_subscription_plan, parent, false);
        return new SubscriptionPlansAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionPlansAdapter.MyViewHolder holder, final int position) {
        userInfo = user_info_db.getUserData(activity.getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", null));
        holder.planTypeTV.setText("Subscription : "+subscriptionDataList.get(position).subs_freq);
        holder.productNameTV.setText(subscriptionDataList.get(position).products_name);
        holder.productPriceTV.setText("Price : "+subscriptionDataList.get(position).products_price);
        holder.planStatusTV.setText(subscriptionDataList.get(position).subscription_status);
        if(subscriptionDataList.get(position).subscription_status.equalsIgnoreCase("active")){
            holder.cancelBT.setVisibility(View.VISIBLE);
            holder.pauseBT.setVisibility(View.VISIBLE);
            holder.resumeBT.setVisibility(View.GONE);
        }
        if(subscriptionDataList.get(position).subscription_status.equalsIgnoreCase("paused")){
            holder.cancelBT.setVisibility(View.VISIBLE);
            holder.pauseBT.setVisibility(View.GONE);
            holder.resumeBT.setVisibility(View.VISIBLE);
        }
        if(subscriptionDataList.get(position).subscription_status.equalsIgnoreCase("cancelled")){
            holder.cancelBT.setVisibility(View.GONE);
            holder.pauseBT.setVisibility(View.GONE);
            holder.resumeBT.setVisibility(View.GONE);
        }
          holder.cancelBT.setTag(position);
          holder.pauseBT.setTag(position);
          holder.resumeBT.setTag(position);
          holder.cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                cancelReasonDialog(pos,"Cancel");
            }
        });

        holder.pauseBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                cancelReasonDialog(pos,"Pause");
            }
        });

        holder.resumeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                cancelReasonDialog(pos,"Resume");
            }
        });
    }

    private void cancelReasonDialog(final int pos, final String status) {
        // final EditText inputEditTextField= new EditText(activity);
       final View cv = inflater.inflate(R.layout.sub_cancel_layout,null, false);
        final EditText inputEditTextField= cv.findViewById(R.id.reason);
        final EditText fromDate= cv.findViewById(R.id.fromDate);
        final EditText toDate= cv.findViewById(R.id.toDate);
        final Button subBtn= cv.findViewById(R.id.submitBtn);
        fromDate.setVisibility(View.GONE);
        toDate.setVisibility(View.GONE);
        if(status.equalsIgnoreCase("Pause")){
            fromDate.setVisibility(View.VISIBLE);
            toDate.setVisibility(View.VISIBLE);
        }

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editTextInput = inputEditTextField.getText().toString();
                String fdate = fromDate.getText().toString();
                String tdate = toDate.getText().toString();
                boolean valid = true;
                if(editTextInput.isEmpty()) {
                    Toast.makeText(activity,"Enter reason",Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if(status.equalsIgnoreCase("Pause")){
                    if(fdate.isEmpty()){
                        Toast.makeText(activity,"Enter From Date",Toast.LENGTH_SHORT).show();
                        valid = false;
                    }
                    if(tdate.isEmpty()){
                        Toast.makeText(activity,"Enter To Date",Toast.LENGTH_SHORT).show();
                        valid = false;
                    }
                }
                if(valid) {
                    cancelSubscription(editTextInput,pos,status,fdate,tdate);
                }
            }
        });


        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        fromDate.setText(sdf.format(myCalendar.getTime()));

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                //mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDatePicker.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        toDate.setText(sdf.format(myCalendar.getTime()));

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                //mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mDatePicker.show();
            }
        });

         dialog = new AlertDialog.Builder(activity)
                .setTitle(status+" Subscription")
              //  .setMessage("Enter reason why you want to "+status+" the subscription?")
                .setView(cv)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
//                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void cancelSubscription(String editTextInput,int pos,String status,String fdate,String tdate) {
        final CancelSubscriptionData data = new CancelSubscriptionData();
        data.setCustomers_id(userInfo.getId());
        data.setCancel_reason(editTextInput);
        data.setStatus(status);
        data.setFromDate(fdate);
        data.setToDate(tdate);
        data.setSubscription_id(subscriptionDataList.get(pos).orders_products_id);
        networkCall = APIClient.getInstance().cancelSubscription(data);
        networkCall.enqueue(new Callback<SubscriptionHistory>() {
            @Override
            public void onResponse(Call<SubscriptionHistory> call, retrofit2.Response<SubscriptionHistory> response) {
                if (response.isSuccessful()) {
                    if (response.body().success.equalsIgnoreCase("1")) {
                        dialog.dismiss();
                        Toast.makeText(activity,"Status Updated",Toast.LENGTH_SHORT).show();
                        subscriptionPlans.getSubscriptionPlans();
                    }
                    else if (response.body().success.equalsIgnoreCase("0")) {

                    }
                }
            }

            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                if (!networkCall.isCanceled()) {
                    Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView planTypeTV,productPriceTV,productNameTV,planStatusTV;
        private Button cancelBT,pauseBT,resumeBT;

        public MyViewHolder(View item) {
            super(item);
            planTypeTV = item.findViewById(R.id.planTypeTV);
            productNameTV = item.findViewById(R.id.productNameTV);
            productPriceTV = item.findViewById(R.id.productPriceTV);
            planStatusTV = item.findViewById(R.id.planStatusTV);
            cancelBT = item.findViewById(R.id.cancelBT);
            pauseBT = item.findViewById(R.id.pauseBT);
            resumeBT = item.findViewById(R.id.resumeBT);
        }
    }
}
