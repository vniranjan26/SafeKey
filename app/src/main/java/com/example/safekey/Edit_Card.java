package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Edit_Card extends AppCompatActivity {
    TextView card_title,card_username,card_password,text_view,username_tvview;
    Bundle bundle;
    EditText pass_tvview;
    DataStore dataStore;
    private DatabaseHandler db;
    private HashMap<String,String> user = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__card);
        card_title = (TextView)findViewById(R.id.card_title);
        card_username = (TextView)findViewById(R.id.card_username);
        card_password = (TextView)findViewById(R.id.card_password);
        text_view = (TextView)findViewById(R.id.text_view);
        username_tvview = (TextView)findViewById(R.id.username_tvview);
        bundle = getIntent().getExtras();
        card_title.setText(bundle.getString("cardName"));
        text_view.setText(bundle.getString("cardName"));
        card_username.setText(bundle.getString("userName"));
        username_tvview.setText(bundle.getString("userName"));
        pass_tvview.setText(bundle.getString("userPassword"));

        db = new DatabaseHandler(Edit_Card.this);
        user = db.getUserDetails();
        dataStore = new DataStore(this);

        pass_tvview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                card_password.setText(pass_tvview.getText().toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onClickSave(View view) {
        DataStore dataStore = new DataStore(Edit_Card.this);
        if(dataStore.update(user.get("email"),bundle.getString("cardName"),bundle.getString("userName"),pass_tvview.getText().toString().trim())){
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Sccessful")
                    .setContentText(bundle.getString("cardName")+"card update")
                    .show();
        }
        else
        {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Something went wrong!")
                    .show();
        }
        finish();
        Intent i = new Intent(Edit_Card.this,HomeActivity.class);
        startActivity(i);

    }
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(Edit_Card.this,HomeActivity.class);
        startActivity(i);
    }
    public void demoBtn(View view) {
        new SweetAlertDialog(Edit_Card.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Info!")
                .setContentText("This is a demo card button works when you save it")
                .setConfirmText("OK")
                .show();
    }
    public void gernatePass(View view) {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "01234567890123456789";
        String SPECIAL_CHARS = "!@#$%^&*/=?!@#$%^&*/=?";
        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER+SPECIAL_CHARS;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);

        }
        pass_tvview.setText(sb.toString());

    }
}