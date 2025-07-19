package barhop.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import barhop.app.R;
import barhop.app.model.Bar;
import barhop.app.model.Comment;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class BarDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bar_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parentConstraint), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        initCommentGrid();
    }
    Realm realm;
    Bar bar;
    Button editBarButton, moreButton;

    ImageButton backButton;
    CardView editContainer;
    TextView barName, barAddress, barDescription;
    RecyclerView recyclerView;
    ImageView barImage;

    SharedPreferences auth;


    public void init() {
        realm = Realm.getDefaultInstance();
        auth = getSharedPreferences("auth", MODE_PRIVATE);

        barName = findViewById(R.id.barName);
        barAddress = findViewById(R.id.barAddress);
        barDescription = findViewById(R.id.barDescription);
        editBarButton = findViewById(R.id.editBarButton1);
        editContainer = findViewById(R.id.editContainer);
        moreButton = findViewById(R.id.moreButton);
        barImage = findViewById(R.id.barImage);
        backButton = findViewById(R.id.backButton);

        String barUUID = getIntent().getStringExtra("barUUID");
        bar = realm.where(Bar.class).equalTo("uuid",barUUID).findFirst();

        barName.setText(bar.getName());
        barAddress.setText(bar.getLocation());
        barDescription.setText(bar.getDescription());

        File cacheDir = this.getExternalCacheDir();
        File barPhoto = new File(cacheDir, bar.getUuid()+".jpeg");
        if (barPhoto.exists())
        {
            // this will put the image saved to the file system to the imageview
            Picasso.get()
                    .load(barPhoto)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(barImage);
        }

        String userUUID = auth.getString("uuid", "");
        if (userUUID.equals(bar.getOwner().getUuid())) {
            editContainer.setVisibility(View.VISIBLE);
            editBarButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditBar.class);
                intent.putExtra("barUUID", bar.getUuid());
                startActivity(intent);
            });
        }

        moreButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putExtra("barUUID", bar.getUuid());
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {
            finish();
        });
    }

    public void initCommentGrid() {
        recyclerView = findViewById(R.id.commentsGrid);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        // query the things to display
        RealmResults<Comment> list = realm
                .where(Comment.class)
                .equalTo("bar.uuid", bar.getUuid())
                .sort("timestamp", Sort.DESCENDING)
                .limit(9)
                .findAll();

        // initialize Adapter
        CommentGrid adapter = new CommentGrid(this, bar.getUuid(), list, true);
        recyclerView.setAdapter(adapter);
    }
}