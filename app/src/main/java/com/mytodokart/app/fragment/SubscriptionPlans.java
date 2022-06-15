package com.mytodokart.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mytodokart.app.R;
import com.mytodokart.app.activities.MainActivity;
import com.mytodokart.app.adapters.SubscriptionPlansAdapter;
import com.mytodokart.app.app.App;
import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.customs.DialogLoader;
import com.mytodokart.app.databases.User_Info_DB;
import com.mytodokart.app.models.ProductSubscription;
import com.mytodokart.app.models.SubscriptionHistory;
import com.mytodokart.app.models.Subscription_history_data;
import com.mytodokart.app.models.user_model.UserDetails;
import com.mytodokart.app.network.APIClient;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;

public class SubscriptionPlans extends Fragment {

    View rootView;
    RecyclerView subscriptionPlansRV;
    SubscriptionPlansAdapter adapter;
    LinearLayoutManager HorizontalLayout;
    int planId;
    Call<SubscriptionHistory> networkCall;
    UserDetails userInfo;
    User_Info_DB user_info_db = new User_Info_DB();
    ArrayList<ProductSubscription> SubscriptionHistoryList = new ArrayList<>();
    DialogLoader dialogLoader;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.f_subscription_plans, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.actionAccount));
        dialogLoader = new DialogLoader(getContext());
        subscriptionPlansRV = rootView.findViewById(R.id.subscriptionPlansRV);
        getSubscriptionPlans();
        return rootView;
    }

    public void getSubscriptionPlans() {

        userInfo = user_info_db.getUserData(getActivity().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", null));
        Subscription_history_data data = new Subscription_history_data();
        data.setCustomers_id(userInfo.getId());
        dialogLoader.showProgressDialog();
        networkCall = APIClient.getInstance().getSubscriptionHistory(data);
        networkCall.enqueue(new Callback<SubscriptionHistory>() {
            @Override
            public void onResponse(Call<SubscriptionHistory> call, retrofit2.Response<SubscriptionHistory> response) {
                System.out.println("Res "+response);
                if (response.isSuccessful()) {
                    if (response.body().success.equalsIgnoreCase("1")) {
                        SubscriptionHistoryList = response.body().data1.data;
                        adapter = new SubscriptionPlansAdapter(getActivity(),SubscriptionHistoryList,false,SubscriptionPlans.this);
                        HorizontalLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        subscriptionPlansRV.setLayoutManager(HorizontalLayout);
                        subscriptionPlansRV.setAdapter(adapter);
                    }
                    else if (response.body().success.equalsIgnoreCase("0")) {

                    }
                }

                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<SubscriptionHistory> call, Throwable t) {
                if (!networkCall.isCanceled()) {
                    Toast.makeText(App.getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                }

                dialogLoader.hideProgressDialog();
            }
        });
    }

    public void onClickPurchasePlan(String inAppProductId, int id) {
        this.planId = id;
        ((MainActivity)getActivity()).initPurchase(ConstantValues.base64key, false, inAppProductId);
    }


}
