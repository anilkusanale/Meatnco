package com.toxsl.purchaseutils;

import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class FuelPurchaseActivity extends AppCompatActivity {

    static final String TAG = "ChatIncognito";
    boolean mSubscribedToInfiniteGas = false;
    public static String SKU_GAS = "gas";
    // SKU for our subscription (infinite gas)
    static final String SKU_INFINITE_GAS = "infinite_gas";
    // How many units (1/4 tank is our unit) fill in the tank.
    static final int TANK_MAX = 30;
    // Current amount of gas in tank, in units
    int mTank;
    private List<String> skuList;
    static final int RC_REQUEST = 1001;
    public static final String TANK_DATA = "tank_data";
    IabHelper mHelper;
    private String base64EncodedPublicKey;
    private String payload = "asjdgasjdgdahsd";
    private boolean testMode;


    public int getTankState() {
        SharedPreferences state = getSharedPreferences(TAG, 0);
        mTank = state.getInt(TANK_DATA, 0);
        return mTank;
    }

    public void setTankState() {
        SharedPreferences state = getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = state.edit();
        editor.putInt(TANK_DATA, mTank);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("Destroying helper.");
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
        // setTankState();
    }

    public void initPurchase(String key, boolean testMode, String productId) {
        this.testMode = testMode;
        base64EncodedPublicKey = key;
        this.productId = productId;

        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException(
                    "Please put your app's public key in MainActivity.java. See README.");
        }
        if (getPackageName().startsWith("com.example")) {
            throw new RuntimeException(
                    "Please change the sample's package name! See README.");
        }

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    complain("Problem setting up in-app com.android.vending.billing: "
                            + result);
                    return;
                }
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

    }
    private String productId;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        @Override
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            // Do we have the infinite gas plan?
            Purchase infiniteGasPurchase = inventory
                    .getPurchase(SKU_INFINITE_GAS);
            mSubscribedToInfiniteGas = (infiniteGasPurchase != null && verifyDeveloperPayload(infiniteGasPurchase));
            Log.d(TAG, "User "
                    + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            if (mSubscribedToInfiniteGas)
                mTank = TANK_MAX;

            skuList = inventory.getAllOwnedSkus();
            if (skuList.size() == 0) {
                buyMore(productId);
            }

            // Check for gas delivery -- if we own gas, we should fill up the
            // tank immediately
            for (String skuItem : skuList) {

                Purchase gasPurchase = inventory.getPurchase(skuItem);
                if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                    Log.d(TAG, "We have gas. Consuming it.");
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(skuItem),
                                mConsumeFinishedListener);
                    }catch (IllegalStateException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
            onInventoryCheckFinished();

        }
    };

    public boolean canConsume(int count) {
        getTankState();
        return !(!mSubscribedToInfiniteGas && (mTank <= count));
    }

    public void consume(int count) {
        getTankState();
        Log.d(TAG, "Drive button clicked.");
        if (!mSubscribedToInfiniteGas && (mTank <= count))
            alert("Oh, no! You are out of Zokens! Try buying some!");
        else {
            if (!mSubscribedToInfiniteGas)
                mTank -= count;
            setTankState();
            Log.d(TAG, "Vrooom. Account is now " + mTank);
        }
    }

    public void addGas(int count) {
        getTankState();
        Log.d(TAG, "Drive button clicked.");
        if (!mSubscribedToInfiniteGas)
            mTank += count;
        setTankState();

        Log.d(TAG, "Vrooom. Account is now " + mTank);
    }

    public void buyMore(String ID) {

        setWaitScreen(true);

        if (testMode)
            ID = "android.test.purchased";
        mHelper.launchPurchaseFlow(this, ID, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (data != null && mHelper != null && !mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {

        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        // String payload = p.getDeveloperPayload();

        return true;
    }

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase
                    + ", result: " + result);

            getTankState();

            if (result.isSuccess()) {

                int count = convertSku2Count(purchase.getSku());


                Log.d(TAG, "Consumption successful. Provisioning.");
                mTank = mTank == TANK_MAX ? TANK_MAX : mTank + count;

                // alert("Your tank is now " + mTank);

            } else {
                complain("Error while consuming: " + result);
            }

            setTankState();
            setWaitScreen(false);

            FuelPurchaseActivity.this.onConsumeFinished();

            Log.d(TAG, "End consumption flow.");
        }

    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            Log.e(TAG, "Purchase : " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                onPurchaseFinished("0", "" + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }
            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_INFINITE_GAS)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Infinite gas subscription purchased.");
                alert("Thank you for subscribing to infinite Zokens!");
                mSubscribedToInfiniteGas = true;
                mTank = TANK_MAX;
                setTankState();

                setWaitScreen(false);

            } else {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);


            }
            onPurchaseFinished(purchase, purchase.getOrderId(), purchase.getToken());
        }
    };

    protected void onInventoryCheckFinished() {
    }

    protected int convertSku2Count(String sku_name) {
        return 0;
    }

    protected void onPurchaseFinished(String orderID, String token) {

    }

    protected void onPurchaseFinished(Purchase purchase, String orderID, String token) {
        onPurchaseFinished(orderID, token);

    }

    protected void onConsumeFinished() {
    }

    protected void setWaitScreen(boolean set) {

    }

    protected void complain(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                .show();
        // alert("Error: " + message);
    }

    protected void alert1(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void alert(String message) {
        //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    protected void log(String string) {
        Log.e("Purchase", string);
    }

}
