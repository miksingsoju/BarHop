package barhop.app.activity;

import android.content.SharedPreferences;
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
import barhop.app.model.Like;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.faveBarLabel), (v, insets) -> {
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

    SharedPreferences auth;

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
        auth = getSharedPreferences("auth",MODE_PRIVATE);

        // query the things to display
        String userUuid = auth.getString("uuid","");
        user = realm.where(User.class).equalTo("uuid", userUuid).findFirst();

        RealmResults<Like>  likes = user.getLikes();

        // 3. Extract barUUIDs
        String[] barUUIDs = new String[likes.size()];
        for (int i = 0; i < likes.size(); i++) {
            barUUIDs[i] = likes.get(i).getBar().getUuid(); // getBar() returns barUUID
        }

        // 4. Query Bars with matching UUIDs (this returns RealmResults<Bar> ✅)
        RealmResults<Bar> favoriteBars = realm.where(Bar.class)
                .in("uuid", barUUIDs)
                .findAll();
        // initialize Adapter
        BarAdapter adapter = new BarAdapter(this, user.getUuid(), favoriteBars, true);
        recyclerView.setAdapter(adapter);
    }

}