package barhop.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    Button addPostButton;
    ImageButton commentsBackButton;
    RecyclerView commentRecycler;
    ConstraintLayout noTweetsContainer;
    CardView postsBottomBar;

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
        commentsBackButton = findViewById(R.id.commentsBackButton);
        postsBottomBar = findViewById(R.id.postsBottomBar);
        noTweetsContainer = findViewById(R.id.noTweetsContainer);

        postsHeaderBar.setText(bar.getName());

        if (userUUID.isEmpty() || userUUID.equals(bar.getOwner().getUuid())) {
            postsBottomBar.setVisibility(View.GONE);
        } else {
            addPostButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddComment.class); // create comment
                intent.putExtra("barUUID", barUUID);
                startActivity(intent);
            });
        }

        commentsBackButton.setOnClickListener(v -> {
            finish();
        });

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
}