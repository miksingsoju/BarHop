package barhop.app.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import barhop.app.R;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;


import barhop.app.model.Bar;
import barhop.app.model.User;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * This is the first thing the user sees when opening the app (even without logging in)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavigationView); // assign to class variable

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                openHome();
                return true;
            } else if (itemId == R.id.nav_favebars) {
                openFavorites();
                return true;
            } else if (itemId == R.id.nav_logout) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Logging Out User")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            openLogout();
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                openProfile();
                return true;
            } else if (itemId == R.id.nav_login) {
                openLogin();
                return true;
            } else if (itemId == R.id.nav_addbars) {
                openAddBar();
                return true;
            }
            return false;

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parentConstraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        checkPermissions();
    }

    public void checkPermissions()
    {

        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            // all permissions accepted proceed
                            init();
                            isUserLoggedIn();
                        }
                        else
                        {
                            // notify about permissions
                            toastRequirePermissions();
                        }
                    }
                })
                .check();
    }

    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }

    Realm realm;

    // Button loginButton, createBarButton, favoriteBarsButton, barListButton;
    TextView mainText;
    BottomNavigationView bottomNav;

    SharedPreferences auth;

    RecyclerView recyclerView;

    TextView barName;

    /**
     * This method initializes the views needed.
     */
    public void init(){
        realm = Realm.getDefaultInstance();
        auth = getSharedPreferences("auth", MODE_PRIVATE);

        //loginButton = findViewById(R.id.loginButton);
        //createBarButton = findViewById(R.id.createBarButton);
        //favoriteBarsButton = findViewById(R.id.favouriteBarsButton);
        //barListButton = findViewById(R.id.barListButton);

        mainText = findViewById(R.id.mainText);

        //loginButton.setOnClickListener(view -> login());
        //favoriteBarsButton.setOnClickListener(view -> favoriteBars());
        //createBarButton.setOnClickListener(view -> createBar());
        //barListButton.setOnClickListener(view -> barList());
        initBarList();
        initLoggedOut(); // THIS MANAGES VISIBILITY
    }

    public void initBarList()
    {
        recyclerView = findViewById(R.id.recyclerView);
        barName = findViewById(R.id.barName);

        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        String userUuid = auth.getString("uuid","");

        // query the things to display
        RealmResults<Bar> list = realm.where(Bar.class).findAll();

        // initialize Adapter
        BarAdapter adapter = new BarAdapter(this,  userUuid, list, true);
        recyclerView.setAdapter(adapter);
    }

    /**
     * This takes you to the login screen, the login screen also has buttons that lead to register screens.
     */
    private void login(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }



    private void favoriteBars(){
        //Intent intent = new Intent(this, FavoriteBars.class);
        //startActivity(intent);
    }

    private void createBar(){
        Intent intent = new Intent(this, CreateBar.class);
        startActivity(intent);
    }


    private void isUserLoggedIn() {
        String userUuid = auth.getString("uuid", null);

        if (userUuid != null) {
            // Fetch user from Realm using UUID
            User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

            if (user != null) {
                mainText.setText("Welcome, " + user.getDisplayName() + "!");
                //loginButton.setText("User Settings"); // change appearance of login button
                //loginButton.setOnClickListener(view -> userSettings());

                if(user.isAdmin()){
                    initAdminView();
                } else {
                    initUserView();
                }
            }
        }
    }

    /**
     * This helper method is for initializing the view when a user has not signed in yet or logged out.
     * IT ONLY MANAGES VISIBILITY!
     */
    private void initLoggedOut(){
        //createBarButton.setVisibility(View.GONE);
        //favoriteBarsButton.setVisibility(View.GONE);

        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);
    }

    /**
     * This helper method is for initializing the views (buttons, etc.) that should be visible to admins
     */
    private void initAdminView(){
        //createBarButton.setVisibility(View.GONE);
        //favoriteBarsButton.setVisibility(View.VISIBLE);

        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_admin);

    }

    /**
     * This helper method is for initializing the views (buttons, etc.) that should be visible to normal users.
     * and hiding views that shouldnt be accessible to normal users.
     */

    private void initUserView(){
        //createBarButton.setVisibility(View.GONE);
        //favoriteBarsButton.setVisibility(View.VISIBLE);

        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_user);

    }


    // Nav Bar Buttons Edit

    private void openHome() {
        Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }




    private void openLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void openLogout() {
        SharedPreferences.Editor authEdit = auth.edit();
        authEdit.clear();
        authEdit.apply();
        Toast.makeText(this, "Logged Out ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void openProfile() {
        Intent intent = new Intent(this, UserSettings.class);
        startActivity(intent);
    }

    private void openAddBar() {
        Intent intent = new Intent(this, CreateBar.class);
        startActivity(intent);
    }

    private void openFavorites() {
        Intent intent = new Intent(this, FavoriteBars.class);
        startActivity(intent);
    }




}