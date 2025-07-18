package barhop.app.activity;

import android.app.Activity;
import android.content.Intent;
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

public class BarAdapter extends RealmRecyclerViewAdapter<Bar, BarAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView barName, barAddress, barLikes;
        ImageButton likeButton, editBarButton;

        ImageView barPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            barName = itemView.findViewById(R.id.barName);
            barAddress = itemView.findViewById(R.id.barAddress);
            barLikes = itemView.findViewById(R.id.barLikes);
            likeButton = itemView.findViewById(R.id.likeButton);
            barPhoto = itemView.findViewById(R.id.barPhoto);
        }
    }
    Activity activity;
    String userUUID;
    Realm realm;

    public BarAdapter(Activity activity, String userUUID, @Nullable OrderedRealmCollection<Bar> data, boolean autoUpdate) {
        super(data, autoUpdate);

        // THIS IS TYPICALLY THE ACTIVITY YOUR RECYCLERVIEW IS IN
        this.activity = activity;
        this.userUUID = userUUID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.bar_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bar bar = getItem(position);
        if (bar == null) return;

        holder.barName.setText(bar.getName());
        holder.barAddress.setText(bar.getLocation());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, BarDetail.class);
            intent.putExtra("barUUID", bar.getUuid());
            activity.startActivity(intent);
        });

        // if you have more add here
        File cacheDir = activity.getExternalCacheDir();
        File photo = new File(cacheDir, bar.getUuid()+".jpeg");
        if (photo.exists())
        {
            // this will put the image saved to the file system to the imageview
            Picasso.get()
                    .load(photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.barPhoto);
        }

        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("uuid", userUUID).findFirst();

        int likes = realm
                .where(Like.class)
                .equalTo("barUUID", bar.getUuid())
                .findAll()
                .size();

        Like like = realm
                .where(Like.class)
                .equalTo("userUUID", userUUID)
                .equalTo("barUUID", bar.getUuid())
                .findFirst();

        holder.barName.setText(bar.getName());
        holder.barAddress.setText(bar.getLocation());
        holder.barLikes.setText(String.valueOf(likes) + " Likes");
        holder.likeButton.setImageResource(
                like == null ? R.drawable.ic_heart_line : R.drawable.ic_heart_fill
        );

        holder.likeButton.setOnClickListener(v -> {
            if (like == null) {
                Like newLike = new Like();
                newLike.setUser(userUUID);
                newLike.setBar(bar.getUuid());

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(newLike);
                realm.commitTransaction();
            } else {
                realm.beginTransaction();
                like.deleteFromRealm();
                realm.commitTransaction();
            }
            notifyDataSetChanged();
        });

    }

}
