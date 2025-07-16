package barhop.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.content.Intent;
import android.widget.Toast;


import barhop.app.R;
import barhop.app.model.Bar;
import barhop.app.model.User;
import io.realm.Realm;

public class CreateBar extends AppCompatActivity {

    Button editBarReturnButton, createBarAddButton;
    EditText createBarNameField, createBarAddressField, createBarDescriptionField;
    ImageButton createBarImageField;

    Realm realm;

    Bar newBar;

    User owner;

    SharedPreferences auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_bar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CreateBarLabel), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    public void initViews() {
        realm = Realm.getDefaultInstance();
        auth = getSharedPreferences("auth",MODE_PRIVATE);
        newBar = new Bar();

        String ownerUUID = auth.getString("uuid","");

        owner = realm.where(User.class).equalTo("uuid",ownerUUID).findFirst();

        createBarNameField = findViewById(R.id.editBarNameField);
        createBarAddressField = findViewById(R.id.editBarAddressField);
        createBarDescriptionField = findViewById(R.id.editDescriptionField);

        createBarImageField = findViewById(R.id.editBarImageField);

        editBarReturnButton = findViewById(R.id.editBarReturnButton);
        createBarAddButton = findViewById(R.id.editEditButton);

        editBarReturnButton.setOnClickListener(v -> returnLanding());
        createBarAddButton.setOnClickListener(v -> addBar());
    }

    public void returnLanding() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void addBar() {
        String name = createBarNameField.getText().toString().trim();
        String address = createBarAddressField.getText().toString();
        String description = createBarDescriptionField.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Bar name must not be blank", Toast.LENGTH_LONG).show();
            return;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "Bar must have a location", Toast.LENGTH_LONG).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Bar must have a description", Toast.LENGTH_LONG).show();
            return;
        }
        // Check if user already exists
        Bar existingBar = realm.where(Bar.class).equalTo("name", name).findFirst();
        if (existingBar != null) {
            Toast.makeText(this, "This bar name is already taken.", Toast.LENGTH_LONG).show();
            return;
        }

        newBar.setName(name);
        newBar.setLocation(address);
        newBar.setDescription(description);
        newBar.setOwner(owner);

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(newBar);
            realm.commitTransaction();

            long count = owner.getBarsOwned().size(); // this returns the number of bars the current bar owner has created

            Toast.makeText(this, "Bars created: " + count, Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving bar", Toast.LENGTH_LONG).show();
        }
    }


}