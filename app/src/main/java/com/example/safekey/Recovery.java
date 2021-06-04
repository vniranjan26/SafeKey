package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Recovery extends AppCompatActivity {

    private SweetAlertDialog pDialog;
    EditText editText_emailCon,editText_email;
    TextView login,signup;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String txt_email,text_emailCon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        pDialog = new SweetAlertDialog(Recovery.this, SweetAlertDialog.PROGRESS_TYPE);
    }
    private void initializeWidgets()
    {
        editText_emailCon = (EditText)findViewById(R.id.editText_emailCon);
        editText_email = (EditText)findViewById(R.id.editText_email);
        signup = (TextView)findViewById(R.id.signup);
        login = (TextView)findViewById(R.id.login);
    }
    private boolean validation()
    {
        txt_email = editText_email.getText().toString().trim();
        text_emailCon = editText_emailCon.getText().toString().trim();

        if(txt_email.isEmpty()){
            editText_email.setError("Email Required");
            return false;
        }
        if(text_emailCon.isEmpty()){
            editText_emailCon.setError("Email Required");
            return false;
        }
        if(!(txt_email.matches(emailPattern)))
        {
            editText_email.setError("Email formate dosen't match");
            return false;
        }
        if(!(text_emailCon.matches(emailPattern)))
        {
            editText_emailCon.setError("Email formate dosen't match");
            return false;
        }
        if(text_emailCon.equals(txt_email)){
            editText_emailCon.setError("Email dosen't match");
            return false;
        }
        return true;
    }
    public void onSubmit(View view) {
        if(validation())
        {
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FF3B5998"));//FF3B5998
            pDialog.setTitleText("Loading ...");
            pDialog.setCancelable(true);
        }
    }
}