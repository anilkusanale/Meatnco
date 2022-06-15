package com.mytodokart.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class Wallet extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String userCurrentMobile;
    String userCouponCode;
    DialogLoader dialogLoader;
    SharedPreferences.Editor editor;
    private TextView walletTotal;
    private ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        walletTotal = (TextView) findViewById(R.id.walletTotal);
        back_btn = (ImageView) findViewById(R.id.back_btn);

        dialogLoader = new DialogLoader(Wallet.this);
        sharedPreferences = getContext().getSharedPreferences("UserInfo", MODE_PRIVATE);

        User_Info_DB userInfoDB = new User_Info_DB();
        UserDetails userInfo = userInfoDB.getUserData(sharedPreferences.getString("userID", null));
        userCurrentMobile = userInfo.getPhone();
        getUserDataPhoneLogin();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                    Toast.makeText(Wallet.this,"something went wrong",Toast.LENGTH_SHORT).show();
                    dialogLoader.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Toast.makeText(Wallet.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                dialogLoader.hideProgressDialog();
            }
        });


    }

    public void  updateInfo(UserDetails userDetails){
        userCouponCode = userDetails.getUniqueId().toString();
        walletTotal.setText(userDetails.getWalletAmount().toString());
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