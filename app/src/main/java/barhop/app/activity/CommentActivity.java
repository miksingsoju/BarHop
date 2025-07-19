package barhop.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import barhop.app.R;
import barhop.app.model.Bar;
import barhop.app.model.Comment;
import io.realm.Realm;
import io.realm.RealmResults;

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_comment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parentConstraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    String userUUID, barUUID;
    TextView postsHeaderBar;
    Button addPostButton, cameraButton, galleryButton;
    ImageButton commentsBackButton;
    RecyclerView commentRecycler;
    ConstraintLayout noTweetsContainer, mediaDrawerContainer;
    CardView postsBottomBar;
    BottomSheetBehavior<View> behavior;

    Bar bar;

    Realm realm;
    SharedPreferences auth;

    public void init() {
        realm = Realm.getDefaultInstance();
        auth = getSharedPreferences("auth", MODE_PRIVATE);
        userUUID = auth.getString("uuid", "");
        barUUID = getIntent().getStringExtra("barUUID");

        bar = realm.where(Bar.class).equalTo("uuid", barUUID).findFirst();

        postsHeaderBar = findViewById(R.id.postsHeaderBar);
        addPostButton = findViewById(R.id.addPostButton);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        commentsBackButton = findViewById(R.id.commentsBackButton);
        postsBottomBar = findViewById(R.id.postsBottomBar);
        noTweetsContainer = findViewById(R.id.noTweetsContainer);
        mediaDrawerContainer = findViewById(R.id.mediaDrawerContainer);

        behavior = BottomSheetBehavior.from(mediaDrawerContainer);
        behavior.setHideable(true);
        behavior.setDraggable(true);
        behavior.setSkipCollapsed(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        postsHeaderBar.setText(bar.getName());

        if (userUUID.isEmpty() || userUUID.equals(bar.getOwner().getUuid())) {
            postsBottomBar.setVisibility(View.GONE);
        }
        commentsBackButton.setOnClickListener(v -> finish());
        addPostButton.setOnClickListener(v -> addPostButtonHandler());
        cameraButton.setOnClickListener(v -> openCamera());
        galleryButton.setOnClickListener(v -> openGallery());

        initRecyclerView();
    }

    public void initRecyclerView() {
        commentRecycler = findViewById(R.id.commentRecycler);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        commentRecycler.setLayoutManager(mLayoutManager);

        RealmResults<Comment> comments = realm
                .where(Comment.class)
                .equalTo("bar.uuid", barUUID)
                .findAll();

        if (!comments.isEmpty()) {
            noTweetsContainer.setVisibility(View.GONE);
        }

        CommentAdapter adapter = new CommentAdapter(this, comments, true);
        commentRecycler.setAdapter(adapter);
    }

    public void addPostButtonHandler() {

        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

    }

    public void openCamera() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);

    }

    public void openGallery() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3) recreate();

        if (resultCode == RESULT_OK && data != null) {
            Uri image = null;
            
            if (requestCode == 2) {
                image = data.getData();
            } else if (requestCode == 1) {
                Bitmap imgBitmap = (Bitmap) data.getExtras().get("data");
                image = getImageUri(imgBitmap);
            } else if (requestCode == 3) {
                Log.d("memo", "recreated1");
                recreate();
            }

            Intent intent = new Intent(this, AddComment.class);
            intent.putExtra("image", image.toString());
            intent.putExtra("barUUID", barUUID);
            startActivityForResult(intent, 3);
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        String imgTitle = UUID.randomUUID().toString();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), inImage, imgTitle, null);
        return Uri.parse(path);
    }
}