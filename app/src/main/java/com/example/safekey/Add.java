package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class Add extends AppCompatActivity {
    List<String> cardIconList = new ArrayList<String>(Arrays.asList("Amazon","Amazon Prime Videos","Brainly","Digilocker","Facebook","Flipkart","Gaana","Google","Hotstar","Instagram","Irctc","Jiosavan","Linkdin","Lpu Touch","Messanger","Microsoft Office","Netflix","Ola","Oneplus","Outlook","Paytm","Phone Pe","Punjab National Bank","State Bank of India","Teitter","Uber","Zee5","Other"));
    int[] iconResource= new int[]{R.drawable.card_ic_amazon,R.drawable.card_ic_amazonprimevideo,R.drawable.card_ic_brainly,R.drawable.icon_card,R.drawable.card_ic_facebook,R.drawable.card_ic_flipkart,R.drawable.card_ic_gaana,R.drawable.card_ic_google,R.drawable.card_ic_hotstar,R.drawable.card_ic_instagram,R.drawable.card_ic_irctc,R.drawable.card_ic_jiosaavn,R.drawable.card_ic_linkedin,R.drawable.lpu,R.drawable.card_ic_messenger,R.drawable.card_ic_microsoftoffice,R.drawable.card_ic_netflix,R.drawable.card_ic_ola,R.drawable.card_ic_oneplus,R.drawable.card_ic_outlook,R.drawable.card_ic_paytm,R.drawable.card_ic_phonepe,R.drawable.card_ic_pnb,R.drawable.card_ic_sbi,R.drawable.icon_card,R.drawable.icon_card,R.drawable.card_ic_zee5,R.drawable.icon_card};
    TextView textView,card_title,card_username,card_password;
    EditText username_tvview,pass_tvview;
    CircleImageView card_icon;
    ArrayList<String> arrayList;
    Dialog dialog;
    String loginCradName,userName,userPassword,loggedinUserEmail;
    DataStore dataStore;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        dataStore = new DataStore(this);
        db = new DatabaseHandler(this);
        loggedinUserEmail = db.getUserDetails().get("email");
        username_tvview = (EditText) findViewById(R.id.username_tvview);
        pass_tvview = (EditText) findViewById(R.id.pass_tvview);
        textView = (TextView)findViewById(R.id.text_view);
        card_title = (TextView)findViewById(R.id.card_title);
        card_username = (TextView)findViewById(R.id.card_username);
        card_password = (TextView)findViewById(R.id.card_password);
        card_icon = (CircleImageView)findViewById(R.id.card_icon);
        arrayList = new ArrayList<>();
        arrayList.add("Amazon");
        arrayList.add("Amazon Prime Videos");
        arrayList.add("Brainly");
        arrayList.add("Digilocker");
        arrayList.add("Facebook");
        arrayList.add("Flipkart");
        arrayList.add("Gaana");
        arrayList.add("Google");
        arrayList.add("Hotstar");
        arrayList.add("Instagram");
        arrayList.add("Irctc");
        arrayList.add("Jiosavan");
        arrayList.add("Linkdin");
        arrayList.add("Lpu Touch");
        arrayList.add("Messanger");
        arrayList.add("Microsoft Office");
        arrayList.add("Netflix");
        arrayList.add("Ola");
        arrayList.add("Oneplus");
        arrayList.add("Outlook");
        arrayList.add("Paytm");
        arrayList.add("Phone Pe");
        arrayList.add("Punjab National Bank");
        arrayList.add("State Bank of India");
        arrayList.add("Teitter");
        arrayList.add("Uber");
        arrayList.add("Zee5");
        arrayList.add("Other");


        username_tvview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                card_username.setText(username_tvview.getText().toString().trim());
            }
        });
        pass_tvview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                card_password.setText(pass_tvview.getText().toString().trim());
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(Add.this);
                dialog.setContentView(R.layout.spinner);
                dialog.getWindow().setLayout(800,1500);
                dialog.show();
                EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
                editText.setTextColor(getColor(R.color.dark_blue));
                ListView listView = (ListView) dialog.findViewById(R.id.list_view);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Add.this, android.R.layout.simple_list_item_1
                        ,arrayList);
                listView.setAdapter(arrayAdapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        arrayAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        textView.setText(arrayAdapter.getItem(position));
                        card_title.setText(arrayAdapter.getItem(position));
                        int i=0;
                        if(cardIconList.indexOf(arrayAdapter.getItem(position))!=-1)
                            i =cardIconList.indexOf(arrayAdapter.getItem(position));
                        else
                            i = cardIconList.size();
                        card_icon.setImageResource(iconResource[i]);
                        dialog.dismiss();
                    }
                });
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }


    public void onClickSave(View view) {
        if(validation())
        {
            if(dataStore.insert(loggedinUserEmail,loginCradName,userName,userPassword))
            {
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Saved Successful")
                        .show();
            }
            else
            {
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        }
    }

    public void demoBtn(View view) {
        new SweetAlertDialog(Add.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Info!")
                .setContentText("This is a demo card button works when you save it")
                .setConfirmText("OK")
                .show();
    }
    private boolean validation(){
        loginCradName = textView.getText().toString().trim();
        userName = username_tvview.getText().toString().trim();
        userPassword = pass_tvview.getText().toString().trim();
        if(loginCradName=="Not Selected")
        {
            return false;
        }
        if(userName.isEmpty()){
            username_tvview.setError("Username Can't be empty");
            return false;
        }
        if(userPassword.isEmpty()){
            pass_tvview.setError("Password can't be empty");
            return false;
        }
        return true;

    }
    @Override
    public void onBackPressed() {
            finish();
        Intent i = new Intent(Add.this,HomeActivity.class);
        startActivity(i);
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

