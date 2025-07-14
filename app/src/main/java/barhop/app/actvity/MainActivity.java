package barhop.app.actvity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import barhop.app.R;

import android.content.Intent;
import android.widget.Button;

/**
 * This is the first thing the user sees when opening the app (even without logging in)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    Button adminRegisterButton, userRegisterButton;

    /**
     * This method initializes the views needed.
     */
    public void init(){
        adminRegisterButton = findViewById(R.id.adminRegisterButton);
        userRegisterButton = findViewById(R.id.userRegisterButton);

        adminRegisterButton.setOnClickListener(view -> register("ADMIN"));
        userRegisterButton.setOnClickListener(view -> register("USER"));
    }

    /**
     * This method takes 2 options, if destination is ADMIN, it will take you to the Admin Registration. If destination is USER,
     * it will take you to the User Registration
     * @param destination decides whether it is User or Admin
     */
    public void register(String destination) {
        if (destination.equals("USER")){
            Intent intent = new Intent(this, UserRegistration.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, AdminRegistration.class);
            startActivity(intent);
        }
    }


}