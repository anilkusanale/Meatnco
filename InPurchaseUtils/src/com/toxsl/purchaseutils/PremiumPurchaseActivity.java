package com.toxsl.purchaseutils;

import android.content.Intent;
import android.content.SharedPreferences;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PremiumPurchaseActivity extends AppCompatActivity {

    static final String TAG = "PremiumPurchaseActivity";

    String payload = "sdhbsijfbajdfabsjdbsjchdvjba";

    static final int RC_REQUEST = 1000;

    IabHelper mHelper;

    private String base64EncodedPublicKey;

    private boolean testMode;

    public boolean getCheckState(String productId) {
        SharedPreferences state = getSharedPreferences(TAG, 0);
        return state.getBoolean(productId, false);
    }

    public void setCheckState(String productId, boolean value) {

        SharedPreferences state = getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = state.edit();
        editor.putBoolean(productId, value);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("Destroying helper.");
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
    }

    public void init(String key, boolean testMode) {

        base64EncodedPublicKey = key;
        this.testMode = testMode;

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

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                onInventoryCheckFinished();
                return;
            }

            List<String> skuList = inventory.getAllOwnedSkus();

            // Check for gas delivery -- if we own gas, we should fill up the
            // tank immediately
            for (String skuItem : skuList) {

                Purchase prePurchase = inventory.getPurchase(skuItem);
                setCheckState(prePurchase.getSku(), false);
                if (prePurchase != null && verifyDeveloperPayload(prePurchase)) {
                    setCheckState(prePurchase.getSku(), true);
                }
            }
            setWaitScreen(false);

            onInventoryCheckFinished();
        }
    };

    public void purchaseProduct(String productId) {
        setWaitScreen(true);
        if (testMode)
            productId = "android.test.purchased";

        mHelper.launchPurchaseFlow(this, productId, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {

            super.onActivityResult(requestCode, resultCode, data);
        } else {

        }
    }

    boolean verifyDeveloperPayload(Purchase p) {
        /*
		 * String payload = ""; try { payload = p.getDeveloperPayload(); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } if (this.payload.contentEquals(payload))
		 * return true; return false;
		 */
        return true;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                onPurchaseFinished();
                return;
            }

            if (result.isAlreadyPurchased()) {

            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                onPurchaseFinished();
                return;
            }

            if (purchase != null) {
                setCheckState(purchase.getSku(), true);
            }

            setWaitScreen(false);

            onPurchaseFinished();

        }
    };

    protected void onInventoryCheckFinished() {

    }

    protected void onPurchaseFinished() {
        finish();
    }

    protected void setWaitScreen(boolean set) {

    }

    protected void complain(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                .show();
        // alert("Error: " + message);
    }

    protected void alert(String message) {
		/*
		 * AlertDialog.Builder bld = new AlertDialog.Builder(this);
		 * bld.setMessage(message); bld.setNeutralButton("OK", null);
		 * log("Showing alert dialog: " + message); bld.create().show();
		 */

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void log(String string) {
        Log.e("Purchase", string);
    }

}
