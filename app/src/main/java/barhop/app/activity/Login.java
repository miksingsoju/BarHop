package barhop.app.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import barhop.app.R;
import io.realm.Realm;
import io.realm.RealmResults;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.Toast;

import barhop.app.model.User;
/**
 * This is the first thing the user sees when opening the app (even without logging in)
 */
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parentConstraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    Button adminRegisterButton, userRegisterButton, loginButton, userSecret, adminSecret;
    EditText userInput, passwordInput;

    Realm realm;

    RealmResults<User> users;

    SharedPreferences rememberedUser;
    CheckBox rememberCheckBox;

    /**
     * This method initializes the views needed.
     */
    public void init(){
        realm = Realm.getDefaultInstance();

        adminRegisterButton = findViewById(R.id.adminRegisterButton);
        userRegisterButton = findViewById(R.id.userRegisterButton);
        loginButton = findViewById(R.id.loginButton);
        userSecret = findViewById(R.id.userSecret);
        adminSecret = findViewById(R.id.adminSecret);

        userInput = findViewById(R.id.userInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberCheckBox = findViewById(R.id.parentConstraint);

        userSecret.setOnClickListener(v -> UserSecret());
        adminSecret.setOnClickListener(v-> AdminSecret());
        adminRegisterButton.setOnClickListener(view -> register("ADMIN"));
        userRegisterButton.setOnClickListener(view -> register("USER"));
        loginButton.setOnClickListener(view -> login());

        users = realm.where(User.class).findAll();

        rememberedUser = getSharedPreferences("rememberedUser", MODE_PRIVATE);
        boolean isRemembered = rememberedUser.getBoolean("remember", false);

        if (isRemembered) {
            String savedUsername = rememberedUser.getString("username", "");
            String savedPassword = rememberedUser.getString("password", "");

            userInput.setText(savedUsername);
            passwordInput.setText(savedPassword);
            rememberCheckBox.setChecked(true);
        }
    }

    /**
     * This logs you in
     */
    private void login(){
        String username = userInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        // QUERY: Look for a user with matching name
        User result = realm.where(User.class).equalTo("displayName", username).findFirst();

        if (result != null) {
            if (result.getPassword().equals(password)) {
                realm.beginTransaction();
                realm.commitTransaction();

                SharedPreferences preferences = getSharedPreferences("rememberMe", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                if (rememberCheckBox.isChecked()) {
                    editor.putBoolean("remember", true);
                    editor.putString("uuid", result.getUuid());
                    Toast.makeText(this, "You will be remembered, " + username, Toast.LENGTH_LONG).show();

                } else {
                    editor.clear(); // Remove saved credentials
                    userInput.setText("");
                    passwordInput.setText("");
                }
                editor.apply();

                String userUuid = result.getUuid(); // Add this line

               SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
               SharedPreferences.Editor authEdit = auth.edit();
               authEdit.putString("uuid",userUuid);
               authEdit.apply();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Logged in. ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_LONG).show();
            }
        } else {
            // No user found
            Toast.makeText(this, "User not found.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This is a helper function
     *
     * This method takes 2 options, if destination is ADMIN, it will take you to the Admin Registration. If destination is USER,
     * it will take you to the User Registration
     * @param destination decides whether it is User or Admin
     */
    private void register(String destination) {
        if (destination.equals("USER")){
            Intent intent = new Intent(this, UserRegistration.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, AdminRegistration.class);
            startActivity(intent);
        }
    }

    public void UserSecret() {
        Intent intent = new Intent(this, AllUsers.class);
        startActivity(intent);

    }

    public void AdminSecret() {
        Intent intent = new Intent(this, AllAdmins.class);
        startActivity(intent);
    }
}