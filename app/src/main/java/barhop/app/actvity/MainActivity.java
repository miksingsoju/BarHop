package barhop.app.actvity;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rememberCheckBox), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        isUserLoggedIn();
    }

    Realm realm;

    Button loginButton;
    TextView mainText;


    /**
     * This method initializes the views needed.
     */
    public void init(){
        realm = Realm.getDefaultInstance();

        loginButton = findViewById(R.id.loginButton);
        mainText = findViewById(R.id.mainText);

        loginButton.setOnClickListener(view -> login());

    }

    /**
     * This takes you to the login screen, the login screen also has buttons that lead to register screens.
     */
    private void login(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void isUserLoggedIn() {
        String userUuid = getIntent().getStringExtra("uuid");

        if (userUuid != null) {
            // Fetch user from Realm using UUID
            User user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

            if (user != null) {
                mainText.setText("Welcome, " + user.getDisplayName() + "!");
                loginButton.setVisibility(View.GONE); // hide login button
            }
        }
    }


}