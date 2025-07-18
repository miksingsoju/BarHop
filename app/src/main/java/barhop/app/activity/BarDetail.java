package barhop.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import barhop.app.R;
import barhop.app.model.Bar;
import io.realm.Realm;

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
    }
    Realm realm;
    Bar bar;
    Button editBarButton, moreButton;
    CardView editContainer;
    TextView barName, barAddress, barDescription;
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

        String barUUID = getIntent().getStringExtra("barUUID");
        bar = realm.where(Bar.class).equalTo("uuid",barUUID).findFirst();

        barName.setText(bar.getName());
        barAddress.setText(bar.getLocation());
        barDescription.setText(bar.getDescription());

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
    }
}