package barhop.app.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rememberCheckBox), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    Realm realm;
    Bar bar;
    TextView barName, barAddress, barDescription;
    public void init(){
        realm = Realm.getDefaultInstance();
        barName = findViewById(R.id.barName);
        barAddress = findViewById(R.id.barAddress);
        barDescription = findViewById(R.id.barDescription);

        String barUUID = getIntent().getStringExtra("barUUID");
        bar = realm.where(Bar.class).equalTo("uuid",barUUID).findFirst();

        barName.setText(bar.getName());
        barAddress.setText(bar.getLocation());
        barDescription.setText(bar.getDescription());
    }
}