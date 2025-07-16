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

public class EditBar extends AppCompatActivity {

    Button editBarReturnBtn, editBarAddButton;
    EditText editBarNameField, editBarAddressField, editBarDescriptionField;
    ImageButton editBarImageField;

    Realm realm;

    Bar barToEdit;

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
        String barUUID = getIntent().getStringExtra("barUUID");
        barToEdit = realm.where(Bar.class).equalTo("uuid",barUUID).findFirst();

        String ownerUUID = auth.getString("uuid","");


        owner = realm.where(User.class).equalTo("uuid",ownerUUID).findFirst();

        editBarNameField = findViewById(R.id.createBarNameField);
        editBarAddressField = findViewById(R.id.createBarAddressField);
        editBarDescriptionField = findViewById(R.id.createBarDescriptionField);

        editBarImageField = findViewById(R.id.createBarImageField);

        editBarReturnBtn = findViewById(R.id.editBarReturnBtn);
        editBarAddButton = findViewById(R.id.createBarAddButton);

        editBarReturnBtn.setOnClickListener(v -> returnLanding());
        editBarAddButton.setOnClickListener(v -> addBar());

        editBarNameField.setText(barToEdit.getName());
        editBarAddressField.setText(barToEdit.getDescription());
        editBarDescriptionField.setText(barToEdit.getDescription());

    }

    public void returnLanding() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addBar() {
        String name = editBarNameField.getText().toString().trim();
        String address = editBarAddressField.getText().toString();
        String description = editBarDescriptionField.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Bar name must not be blank", Toast.LENGTH_LONG).show();
            return;
        }



        try {
            realm.beginTransaction();
            
            barToEdit.setName(name);
            barToEdit.setLocation(address);
            barToEdit.setDescription(description);

            realm.commitTransaction();

            Toast.makeText(this, "Editing " + barToEdit.getName() + " was edited successfully." , Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving bar", Toast.LENGTH_LONG).show();
        }
    }

}