package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.Context;
import android.graphics.Color;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

    private SweetAlertDialog pDialog;
    private EditText userEmail, userPassword;
    private Button btnSignup;
    private TextView txtSignup,forgot_pass;
    private String txt_email, txt_pass;
    private CheckBox checkBox;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private SessionManager session;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeWidgets();
        pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        db = new DatabaseHandler(this);
        // session manager
        session = new SessionManager(this);
        // check user is already logged in
        if (session.isLoggedIn()) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        btnSignup.setOnClickListener(this);

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
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
    }

    private void initializeWidgets()
    {
        userEmail = (EditText)findViewById(R.id.editText_email);
        userPassword = (EditText)findViewById(R.id.editText_emailCon);
        btnSignup = (Button)findViewById(R.id.btnLogin);
        txtSignup = (TextView)findViewById(R.id.signup);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        forgot_pass = (TextView)findViewById(R.id.login);
    }

    @Override
    public void onClick(View v) {
        if(validation())
        {
            if(isConnected()) {
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#FF3B5998"));//FF3B5998
                pDialog.setTitleText("Loading ...");
                pDialog.setCancelable(false);
                loginProcess(txt_email, txt_pass);
            }
            else
            {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("No Internet Connextion")
                        .setContentText("Check your mobile data or wifi and then try again!")
                        .show();
            }
        }
    }

    private void loginProcess(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.LOGIN_URL, response -> {
            Log.d(TAG, "Login Response: " + response);
            pDialog.dismiss();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    JSONObject json_user = jObj.getJSONObject("user");

                    Functions logout = new Functions();
                    logout.logoutUser(getApplicationContext());

                    if(Integer.parseInt(json_user.getString("verified")) == 1){
                        db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME),
                                json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                        
                        Intent upanel = new Intent(LoginActivity.this, HomeActivity.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(upanel);

                        session.setLogin(true);
                    } else {
                       Bundle b = new Bundle();
                        b.putString("email", email);

                        Intent upanel = new Intent(LoginActivity.this, OtpActivity.class);
                        upanel.putExtras(b);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(upanel);
                    }
                    finish();

                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("message");
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Login exeception " + response);
            }

        }, error -> {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
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
    private boolean validation()
    {
        txt_email = userEmail.getText().toString().trim();
        txt_pass = userPassword.getText().toString().trim();

        if(txt_email.isEmpty()){
            userEmail.setError("Email Required");
            return false;
        }
        if(!(txt_email.matches(emailPattern)))
        {
            userEmail.setError("Email formate dosen't match");
            return false;
        }
        if(txt_pass.isEmpty()){
            userEmail.setError("Email Required");
            return false;
        }
        return true;
    }

    public void thirdPartLogin(View view) {
        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Sorry.")
                .setContentText("Thrid party login will live soon, while try signup with email.")
                .show();
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
