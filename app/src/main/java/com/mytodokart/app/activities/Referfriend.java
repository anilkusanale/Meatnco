package com.mytodokart.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mytodokart.app.R;
import com.mytodokart.app.customs.DialogLoader;
import com.mytodokart.app.databases.User_Info_DB;
import com.mytodokart.app.models.user_model.UserData;
import com.mytodokart.app.models.user_model.UserDetails;
import com.mytodokart.app.network.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mytodokart.app.app.App.getContext;

public class Referfriend extends AppCompatActivity {

    private TextView coupan_code;
    private Button refer_btn;
    SharedPreferences sharedPreferences;
    String userCurrentMobile;
    String userCouponCode;
    DialogLoader dialogLoader;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_referfriend);
        coupan_code = (TextView) findViewById(R.id.coupan_code);
        refer_btn = (Button) findViewById(R.id.refer_btn);
        dialogLoader = new DialogLoader(Referfriend.this);
        sharedPreferences = getContext().getSharedPreferences("UserInfo", MODE_PRIVATE);

        User_Info_DB userInfoDB = new User_Info_DB();
        UserDetails userInfo = userInfoDB.getUserData(sharedPreferences.getString("userID", null));
        userCurrentMobile = userInfo.getPhone();
        getUserDataPhoneLogin();

        refer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                String shareBody = "Hey, Get fresh customised meat & Seafood home delivered by meatnco.in. Use my coupon code ";
                shareBody +=userCouponCode+" & get Rs.100 off on your first order. \n";
                shareBody +="Download app: https://play.google.com/store/apps/details?id=in.meatnco.app";
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Meatn Co");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, null));
            }
        });
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
                    updateInfo(response.body().getData().get(0));
                }
                else
                {
                    Toast.makeText(Referfriend.this,"something went wrong",Toast.LENGTH_SHORT).show();
                    dialogLoader.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Toast.makeText(Referfriend.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                dialogLoader.hideProgressDialog();
            }
        });


    }

    public void  updateInfo(UserDetails userDetails){
        userCouponCode = userDetails.getUniqueId().toString();
        coupan_code.setText(userDetails.getUniqueId().toString());
        editor = sharedPreferences.edit();
        editor.putString("userID", userDetails.getId());
        editor.putString("userUniqueID", userDetails.getUniqueId());
        editor.putString("userEmail", userDetails.getEmail());
        editor.putString("userName", userDetails.getFirstName() + " " + userDetails.getLastName());
        editor.putString("userTelephone", userDetails.getPhone());
        editor.putString("userDefaultAddressID", userDetails.getDefaultAddressId());
        editor.putBoolean("isLogged_in", true);
        editor.commit();
    }
}