package barhop.app.actvity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageButton;

import android.content.Intent;



import barhop.app.R;

public class CreateBar extends AppCompatActivity {

    Button createBarReturnButton, createBarAddButton;
    TextView createBarLabel, createBarPicLabel, createBarNameLabel, createBarAddressLabel, createBarDescriptionLabel;
    EditText createBarNameField, createBarAddressField, createBarDescriptionField;
    ImageButton createBarImageField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_bar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.userSettingsEditPasswordButton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    public void initViews() {
        createBarLabel = findViewById(R.id.createBarLabel);
        createBarPicLabel = findViewById(R.id.createBarPicLabel);
        createBarNameLabel = findViewById(R.id.createBarNameLabel);
        createBarAddressLabel = findViewById(R.id.createBarAddressLabel);
        createBarDescriptionLabel = findViewById(R.id.createBarDescriptionLabel);

        createBarNameField = findViewById(R.id.createBarNameField);
        createBarAddressField = findViewById(R.id.createBarAddressField);
        createBarDescriptionField = findViewById(R.id.createBarDescriptionField);

        createBarImageField = findViewById(R.id.createBarImageField);

        createBarReturnButton = findViewById(R.id.userSettingsReturnButton);
        createBarAddButton = findViewById(R.id.createBarAddButton);

        createBarReturnButton.setOnClickListener(v -> ReturnLanding());
        createBarAddButton.setOnClickListener(v -> AddBar());

    }

    public void ReturnLanding() {
        Intent intent = new Intent(this, BarList.class);
        startActivity(intent);

    }

    public void AddBar() {
        // Please do add bar stuff here


    }


}