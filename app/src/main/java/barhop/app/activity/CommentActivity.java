package barhop.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
        final int bottomBarTopPad = postsBottomBar.getContentPaddingTop();

        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            postsBottomBar.setContentPadding(
                    postsBottomBar.getContentPaddingLeft(),0,
                    postsBottomBar.getContentPaddingRight(), postsBottomBar.getContentPaddingBottom()
            );
            addPostButton.setText("Close Menu");
        } else {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            postsBottomBar.setContentPadding(
                    postsBottomBar.getContentPaddingLeft(), bottomBarTopPad,
                    postsBottomBar.getContentPaddingRight(), postsBottomBar.getContentPaddingBottom()
            );
            addPostButton.setText("Share your Experience");
        }
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 5);;
    }

    public void openGallery() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        startActivityForResult(intent, 5);;
    }
}