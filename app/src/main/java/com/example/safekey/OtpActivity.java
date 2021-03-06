package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = OtpActivity.class.getSimpleName();
    private PinView pinView;
    private String email;
    private Button btn_verify;
    private TextView otpCountDown,emailtv,btn_resend;
    private SweetAlertDialog pDialog;
    Bundle bundle;
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

    private SessionManager session;
    private DatabaseHandler db;

    private static final String FORMAT = "%02d:%02d";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        btn_verify=(Button) findViewById(R.id.btn_verify);
        pinView=(PinView) findViewById(R.id.secondPinView);
        emailtv= (TextView)findViewById(R.id.email);
        btn_resend = (TextView) findViewById(R.id.btn_resend);
        otpCountDown = (TextView)findViewById(R.id.error);
        pinView.setAnimationEnable(true);
        pDialog = new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.PROGRESS_TYPE);

        bundle = getIntent().getExtras();
        email = bundle.getString("email");
        emailtv.setText(email);
        db = new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        // Hide Keyboard
       getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        btn_resend.setEnabled(false);
        btn_resend.setVisibility(View.GONE);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                String otp = Objects.requireNonNull(pinView.getText()).toString();
                if (!otp.isEmpty()) 
                {
                    if(otp.length()==6) {
                        verifyCode(email, otp);
                        Toast.makeText(OtpActivity.this, otp, Toast.LENGTH_SHORT).show();
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FF3B5998"));//FF3B5998
                        pDialog.setCancelable(false);
                    }
                    else
                    {
                        new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setContentText("Please enter verification code of 6 digit")
                                .show();
                    }
                }
                else {
                    new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Please enter verification code")
                            .show();
                }
                
            }
        });

        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
            }
        });
        countDown();


    }
    private void countDown() {
        new CountDownTimer(70000, 1000) { // adjust the milli seconds here

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                otpCountDown.setVisibility(View.VISIBLE);
                otpCountDown.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) ));
            }

            public void onFinish() {
                otpCountDown.setVisibility(View.GONE);
                btn_resend.setEnabled(true);
                btn_resend.setVisibility(View.VISIBLE);

            }
        }.start();
    }
    private void verifyCode(final String email, final String otp) {
        // Tag used to cancel the request
        String tag_string_req = "req_verify_code";
        pDialog.setTitleText("Checking ...");
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.OTP_VERIFY_URL, response -> {
            Log.d(TAG, "Verification Response: " + response);
            pDialog.dismiss();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    JSONObject json_user = jObj.getJSONObject("user");

                    Functions logout = new Functions();
                    logout.logoutUser(getApplicationContext());
                    db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                    session.setLogin(true);
                    Intent upanel = new Intent(OtpActivity.this, HomeActivity.class);
                    upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(upanel);
                    finish();

                } else {
                    new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Invalid Verification Code")
                            .show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            Log.e(TAG, "Verify Code Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("tag", "verify_code");
                params.put("email", email);
                params.put("otp", otp);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };
        // Adding request to request queue
       // MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(strReq);
    }
    private void resendCode() {
        // Tag used to cancel the request
        String tag_string_req = "req_resend_code";
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FF3B5998"));
        pDialog.setTitleText("Sending ...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.OTP_VERIFY_URL, response -> {
            Log.d(TAG, "Resend Code Response: " + response);
            pDialog.dismiss();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("OTP Resend")
                            .setContentText("Please check your spam/promotiomal folder of mailbox")
                            .show();
                      btn_resend.setEnabled(false);
                      btn_resend.setVisibility(View.GONE);
                      countDown();
                } else {
                    Toast.makeText(getApplicationContext(), "Code sending failed!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Log.e(TAG, "Resend Code Error: " + response);
                Toast.makeText(getApplicationContext(), "Json error: " + response, Toast.LENGTH_LONG).show();

            }

        }, error -> {
            Log.e(TAG, "Resend Code Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("tag", "resend_code");
                params.put("email", email);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

        };
        // Adding request to request queue
       // MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(strReq);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}