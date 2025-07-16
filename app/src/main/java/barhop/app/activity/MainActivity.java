package barhop.app.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import barhop.app.R;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import barhop.app.model.User;
import io.realm.Realm;

/**
 * This is the first thing the user sees when opening the app (even without logging in)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
                openLogout();
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rememberCheckBox), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        isUserLoggedIn();

    }

    Realm realm;

    Button loginButton, createBarButton, favoriteBarsButton, barListButton;
    TextView mainText;
    BottomNavigationView bottomNav;


    /**
     * This method initializes the views needed.
     */
    public void init(){
        realm = Realm.getDefaultInstance();

        loginButton = findViewById(R.id.loginButton);
        createBarButton = findViewById(R.id.createBarButton);
        favoriteBarsButton = findViewById(R.id.favouriteBarsButton);
        barListButton = findViewById(R.id.barListButton);

        mainText = findViewById(R.id.mainText);

        // Non Logged In User

        // Normal User

        // Admin User

        loginButton.setOnClickListener(view -> login());
        favoriteBarsButton.setOnClickListener(view -> favoriteBars());
        createBarButton.setOnClickListener(view -> createBar());
        barListButton.setOnClickListener(view -> barList());

        initLoggedOut(); // THIS MANAGES VISIBILITY
    }

    /**
     * This takes you to the login screen, the login screen also has buttons that lead to register screens.
     */
    private void login(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void barList(){
        Intent intent = new Intent(this, BarList.class);
        startActivity(intent);
    }

    private void favoriteBars(){

    }

    private void createBar(){
        Intent intent = new Intent(this, CreateBar.class);
        String userUuid = getIntent().getStringExtra("uuid");
        intent.putExtra("uuid", userUuid);
        startActivity(intent);
    }


    private void isUserLoggedIn() {
        String userUuid = getIntent().getStringExtra("uuid");

        if (userUuid != null) {
            // Fetch user from Realm using UUID
            User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

            if (user != null) {
                mainText.setText("Welcome, " + user.getDisplayName() + "!");
                loginButton.setText("User Settings"); // change appearance of login button
                loginButton.setOnClickListener(view -> userSettings());

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
        createBarButton.setVisibility(View.GONE);
        favoriteBarsButton.setVisibility(View.GONE);

        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);
    }

    /**
     * This helper method is for initializing the views (buttons, etc.) that should be visible to admins
     */
    private void initAdminView(){
        createBarButton.setVisibility(View.GONE);
        favoriteBarsButton.setVisibility(View.VISIBLE);

        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_admin);

    }

    /**
     * This helper method is for initializing the views (buttons, etc.) that should be visible to normal users.
     * and hiding views that shouldnt be accessible to normal users.
     */

    private void initUserView(){
        createBarButton.setVisibility(View.GONE);
        favoriteBarsButton.setVisibility(View.VISIBLE);

        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_user);

    }


    // Nav Bar Buttons Edit

    private void openHome() {
        Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
        // Optional: navigate to a HomeActivity or update UI
    }

    private void openBars() {
        Toast.makeText(this, "Bars clicked", Toast.LENGTH_SHORT).show();
        barList();
        // Optional: open bar list screen
        // startActivity(new Intent(this, BarListActivity.class));
    }

    private void userSettings(){
        String userUuid = getIntent().getStringExtra("uuid");

        Intent intent = new Intent(this, UserSettings.class);
        intent.putExtra("uuid", userUuid);
        startActivity(intent);
    }

    private void openLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void openLogout() {
        Intent intent = new Intent(this, MainActivity.class);
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