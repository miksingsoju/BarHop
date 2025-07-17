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

import barhop.app.R;

public class UserSettings extends AppCompatActivity {

    TextView userSettingsLabel;
    EditText userSettingsDisplayName, userSettingsPasswordField;
    ImageView userSettingsImage , userSettingsEditNameButton, userSettingsEditPasswordButton;
    Button userSettingsReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parentConstraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    public void initViews(){
        userSettingsLabel = findViewById(R.id.userSettingsLabel);
        userSettingsDisplayName = findViewById(R.id.userSettingsDisplayName);
        userSettingsPasswordField = findViewById(R.id.userSettingsPasswordField);

        userSettingsImage = findViewById(R.id.userSettingsImage);

        userSettingsReturnButton = findViewById(R.id.userSettingsReturnButton);
        userSettingsEditNameButton = findViewById(R.id.userSettingsEditNameButton);
        //userSettingsEditPasswordButton = findViewById(R.id.userSettingsEditPasswordButton);

        userSettingsReturnButton.setOnClickListener(v -> ReturnHome());
        userSettingsEditNameButton.setOnClickListener(v -> EditName());
        //userSettingsEditPasswordButton.setOnClickListener(v -> EditPassword());

    }

    public void ReturnHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void EditName() {
        // Please do edit name stuff here
    }

    public void EditPassword() {
        // Please do edit password stuff here

    }
}