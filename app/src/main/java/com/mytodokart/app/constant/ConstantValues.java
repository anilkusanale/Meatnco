package com.mytodokart.app.constant;


import android.text.format.Formatter;
import android.util.Log;

import com.mytodokart.app.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * ConstantValues contains some constant variables, used all over the App.
 **/

public class ConstantValues {

    //*********** API Base URL ********//

   // public static final String ECOMMERCE_URL = "https://aapkidukaan.ecomempire.co/";
//    public static final String ECOMMERCE_URL = "http://198.211.110.165:8096/";
    public static final String ECOMMERCE_URL = "https://meatnco.in/";
   // public static final String ECOMMERCE_URL = "http://todokart.com/";

    public static final String ECOMMERCE_CONSUMER_KEY = "762f68dd1600171015672c1c97";
    public static final String ECOMMERCE_CONSUMER_SECRET = "ac82ac7316001710158eb23233";
    private static String TAG = "Consumer_IP";
    public static final String base64key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvpdVFoXVN83v+ruMqLUuklKHcV6r7Oh7KEYcovT46KhNLAoYBcDQ2iZrAoGpsyGOD8WI1k0O9b8uOJRHPC9rcouvoxuwU6hefsuLHpe7GYDLMVIkCMiyXaNiQYpTY35c5Rr1MrMiCpoAZjQfouOYGF6UXlMAoGtXyk6JRuBNALoDVyJ+T+eIdnovnOl7P/mMMR6Uxei5hULqujR9PCgFslmKiAfisGXwAt2ygUAjdOqoSaORbcW/vKs/8c4xBfoBXd27bPAWO/ftPhTPpi6/tidw17C7UvKuCA/PtebSYdGaABTohDd8EmKQT5+3ct9V9StKAWn6Ni1JmHwoYCKpkQIDAQAB";
    public static final String CODE_VERSION = "4.0.14";

    public static final boolean IS_CLIENT_ACTIVE = true;                               // "false" if compiling the project for Demo, "true" otherwise
    public static final int PURCHASE_RESULTCODE = 1001;
    public static final String DEFAULT_NOTIFICATION = "fcm";                      // "fcm" for FCM_Notifications, "onesignal" for OneSignal
    public static String NAVIGATION_STYLE = "side";                             // "bottom" for bottom navigation. "side" for side navigation.
    public static String DAYNIGHT_MODE = "day";                                 // "day" for light mode "night" for dark mode.

    public static int THEME_ID = R.style.AppTheme;

    public static String APP_HEADER;

    public static String MAINTENANCE_MODE;
    public static String MAINTENANCE_TEXT;

    public static String DEFAULT_HOME_STYLE;
    public static String DEFAULT_CATEGORY_STYLE;
    public static int DEFAULT_PRODUCT_CARD_STYLE;
    public static int DEFAULT_BANNER_STYLE;

    public static int LANGUAGE_ID;
    public static String LANGUAGE_CODE;
    public static String CURRENCY_SYMBOL;
    public static String CURRENCY_CODE;
    public static String PACKING_CHARGE;
    public static long NEW_PRODUCT_DURATION;

    public static boolean IS_GOOGLE_LOGIN_ENABLED;
    public static boolean IS_FACEBOOK_LOGIN_ENABLED;
    public static boolean IS_PHONE_LOGIN_ENABLED;
    public static boolean IS_EMAIL_LOGIN_ENABLED;
    public static boolean IS_ADD_TO_CART_BUTTON_ENABLED;

    public static boolean IS_ADMOBE_ENABLED;
    public static String ADMOBE_ID;
    public static String AD_UNIT_ID_BANNER;
    public static String AD_UNIT_ID_INTERSTITIAL;

    public static boolean IS_RESTART = false;

    public static String ABOUT_US;
    public static String TERMS_SERVICES;
    public static String PRIVACY_POLICY;
    public static String REFUND_POLICY;
    public static String A_Z;

    public static boolean IS_USER_LOGGED_IN;
    public static boolean IS_PUSH_NOTIFICATIONS_ENABLED;
    public static boolean IS_LOCAL_NOTIFICATIONS_ENABLED;

    public static String PKG_NAME;
    public static String SHA1;

    public static final String PHONE_PATTERN = "^[987]\\d{9}$";

    public static final String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i(TAG, "***** IP = " + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }


}
