package barhop.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import barhop.app.R;
import io.realm.Realm;

import barhop.app.model.User;

public class UserRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rememberCheckBox), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    EditText userInput, passwordInput, confirmPasswordInput, otpInput;
    Button saveButton;
    Button cancelButton;

    Realm realm;

    User newUser;

    public void initViews(){
        realm = Realm.getDefaultInstance();
        newUser = new User();

        userInput = findViewById(R.id.userInput);
        passwordInput = findViewById(R.id.passwordInput);

        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> save());

        cancelButton.setOnClickListener(v -> cancel());

    }

    public void cancel(){
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);

        Toast t = Toast.makeText(this, "Back to Login", Toast.LENGTH_LONG);
        t.show();
    }

    public void save() {
        String name = userInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();


        if (name.isEmpty()) {
            Toast.makeText(this, "Name must not be blank", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Confirm password does not match.", Toast.LENGTH_LONG).show();
            return;
        }





        // Check if user already exists
        User existingUser = realm.where(User.class).equalTo("displayName", name).findFirst();
        if (existingUser != null) {
            Toast.makeText(this, "Username is already taken.", Toast.LENGTH_LONG).show();
            return;
        }

        newUser.setDisplayName(name);
        newUser.setPassword(password);
        newUser.setAdmin(false);

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(newUser);
            realm.commitTransaction();

            long count = realm.where(User.class).equalTo("isAdmin", false).count();

            Toast.makeText(this, "Bar Hoppers saved: " + count, Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving user", Toast.LENGTH_LONG).show();
        }
    }
}