package com.mytodokart.app.activities;

import android.app.Activity;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.mytodokart.app.R;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import am.appwise.components.ni.NoInternetDialog;

import com.mytodokart.app.constant.ConstantValues;
import com.mytodokart.app.customs.DialogLoader;
import com.mytodokart.app.models.NumberLoginResponse;
import com.mytodokart.app.models.user_model.UserData;
import com.mytodokart.app.models.user_model.UserDetails;
import com.mytodokart.app.network.APIClient;
import com.mytodokart.app.network.StartAppRequests;
import com.mytodokart.app.app.MyAppPrefsManager;
import com.mytodokart.app.utils.LocaleHelper;
import com.mytodokart.app.utils.ValidateInputs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.mytodokart.app.databases.User_Info_DB;

/**
 * Login activity handles User's Email, Facebook and Google Login
 **/


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    View parentView;
    Toolbar toolbar;
    ActionBar actionBar;

    EditText user_email, user_password , phone_number;
    TextView forgotPasswordText, signupText;
    Button loginBtn, facebookLoginBtn, googleLoginBtn, phoneLoginBtn;
    LinearLayout layoutEmailLogin;

    User_Info_DB userInfoDB;
    DialogLoader dialogLoader;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;


    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;

    private static final int RC_SIGN_IN = 100;


    private UserDetails userDetails,userD;
    private Dialog dialogOTP;
    private FirebaseAuth mAuth;
    EditText ed_otp;
    private int TEMP_USER_TYPE;
    private static String user_current_phone_number;
    String otpCode = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ConstantValues.THEME_ID);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(Login.this).build();
        // noInternetDialog.show();
        mAuth = FirebaseAuth.getInstance();


        // Initializing Google SDK for Google Login
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();


        setContentView(R.layout.login);


        // setting Toolbar
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.login));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Binding Layout Views
        user_email = (EditText) findViewById(R.id.user_email);
        user_password = (EditText) findViewById(R.id.user_password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        facebookLoginBtn = (Button) findViewById(R.id.facebookLoginBtn);
        googleLoginBtn = (Button) findViewById(R.id.googleLoginBtn);
        phoneLoginBtn = (Button) findViewById(R.id.phoneLoginBtn);
        signupText = (TextView) findViewById(R.id.login_signupText);
        forgotPasswordText = (TextView) findViewById(R.id.forgot_password_text);
        layoutEmailLogin = (LinearLayout) findViewById(R.id.layout_email_login);
        phone_number = (EditText) findViewById(R.id.edittextPhone);

        parentView = signupText;


        if (ConstantValues.IS_GOOGLE_LOGIN_ENABLED) {
            googleLoginBtn.setVisibility(View.VISIBLE);
        } else {
            googleLoginBtn.setVisibility(View.GONE);
        }

        if (ConstantValues.IS_FACEBOOK_LOGIN_ENABLED) {
            facebookLoginBtn.setVisibility(View.VISIBLE);
        } else {
            facebookLoginBtn.setVisibility(View.GONE);
        }

        if (ConstantValues.IS_PHONE_LOGIN_ENABLED) {
            phoneLoginBtn.setVisibility(View.GONE);
        } else {
          //  phoneLoginBtn.setVisibility(View.VISIBLE);
        }

        if (ConstantValues.IS_EMAIL_LOGIN_ENABLED) {
            layoutEmailLogin.setVisibility(View.VISIBLE);
        } else {
            layoutEmailLogin.setVisibility(View.GONE);
        }

        dialogLoader = new DialogLoader(Login.this);

        userInfoDB = new User_Info_DB();
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);


        user_email.setText(sharedPreferences.getString("userEmail", null));


        // Initializing Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(Login.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();


        // Handle on Forgot Password Click
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this, R.style.DialogFullscreen);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_input, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);

                final Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                final EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                final TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                final ImageView dismiss_button = (ImageView) dialogView.findViewById(R.id.dismissButton);

                dialog_button.setText(getString(R.string.send));
                dialog_title.setText(getString(R.string.forgot_your_password));

                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                dismiss_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ValidateInputs.isValidEmail(dialog_input.getText().toString().trim())) {
                            // Request for Password Reset
                            processForgotPassword(dialog_input.getText().toString());

                        } else {
                            Snackbar.make(parentView, getString(R.string.invalid_email), Snackbar.LENGTH_LONG).show();
                        }

                        alertDialog.dismiss();
                    }
                });
            }
        });


        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignUp Activity
                startActivity(new Intent(Login.this, Signup.class));
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate Login Form Inputs
                boolean isValidData = validateLogin();

                if (isValidData) {
                    hideKeyboardFrom(Login.this);
                    processLogin();

                }
            }
        });




        // Handle Google Login Button click
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout the User if already Logged-in
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }

                // Get the Google SignIn Intent
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

                // Start Activity with Google SignIn Intent
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        phoneLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (phone_number.getVisibility()==View.GONE) {
                    phone_number.setVisibility(View.VISIBLE);
                }
                else {
                    //showPhoneDialog();
                    if (ValidateInputs.isValidPhoneNo(phone_number.getText().toString().trim())) {

                        checkUserPhoneNumber(phone_number.getText().toString().trim());

                    } else {
                        Snackbar.make(parentView, getString(R.string.invalid_contact), Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    void checkUserPhoneNumber(final String number)
    {
        dialogLoader.showProgressDialog();
        Call<NumberLoginResponse> call = APIClient.getInstance().processPhoneLogin(number);

        call.enqueue(new Callback<NumberLoginResponse>() {
            @Override
            public void onResponse(Call<NumberLoginResponse> call, Response<NumberLoginResponse> response) {
                if (response.isSuccessful())
                {
                    NumberLoginResponse loginResponse=response.body();
                    System.out.println(loginResponse.getMessage());
                    if (loginResponse.getMessage().equals("user exist"))
                    {
                        System.out.println(loginResponse.getData());
                        otpCode = loginResponse.getData().toString();
                        getUserDataPhoneLogin(number);
//                        user_current_phone_number = getResources().getString(R.string.indian_code) +number;
//                        sendOTP(user_current_phone_number);
                    }
                    else
                    {
                        Toast.makeText(Login.this,"Please signup to continue",Toast.LENGTH_SHORT).show();
                        dialogLoader.hideProgressDialog();
                        startActivity(new Intent(Login.this, Signup.class));
                        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                    }
                }
            }

            @Override
            public void onFailure(Call<NumberLoginResponse> call, Throwable t) {
                Toast.makeText(Login.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                dialogLoader.hideProgressDialog();
            }
        });

    }

    private void getUserDataPhoneLogin(final String number) {

        Call<UserData> call = APIClient.getInstance().getUserDataPhoneLogin(number);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful())
                {
                    dialogLoader.hideProgressDialog();
                    user_current_phone_number = number;
                    userD = response.body().getData().get(0);
                    sendOTP(user_current_phone_number);
                }
                else
                {
                    Toast.makeText(Login.this,"something went wrong",Toast.LENGTH_SHORT).show();
                    dialogLoader.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Toast.makeText(Login.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                dialogLoader.hideProgressDialog();
            }
        });


    }

    //*********** Called if Connection fails for Google Login ********//

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // If Connection fails for GoogleApiClient
    }


    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Handle Activity Result
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }

    }


    //*********** Get Google Account Details from GoogleSignInResult ********//

    private void handleGoogleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            // Proceed Google Registration
            processGoogleRegistration(acct);

        } else {
            // If Login Fails
            Toast.makeText(this, "Login Failed  " + result.getStatus().getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }


    //*********** Proceed Login with User Email and Password ********//

    private void processLogin() {

        dialogLoader.showProgressDialog();

        Call<UserData> call = APIClient.getInstance().processLogin(user_email.getText().toString().trim(),
                                user_password.getText().toString().trim());

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1") || response.body().getSuccess().equalsIgnoreCase("2")) {
                        // Get the User Details from Response
                        userDetails = response.body().getData().get(0);
                        TEMP_USER_TYPE = 0; // 0 for Simple Login.
                        //showPhoneDialog();
                        loginUser(userDetails);
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    Toast.makeText(Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(Login.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPhoneDialog() {
       // AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this, android.R.style.Theme_Material_Light_NoActionBar);
        AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_phone, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        final Button okayButton = dialogView.findViewById(R.id.dialog_button);
        final EditText phoneEditText = dialogView.findViewById(R.id.dialog_input);
        if (!phoneEditText.getText().toString().isEmpty()) {
            phoneEditText.setText(phoneEditText.getText().toString());
        }

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidateInputs.isValidPhoneNo(phoneEditText.getText().toString().trim())) {

                    checkUserPhoneNumber(phoneEditText.getText().toString().trim());
                    // Request for OTP
//                     user_current_phone_number = getResources().getString(R.string.indian_code) + phoneEditText.getText().toString().trim();
//                    //sendOTP(phoneEditText.getText().toString().trim());
//                      sendOTP(user_current_phone_number);

                } else {
                    Snackbar.make(parentView, getString(R.string.invalid_contact), Snackbar.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            }
        });
    }

    private void sendOTP(String phoneNumber) {
        dialogLoader.hideProgressDialog();
        showOTPDialog(phoneNumber);
    }

    private void showOTPDialog(final String phoneNumber) {
        dialogOTP = new Dialog(Login.this);
        dialogOTP.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOTP.setCancelable(false);
        dialogOTP.setContentView(R.layout.dialog_otp);

        ed_otp = dialogOTP.findViewById(R.id.ed_otp);
        AppCompatButton btn_resend, btn_submit;
        ImageView closeBtn = (ImageView) dialogOTP.findViewById(R.id.closeBtn);
        btn_resend = dialogOTP.findViewById(R.id.btn_resend);
        btn_submit = dialogOTP.findViewById(R.id.btn_submit);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendOTP(phoneNumber);
                dialogOTP.dismiss();
            }
        });

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendOTP(phoneNumber);
                checkUserPhoneNumber(phoneNumber);
                dialogOTP.dismiss();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verifyVerificationCode(ed_otp.getText().toString().trim());
                String eOtp = ed_otp.getText().toString().trim();
                if(eOtp.equalsIgnoreCase(otpCode)){
                    dialogOTP.dismiss();
                    loginUser(userD);
                } else {
                   Toast msg = Toast.makeText(Login.this, "Invalid code.", Toast.LENGTH_SHORT);
                   msg.setGravity(Gravity.CENTER,0,0);
                   msg.show();
                }
            }
        });
        dialogOTP.show();
    }

    private String mVerificationId;
    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                ed_otp.setText(code);
                verifyVerificationCode(code);
            } else {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        if (code.isEmpty()) {
            Toast.makeText(this, "Invalid code.", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            dialogOTP.dismiss();
                            //Final Proceed to login
/*
                            suserDetails = response.body().getData().get(0);
                        TEMP_USER_TYPE = 0; // 0 for Simple Login.
                        //showPhoneDialog();
                        loginUser(userDetails);witch (TEMP_USER_TYPE) {
                                case 0: // simple Login
                                    loginUser(userDetails);
                                    break;
                                case 1: // Gmail Login
                                    loginGmailUser(userDetails);
                                    break;
                                case 2: // Facebook Login
                                    loginFacebookUser(userDetails);
                                    break;
                            }
*/
//                            Intent intent=new Intent(Login.this,MainActivity.class);
//                            startActivity(intent);

                            TEMP_USER_TYPE = 0; // 0 for Simple Login.
                            //showPhoneDialog();
                            loginUser(userD);


                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginUser(UserDetails userDetails) {
        // Save User Data to Local Databases
        if (userInfoDB.getUserData(userDetails.getId()) != null) {
            // User already exists
            userInfoDB.updateUserData(userDetails);
        } else {
            // Insert Details of New User
            userInfoDB.insertUserData(userDetails);
        }

        // Save necessary details in SharedPrefs
        editor = sharedPreferences.edit();
        editor.putString("userID", userDetails.getId());
        editor.putString("userUniqueID", userDetails.getUniqueId());
        editor.putString("userEmail", userDetails.getEmail());
        editor.putString("userName", userDetails.getFirstName() + " " + userDetails.getLastName());
        editor.putString("userTelephone", userDetails.getPhone());
        editor.putString("userDefaultAddressID", userDetails.getDefaultAddressId());
        editor.putBoolean("isLogged_in", true);
        editor.commit();

        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(Login.this);
        myAppPrefsManager.setUserLoggedIn(true);

        // Set isLogged_in of ConstantValues
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
        StartAppRequests.RegisterDeviceForFCM(Login.this);
        // Navigate back to MainActivity
        Intent i = new Intent(Login.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }

    private void loginGmailUser(UserDetails userDetails) {
        // Save User Data to Local Databases
        if (userInfoDB.getUserData(userDetails.getId()) != null) {
            // User already exists
            userInfoDB.updateUserData(userDetails);
        } else {
            // Insert Details of New User
            userInfoDB.insertUserData(userDetails);
        }

        // Save necessary details in SharedPrefs
        editor = sharedPreferences.edit();
        editor.putString("userID", userDetails.getId());
        editor.putString("userEmail", userDetails.getEmail());
        editor.putString("userName", userDetails.getFirstName() + " " + userDetails.getLastName());
        editor.putString("userTelephone", user_current_phone_number);
        editor.putString("userDefaultAddressID", userDetails.getDefaultAddressId());
        editor.putBoolean("isLogged_in", true);
        editor.commit();

        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(Login.this);
        myAppPrefsManager.setUserLoggedIn(true);

        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();

        StartAppRequests.RegisterDeviceForFCM(Login.this);


        // Navigate back to MainActivity
        startActivity(new Intent(Login.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }

    private void loginFacebookUser(UserDetails userDetails) {
        // Save User Data to Local Databases
        if (userInfoDB.getUserData(userDetails.getId()) != null) {
            // User already exists
            userInfoDB.updateUserData(userDetails);
        } else {
            // Insert Details of New User
            userInfoDB.insertUserData(userDetails);
        }

        // Save necessary details in SharedPrefs
        editor = sharedPreferences.edit();
        editor.putString("userID", userDetails.getId());
        editor.putString("userEmail", userDetails.getEmail());
        editor.putString("userName", userDetails.getFirstName() + " " + userDetails.getLastName());
        editor.putString("userTelephone", user_current_phone_number);
        editor.putString("userDefaultAddressID", userDetails.getDefaultAddressId());
        editor.putBoolean("isLogged_in", true);
        editor.commit();
        // Set UserLoggedIn in MyAppPrefsManager
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(Login.this);
        myAppPrefsManager.setUserLoggedIn(true);

        // Set isLogged_in of ConstantValues
        ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();

        StartAppRequests.RegisterDeviceForFCM(Login.this);

        // Navigate back to MainActivity
        Intent i = new Intent(Login.this, MainActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }

    //*********** Proceed Forgot Password Request ********//

    private void processForgotPassword(String email) {

        dialogLoader.showProgressDialog();

        Call<UserData> call = APIClient.getInstance()
                .processForgotPassword
                        (
                                email
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    // Show the Response Message
                    String message = response.body().getMessage();
                    Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show();

                } else {
                    // Show the Error Message
                    Toast.makeText(Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(Login.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    //*********** Proceed Facebook Registration Request ********//

    private void processFacebookRegistration(String access_token) {

        dialogLoader.showProgressDialog();
        Log.i("VC_Shop", "[FacebookRegistration] > access_token" + access_token);


        Call<UserData> call = APIClient.getInstance()
                .facebookRegistration
                        (
                                access_token
                        );

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1") || response.body().getSuccess().equalsIgnoreCase("2")) {
                        // Get the User Details from Response
                        userDetails = response.body().getData().get(0);
                        TEMP_USER_TYPE = 2; // 2 for Facebook Login.
                        //showPhoneDialog();
                        loginFacebookUser(userDetails);
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Get the Error Message from Response
                        String message = response.body().getMessage();
                        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Show the Error Message
                    Toast.makeText(Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(Login.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    //*********** Proceed Google Registration Request ********//

    private void processGoogleRegistration(GoogleSignInAccount account) {

        dialogLoader.showProgressDialog();

        String accountStr = account.getIdToken();
        String accountID = account.getId();
        String name = account.getGivenName();
        String familyName = account.getFamilyName();
        String email = account.getEmail();

        String photoURL = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

        Call<UserData> call = APIClient.getInstance()
                .googleRegistration
                        (
                                account.getIdToken(),
                                account.getId(),
                                account.getGivenName(),
                                account.getFamilyName(),
                                account.getEmail(),
                                photoURL
                        );


        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                String gson = response.raw().body().toString();

                dialogLoader.hideProgressDialog();

                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1") || response.body().getSuccess().equalsIgnoreCase("2")) {

                        String gsonStr = new Gson().toJson(response.body().getData().get(0));
                        userDetails = response.body().getData().get(0);
                        TEMP_USER_TYPE = 1; // 1 for Gmail login.
                        //showPhoneDialog();
                        loginGmailUser(userDetails);
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(parentView, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Login.this, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Show the Error Message
                    Toast.makeText(Login.this, response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(Login.this, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    //*********** Validate Login Form Inputs ********//

    private boolean validateLogin() {
        if (!ValidateInputs.isValidEmail(user_email.getText().toString().trim())) {
            user_email.setError(getString(R.string.invalid_email));
            return false;
        }
        //else if (!ValidateInputs.isValidPassword(user_password.getText().toString().trim())) {
        else if (TextUtils.isEmpty(user_password.getText().toString().trim())) {
            user_password.setError(getString(R.string.invalid_password));
            return false;
        } else {
            return true;
        }
    }


    //*********** Set the Base Context for the ContextWrapper ********//

    @Override
    protected void attachBaseContext(Context newBase) {

        String languageCode = ConstantValues.LANGUAGE_CODE;
        if ("".equalsIgnoreCase(languageCode))
            languageCode = ConstantValues.LANGUAGE_CODE = "en";

        super.attachBaseContext(LocaleHelper.wrapLocale(newBase, languageCode));
    }


    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {

        // Navigate back to MainActivity
        startActivity(new Intent(Login.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(user_email.getWindowToken(), 0);
    }

}

