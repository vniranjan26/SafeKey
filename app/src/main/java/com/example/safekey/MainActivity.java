package com.example.safekey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button btnChangePass, btnLogout;
    private SessionManager session;

    private HashMap<String,String> user = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txtName = findViewById(R.id.username);
        TextView txtEmail = findViewById(R.id.email);
        btnChangePass = findViewById(R.id.change_password);
        btnLogout = findViewById(R.id.logout);

        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        user = db.getUserDetails();

        // session manager
        session = new SessionManager(MainActivity.this);

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from database
        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void logoutUser() {
        session.setLogin(false);
        // Launching the login activity
        Functions logout = new Functions();
        logout.logoutUser(MainActivity.this);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}