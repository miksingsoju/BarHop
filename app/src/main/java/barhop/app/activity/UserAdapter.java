package barhop.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import barhop.app.model.Bar;
import barhop.app.model.User;
import barhop.app.model.Like;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import barhop.app.R;
import io.realm.RealmResults;

public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, password, userType;
        ImageView photo;

        ImageButton edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            password = itemView.findViewById(R.id.password);
            userType = itemView.findViewById(R.id.userType);
            photo = itemView.findViewById(R.id.photo);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
    Activity activity;
    Realm realm;

    public UserAdapter(Activity activity, @Nullable OrderedRealmCollection<User> data, boolean autoUpdate) {
        super(data, autoUpdate);

        // THIS IS TYPICALLY THE ACTIVITY YOUR RECYCLERVIEW IS IN
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.user_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        realm = Realm.getDefaultInstance();
        User user = getItem(position);
        if (user == null) return;

        holder.name.setText("UN: "+ user.getDisplayName());
        holder.password.setText("PW: "+ user.getPassword());

        if (user.isAdmin()){
            holder.userType.setText("BAR OWNER");
        }else {
            holder.userType.setText("BAR HOPPER");
        }

        holder.delete.setOnClickListener(view -> {
            if (user.isValid()) {
                realm.beginTransaction();
                    // Delete bars owned by this user, if any
                    RealmResults<Bar> ownedBars = user.getBarsOwned();
                    if (ownedBars != null && !ownedBars.isEmpty()) {
                        ownedBars.deleteAllFromRealm();
                    }

                    // Delete the user
                    user.deleteFromRealm();
                realm.commitTransaction();

            }
        });

        holder.edit.setOnClickListener(view -> {
            Intent intent = new Intent(activity,EditUser.class);
            intent.putExtra("uuid", user.getUuid());
            activity.startActivity(intent);
        });

        // if you have more add here
        File cacheDir = activity.getExternalCacheDir();
        File photo = new File(cacheDir, user.getUuid()+".jpeg");
        if (photo.exists())
        {
            // this will put the image saved to the file system to the imageview
            Picasso.get()
                    .load(photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.photo);
        }
    }

}
