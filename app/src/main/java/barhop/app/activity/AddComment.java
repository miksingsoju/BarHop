package barhop.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
    User user;

    Bar bar;

    Realm realm;

    ImageButton commentPhoto,returnButton;
    EditText commentText;
    Button createComment;
    
    Comment comment;
    


    public void init(){
        realm = Realm.getDefaultInstance();
        auth = getSharedPreferences("auth",MODE_PRIVATE);
        String userUuid = auth.getString("uuid","");
        user = realm.where(User.class).equalTo("uuid",userUuid).findFirst();
        comment = new Comment();

        String barUuid = getIntent().getStringExtra("barUUID");
        bar = realm.where(Bar.class).equalTo("uuid",barUuid).findFirst();

        commentPhoto = findViewById(R.id.commentPhoto);
        returnButton = findViewById(R.id.returnButton);
        commentText = findViewById(R.id.commentText);
        createComment = findViewById(R.id.createComment);
        
        commentPhoto.setOnClickListener(view -> takePhoto());
        createComment.setOnClickListener(view -> createComment());
        returnButton.setOnClickListener(view -> cancel());
    }
    
    public void createComment(){
        String caption = commentText.getText().toString();

        if (caption.isEmpty()) {
            Toast.makeText(this, "Caption must not be blank", Toast.LENGTH_LONG).show();
            return;
        }

        comment.setCommenter(user);
        comment.setBar(bar);
        comment.setTimestamp(new Date());
        comment.setText(caption);



        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(comment);
            realm.commitTransaction();

            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving bar", Toast.LENGTH_LONG).show();
        }
    }
    
    public void cancel(){
        
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
                    File savedImage = saveFile(jpeg, comment.getUuid()+".jpeg");  // WHERE TO SAVE

                    // load file to the image view via picasso
                    refreshImageView(commentPhoto, savedImage);
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