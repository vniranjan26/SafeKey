package com.example.safekey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.Executor;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashScreen extends AppCompatActivity {
    DatabaseHandler db;
    FragmentActivity activity;
    private SessionManager session;
    private HashMap<String,String> user = new HashMap<>();
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        activity = this;
        executor = ContextCompat.getMainExecutor(activity);
        db = new DatabaseHandler(activity);
        user = db.getUserDetails();

        // session manager
        session = new SessionManager(activity);

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    if(!session.isLoggedIn())
                    {
                        Intent i = new Intent(activity, HomeActivity.class);
                        startActivity(i);
                    }
                    else {
                    if (Integer.parseInt(user.get("biometric")) == 1) {
                        verifyBiometric();
                    } else {
                        Intent i = new Intent(activity, HomeActivity.class);
                        startActivity(i);
                    }
                }
            }
        }, 2000);
    }
    public  void verifyBiometric()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
        {
            biometricPrompt = new BiometricPrompt(activity,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();

                }
                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show();

                }
                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Intent i = new Intent(activity,HomeActivity.class);
                    startActivity(i);
                }


            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verify is this "+user.get("name")+" ?")
                    .setSubtitle("Accessing app require any biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build();

            // Prompt appears when user clicks "Log in".
            // Consider integrating with the keystore to unlock cryptographic operations,
            // if needed by your app.
            biometricPrompt.authenticate(promptInfo);
        }
        else
        {
            new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Opsss....")
                    .setContentText("Biometric Authentication works only on 9.0 or more")
                    .show();
        }
    }
    private void logoutUser() {
        session.setLogin(false);
        // Launching the login activity
        Functions logout = new Functions();
        logout.logoutUser(activity);
        Intent intent = new Intent(activity, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}