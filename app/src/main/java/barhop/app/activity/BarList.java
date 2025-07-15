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
import io.realm.Realm;
import io.realm.RealmResults;

public class BarList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bar_list);
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

        String userUuid = getIntent().getStringExtra("uuid");

        // query the things to display
        RealmResults<Bar> list = realm.where(Bar.class).findAll();

        // initialize Adapter
        BarAdapter adapter = new BarAdapter(this,  userUuid, list, true);
        recyclerView.setAdapter(adapter);
    }

}