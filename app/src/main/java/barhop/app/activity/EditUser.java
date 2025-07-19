package barhop.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import barhop.app.R;
import barhop.app.model.User;
import io.realm.Realm;

public class EditUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    Button save, cancel;
    ImageView adminEditPhoto;

    EditText editName, editPassword, confirmPW;
    User user;
    Realm realm;
    public void init(){
        realm = Realm.getDefaultInstance();
        String userUuid = getIntent().getStringExtra("uuid");
        user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

        save = findViewById(R.id.save);
        adminEditPhoto = findViewById(R.id.adminEditPhoto);
        editName = findViewById(R.id.editName);
        editPassword = findViewById(R.id.editPassword);
        confirmPW = findViewById(R.id.confirmPW);
        cancel = findViewById(R.id.cancel);

        editName.setHint(user.getDisplayName());
        editPassword.setHint(user.getPassword());

        save.setOnClickListener(view -> save());
        cancel.setOnClickListener(view -> cancel());
        adminEditPhoto.setOnClickListener(view -> takePhoto());

        File cacheDir = this.getExternalCacheDir();
        File photo = new File(cacheDir, user.getUuid()+".jpeg");
        if (photo.exists())
        {
            // this will put the image saved to the file system to the imageview
            Picasso.get()
                    .load(photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(adminEditPhoto);
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
                    refreshImageView(adminEditPhoto, savedImage);
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
                .into(adminEditPhoto);
    }

    public void cancel(){
        finish();
    }

    public void save() {
        String name = editName.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = confirmPW.getText().toString();

        User existingUser = realm.where(User.class).equalTo("displayName", name).findFirst();
        if (existingUser != null) {
            Toast.makeText(this, "Username is already taken.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Confirm password does not match.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            realm.beginTransaction();
             // Update existing user
                if (!name.isEmpty()){
                    user.setDisplayName(name);
                }
                if (!password.isEmpty()){
                    user.setPassword(password);
                }
            realm.commitTransaction();

            Toast t = Toast.makeText(this, "User saved", Toast.LENGTH_LONG);
            t.show();
            finish();
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            Toast.makeText(this, "Error saving user", Toast.LENGTH_LONG).show();
        }
    }

}