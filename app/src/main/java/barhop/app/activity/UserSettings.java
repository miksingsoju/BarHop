package barhop.app.activity;

import android.os.Bundle;

import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;

import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import android.widget.Toast;

import barhop.app.R;
import barhop.app.model.User;
import io.realm.Realm;

public class UserSettings extends AppCompatActivity {

    TextView userSettingsLabel;
    EditText userSettingsDisplayName, userSettingsPasswordField;
    ImageView userSettingsImage , userEditButton;
    Button userSettingsReturnButton;

    Realm realm;
    SharedPreferences auth;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rememberCheckBox), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    public void initViews(){
        realm = Realm.getDefaultInstance();
        auth = getSharedPreferences("auth",MODE_PRIVATE);
        String userUUID = auth.getString("uuid","");
        user = realm.where(User.class).equalTo("uuid",userUUID).findFirst();


        userSettingsLabel = findViewById(R.id.userSettingsLabel);
        userSettingsDisplayName = findViewById(R.id.userSettingsDisplayName);
        userSettingsPasswordField = findViewById(R.id.userSettingsPasswordField);

        userSettingsImage = findViewById(R.id.userSettingsImage);

        userSettingsReturnButton = findViewById(R.id.userSettingsReturnButton);
        userEditButton = findViewById(R.id.userEditButton);


        userSettingsReturnButton.setOnClickListener(v -> ReturnHome());
        userEditButton.setOnClickListener(v -> EditCreds());


    }

    public void ReturnHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void EditCreds() {
        String newName = userSettingsDisplayName.getText().toString();
        String newPassword = userSettingsPasswordField.getText().toString();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Bar name must not be blank", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            realm.beginTransaction();
            user.setDisplayName(newName);
            user.setPassword(newPassword);
            realm.commitTransaction();

            Toast.makeText(this, "Editing " + user.getDisplayName() + " was edited successfully." , Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error changing profile details", Toast.LENGTH_LONG).show();
        }

    }

    public void EditPassword() {
        // Please do edit password stuff here
    }
}