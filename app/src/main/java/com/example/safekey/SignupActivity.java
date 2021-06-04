package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignupActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private static final String TAG = SignupActivity.class.getSimpleName();
    private SweetAlertDialog pDialog;
    private EditText userName, userEmail, userPassword, userConPass;
    private Button btnSignUp;
    private TextView txtLogin;
    private String name, email, pwd, con_pwd;
    private CheckBox checkBox;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final String passwordPattern ="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";// "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[$@$!%*#?&])[A-Za-z\\\\d$@$!%*#?&]{8,}$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        pDialog = new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FF3B5998"));//FF3B5998
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(false);
        initializeWidgets();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        name = sharedPreferences.getString(NAME, "");
        userName.setText(name);
        email = sharedPreferences.getString(EMAIL, "");
        userEmail.setText(email);
        pwd = sharedPreferences.getString(PASSWORD, "");
        userPassword.setText(pwd);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBox.isChecked())
                {
                    userPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else if(!checkBox.isChecked())
                {
                    userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validation()){return;}
                if(isConnected())
                    registerUser(name, email, pwd);
                else
                {
                    new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("No Internet Connextion")
                            .setContentText("Check your mobile data or wifi and then try again!")
                            .show();
                }
            }
        });
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(NAME, userName.getText().toString());
                editor.apply();
            }
        });
        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(EMAIL, userEmail.getText().toString());
                editor.apply();
            }
        });
        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(PASSWORD, userPassword.getText().toString());
                editor.apply();
            }
        });

    }

    private void initializeWidgets(){
        userName = (EditText)findViewById(R.id.ed_name);
        userEmail = (EditText)findViewById(R.id.ed_email);
        userPassword = (EditText)findViewById(R.id.ed_pass);
        userConPass = (EditText)findViewById(R.id.con_pass);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        txtLogin = (TextView)findViewById(R.id.signup);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
    }




    private boolean validation(){
        name = userName.getText().toString().trim();
        email = userEmail.getText().toString().trim();
        pwd = userPassword.getText().toString().trim();
        con_pwd = userConPass.getText().toString().trim();

        if(email.isEmpty()){
            userEmail.setError("Email Required");
            return false;
        }
        if(!(email.matches(emailPattern)))
        {
            userEmail.setError("Email formate dosen't match");
            return false;
        }
        if(pwd.isEmpty()){
            userPassword.setError("Password Required");
            return false;
        }
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(pwd);
        if(!matcher.matches())
        {
            userPassword.setError("A minimum 8 characters contains a combination of minimum 1 uppercase and lowercase letter, number and special charecter Eg:Demo@Pass1");
            return false;
        }
        if(con_pwd.isEmpty()){
            userConPass.setError("Password Required");
            return false;
        }
        if(!(pwd.equals(con_pwd)))
        {
            userConPass.setError("Confirm password dosen't match");
            return false;
        }
        return true;

    }
    private void registerUser(final String name, final String email, final String password) {
        // Tag used to cancel the request
        pDialog.show();
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.POST, Functions.REGISTER_URL, response -> {
            Log.d(TAG, "Register Response: " + response);
            pDialog.dismiss();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    Functions logout = new Functions();
                    logout.logoutUser(getApplicationContext());

                    Bundle b = new Bundle();
                    b.putString("email", email);
                    Intent i = new Intent(SignupActivity.this, OtpActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtras(b);
                    startActivity(i);
                    finish();



                } else {
                    // Error occurred in registration. Get the error
                    // message
                    Log.d(TAG, "Register Response1: " + response);
                    String errorMsg = jObj.getString("message");
                    Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

            Log.e(TAG, "Registration Error: " + error.getMessage(), error);
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
           // hideDialog();
            pDialog.dismiss();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };



        // Adding request to request queue
       // MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(strReq);
    }

    public void reset(View view) {
        userName.setText("");
        userEmail.setText("");
        userPassword.setText("");
    }
    public boolean isConnected()
    {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(null!=activeNetwork)
        {
            return true;
        }
        return false;
    }
}
