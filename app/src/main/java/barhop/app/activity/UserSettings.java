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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import barhop.app.R;
import barhop.app.model.User;
import io.realm.Realm;

public class UserSettings extends AppCompatActivity {

    TextView userSettingsLabel, userSettingsPicLabel, userSettingsConfirmLabel;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parentConstraint), (v, insets) -> {
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

        userSettingsConfirmLabel = findViewById(R.id.userSettingsConfirmLabel);
        userSettingsPicLabel = findViewById(R.id.userSettingsPicLabel);
        userSettingsLabel = findViewById(R.id.userSettingsLabel);
        userSettingsDisplayName = findViewById(R.id.userSettingsDisplayName);
        userSettingsPasswordField = findViewById(R.id.userSettingsPasswordField);

        userSettingsImage = findViewById(R.id.userSettingsImage);

        userSettingsReturnButton = findViewById(R.id.userSettingsReturnButton);
        userEditButton = findViewById(R.id.userEditButton);

        userSettingsImage.setOnClickListener(v -> takePhoto());
        userSettingsReturnButton.setOnClickListener(v -> ReturnHome());
        userEditButton.setOnClickListener(v -> EditCreds());

        File imageFile = new File(getExternalCacheDir(), user.getUuid() + ".jpeg");
        if (imageFile.exists()) {
            Picasso.get()
                    .load(imageFile)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(userSettingsImage);
        }


    }

    public void ReturnHome() {
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

        } catch (Exception e) {
            Toast.makeText(this, "Error changing profile details", Toast.LENGTH_LONG).show();
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
                    File savedImage = saveFile(jpeg, user.getUuid()+".jpeg");  // WHERE TO SAVE

                    // load file to the image view via picasso
                    refreshImageView(userSettingsImage, savedImage);
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