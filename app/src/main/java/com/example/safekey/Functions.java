package com.example.safekey;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

public class Functions {
    //Main URL
    private static String MAIN_URL = "https://developervaibhav.in/app/SafeKey/";
    // Login URL
    public static String LOGIN_URL = MAIN_URL + "login.php";
    // Register URL
    public static String REGISTER_URL = MAIN_URL + "register.php";
    // OTP Verification
    public static String OTP_VERIFY_URL = MAIN_URL + "verification.php";
    // Forgot Password
    public static String RESET_PASS_URL = MAIN_URL + "reset-password.php";

    public void logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}