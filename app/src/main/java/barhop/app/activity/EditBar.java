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
import android.widget.ImageView;
import android.widget.Toast;


import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import barhop.app.R;
import barhop.app.model.Bar;
import barhop.app.model.User;
import io.realm.Realm;

public class EditBar extends AppCompatActivity {

    Button editBarReturnButton, editBarAddButton;
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

        editBarReturnButton = findViewById(R.id.userSettingsReturnButton);
        editBarAddButton = findViewById(R.id.createBarAddButton);

        editBarReturnButton.setOnClickListener(v -> returnLanding());
        editBarAddButton.setOnClickListener(v -> addBar());

        editBarNameField.setText(barToEdit.getName());
        editBarAddressField.setText(barToEdit.getDescription());
        editBarDescriptionField.setText(barToEdit.getDescription());
        editBarImageField.setOnClickListener(view -> takePhoto());

        File imageFile = new File(getExternalCacheDir(), barToEdit.getUuid() + ".jpeg");
        if (imageFile.exists()) {
            Picasso.get()
                    .load(imageFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(editBarImageField);
        }
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

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    public void takePhoto()
    {
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    // SINCE WE USE startForResult(), code will trigger this once the next screen calls finish()
    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                // receieve the raw JPEG data from ImageActivity
                // this can be saved to a file or save elsewhere like Realm or online
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    // save rawImage to file
                    File savedImage = saveFile(jpeg, barToEdit.getUuid()+".jpeg");  // WHERE TO SAVE

                    // load file to the image view via picasso
                    refreshImageView(editBarImageField, savedImage);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private File saveFile(byte[] jpeg, String filename) throws IOException
    {
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, filename);


        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }

    private void refreshImageView(ImageView imageView, File savedImage) {
        // this will put the image saved to the file system to the imageview
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }

}