package com.example.safekey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import androidx.biometric.BiometricPrompt;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    CircleImageView profilePic;
    FragmentActivity activity;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private SwitchCompat biometricSwitch;
    private SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<CardMaker> cardlist;
    DataStore dataStore;
    DrawerLayout drawerLayout;
    private TextView userName,useremail;
    private SessionManager session;
    private DatabaseHandler db;
    private HashMap<String,String> user = new HashMap<>();
    String name,email;
    boolean isBdExists;
    TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHandler(HomeActivity.this);
        user = db.getUserDetails();

        // session manager
        session = new SessionManager(HomeActivity.this);

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        dataStore = new DataStore(this);

        executor = ContextCompat.getMainExecutor(this);
        activity = this;
        empty = (TextView)findViewById(R.id.empty);
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);getSupportActionBar().setTitle(null);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        biometricSwitch = (SwitchCompat)navigationView.getMenu().findItem(R.id.nav_switch).getActionView();
        try {
            if(Integer.parseInt(user.get("biometric"))==1)
                isBdExists = true;
            else
                isBdExists = false;
        }
        catch (Exception e)
        {
            isBdExists = false;
        }
        if(isBdExists){biometricSwitch.setChecked(true);}
        else{biometricSwitch.setChecked(false);}
        userName = (TextView) header.findViewById(R.id.username);
        useremail = (TextView) header.findViewById(R.id.useremail);
        profilePic = (CircleImageView) header.findViewById(R.id.profilepic);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);



        // Fetching user details from database
         name = user.get("name");
        email = user.get("email");
        userName.setText(name);
        useremail.setText(email);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.ActionToggle));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
           // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }


        biometricSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    disableBiometric();;

                } else {
                    setBiometric();

                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id)
                {

                    case R.id.nav_home:
                        break;
                    case R.id.nav_addcard:
                        Intent i = new Intent(getApplicationContext(),Add.class);
                        startActivity(i);break;
                    case R.id.synch:
                        new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Opsss....")
                                .setContentText("Cloud backup is in beta testing, this will come in upcoming update")
                                .show();break;
                    case R.id.nav_logout:
                        logoutUser();break;
                    case R.id.nav_share:
                        Intent share  = new Intent(Intent.ACTION_SEND);
                        share.setType("test/plane");
                        share.putExtra(Intent.EXTRA_TEXT,"Manage your password, locally in single utility. SafeKey\n Downlode and try it : https://developervaibhav.in/safekey");
                        startActivity(share);
                        break;
                    case R.id.nav_rate:
                        String uriString = "https://developervaibhav.in/safekey.html";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            Log.d("TAG", "onClick: inTryBrowser");
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Log.e("TAG", "onClick: in inCatchBrowser", ex );
                            intent.setPackage(null);
                            startActivity(Intent.createChooser(intent, "Select Browser"));
                        }
                        break;
                    default:
                        return true;

                }
                return true;
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecylerDecor(getResources().getDimensionPixelSize(R.dimen.sidepadding),getResources().getDimensionPixelSize(R.dimen.toppadding)));
        cardlist= new ArrayList<>();
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                cardlist.clear();
                loadrecyleview();
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeContainer.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        loadrecyleview();


    }

    private void logoutUser() {
        session.setLogin(false);
        // Launching the login activity
        Functions logout = new Functions();
        logout.logoutUser(HomeActivity.this);
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void loadrecyleview() {
        //while(dataStore.getData().moveToLast())
        Cursor c = dataStore.getData();
        if(c.getCount()<=0){empty.setVisibility(View.VISIBLE);}
        else {empty.setVisibility(View.GONE);}
       while (c.moveToNext())
       {
           Log.d("",c.getString(0)+"\n"+c.getString(1)+"\n"+c.getString(2)+"\n"+c.getString(3)+"\n"+c.getString(4));
           String id = c.getString(0);
           String auth_email = c.getString(1);
           String card_name = c.getString(2);
           String username = c.getString(3);
           String password = c.getString(4);
           CardMaker cardMaker = new CardMaker(card_name,username,password);
           cardlist.add(cardMaker);
           adapter=new CardAdapter(cardlist,getApplicationContext());
           recyclerView.setAdapter(adapter);

        }
        //{ Log.d("Json Fetch", String.valueOf(dataStore.getData().getCount()));}
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
            finishAffinity();
            System.exit(0);
        }
    }

    public void setBiometric()
    {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
        {
            final boolean[] error = {true};
            biometricPrompt = new BiometricPrompt(HomeActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                    biometricSwitch.setChecked(false);
                    return;
                }
                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show();
                    biometricSwitch.setChecked(false);
                    return;
                }
                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    if(changeBiometric("1"))
                    Toast.makeText(HomeActivity.this, "Biometric Authentication Activate", Toast.LENGTH_SHORT).show();
                    else
                    {
                        Log.d("","Error");
                    }
                    return;
                }


            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric setup for my app")
                    .setSubtitle("Accessing app content required biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build();

            // Prompt appears when user clicks "Log in".
            // Consider integrating with the keystore to unlock cryptographic operations,
            // if needed by your app.
            biometricPrompt.authenticate(promptInfo);
        }
        else
        {
            biometricSwitch.setChecked(false);
            new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Opsss....")
                    .setContentText("Biometric Authentication works only on 9.0 or more")
                    .show();
        }
    }
    public void disableBiometric()
    {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
        {
            final boolean[] error = {true};
            biometricPrompt = new BiometricPrompt(HomeActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                    biometricSwitch.setChecked(true);
                    return;
                }
                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show();
                    biometricSwitch.setChecked(true);
                    return;
                }
                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    if (changeBiometric("0"))
                    Toast.makeText(HomeActivity.this, "Biometric Authentication Disabled", Toast.LENGTH_SHORT).show();
                    else
                    {
                        Log.d("","Erorr");
                    }
                    return;
                }


            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Desabling Biometric for my app")
                    .setSubtitle("Accessing app content desen't require any biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build();

            // Prompt appears when user clicks "Log in".
            // Consider integrating with the keystore to unlock cryptographic operations,
            // if needed by your app.
            biometricPrompt.authenticate(promptInfo);
        }
        else
        {
            biometricSwitch.setChecked(true);
            new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Opsss....")
                    .setContentText("Biometric Authentication works only on 9.0 or more")
                    .show();
        }
    }
    public boolean changeBiometric(String state)
    {
        if (db.setBiometric(state,email))
        {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item1:
                Toast.makeText(activity, "hello", Toast.LENGTH_SHORT).show();
                return  true;
            case R.id.item3:
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:vpatel2600@gmail.com"));
                email.putExtra(Intent.EXTRA_SUBJECT, "Report Issue");
                email.putExtra(Intent.EXTRA_TEXT, "Write your issue Here.");
                startActivity(email);

                return  true;
            case R.id.item4:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:vpatel2600@gmail.com")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Write Your Message Here");
                    startActivity(intent);
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}