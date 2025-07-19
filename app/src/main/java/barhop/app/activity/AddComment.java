package barhop.app.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import barhop.app.R;
import barhop.app.model.Bar;
import barhop.app.model.Comment;
import barhop.app.model.User;
import io.realm.Realm;

public class AddComment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    SharedPreferences auth;
    Realm realm;

    User user;
    Bar bar;
    Comment comment;

    ImageButton commentPhoto,returnButton;
    CropImageView cropImageView;
    EditText commentText;
    Button createComment;
    String userUuid, barUuid;

    public void init() {
        realm = Realm.getDefaultInstance();
        auth = getSharedPreferences("auth",MODE_PRIVATE);

        userUuid = auth.getString("uuid","");
        user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

        barUuid = getIntent().getStringExtra("barUUID");
        bar = realm.where(Bar.class).equalTo("uuid", barUuid).findFirst();

        commentPhoto = findViewById(R.id.commentPhoto);
        returnButton = findViewById(R.id.returnButton);
        commentText = findViewById(R.id.commentText);
        createComment = findViewById(R.id.createComment);
        cropImageView = findViewById(R.id.cropImageView);
        
//        commentPhoto.setOnClickListener(view -> takePhoto());
        createComment.setOnClickListener(view -> createComment());
        returnButton.setOnClickListener(view -> finish());


        String imageUri = getIntent().getStringExtra("image");
        if (!imageUri.isEmpty()) {
            Uri uri = Uri.parse(imageUri);

            cropImageView.setImageUriAsync(uri);
        }
    }

    public void createComment() {
        String caption = commentText.getText().toString();

        if (caption.isEmpty()) {
            Toast.makeText(this, "Caption must not be blank", Toast.LENGTH_LONG).show();
            return;
        }

        comment = new Comment();
        comment.setCommenter(user);
        comment.setBar(bar);
        comment.setTimestamp(new Date());
        comment.setText(caption);

        try {
            Bitmap cropped = cropImageView.getCroppedImage();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cropped.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            File savedImage = saveFile(byteArray, comment.getUuid()+".jpeg");
            refreshImageView(commentPhoto, savedImage);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(comment);
            realm.commitTransaction();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving bar", Toast.LENGTH_LONG).show();
        }
    }
    
    private File saveFile(byte[] jpeg, String filename) throws IOException
    {
        File getImageDir = getExternalCacheDir();
        File savedImage = new File(getImageDir, filename);
        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }

    private void refreshImageView(ImageView imageView, File savedImage) {
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }
}