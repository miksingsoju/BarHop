package barhop.app.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import barhop.app.R;
import barhop.app.model.User;
import io.realm.Realm;
import io.realm.RealmResults;

public class AdminPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initBarList();
    }
    RecyclerView recyclerView;
    Realm realm;

    public void initBarList()
    {
        realm = Realm.getDefaultInstance();
        recyclerView = findViewById(R.id.adminsList);

        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        // query the things to display
        RealmResults<User> list = realm.where(User.class).findAll();

        // initialize Adapter
        UserAdapter adapter = new UserAdapter(this, list, true);
        recyclerView.setAdapter(adapter);
    }
}