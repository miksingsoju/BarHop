package barhop.app.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import barhop.app.R;

import barhop.app.model.Bar;
import barhop.app.model.User;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FavoriteBars extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_bars);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    RecyclerView recyclerView;

    TextView nameView;

    Realm realm;

    User user;

    public void init()
    {
        recyclerView = findViewById(R.id.recyclerView);
        nameView = findViewById(R.id.barName);

        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        // initialize Realm
        realm = Realm.getDefaultInstance();

        // query the things to display
        String userUuid = getIntent().getStringExtra("uuid");
        user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

        // initialize Adapter
        BarAdapter adapter = new BarAdapter(this, user.getUuid(), user.getFavoriteBars(), true);
        recyclerView.setAdapter(adapter);
    }

}