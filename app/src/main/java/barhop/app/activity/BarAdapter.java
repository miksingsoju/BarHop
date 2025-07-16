package barhop.app.activity;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.atomic.AtomicBoolean;

import barhop.app.model.Like;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import barhop.app.model.Bar;

import barhop.app.R;

public class BarAdapter extends RealmRecyclerViewAdapter<Bar, BarAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView barName, barAddress, barLikes;
        ImageButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            barName = itemView.findViewById(R.id.barName);
            barAddress = itemView.findViewById(R.id.barAddress);
            barLikes = itemView.findViewById(R.id.barLikes);
            likeButton = itemView.findViewById(R.id.likeButton);
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

        realm = Realm.getDefaultInstance();
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
        holder.barLikes.setText(String.valueOf(likes));
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
//                holder.likeButton.setImageResource(R.drawable.ic_heart_fill);
//                holder.barLikes.setText(String.valueOf(likes + 1));
            } else {
//                holder.likeButton.setImageResource(R.drawable.ic_heart_line);
                realm.beginTransaction();
                like.deleteFromRealm();
                realm.commitTransaction();
//                holder.barLikes.setText(String.valueOf(likes-1));
            }
            notifyDataSetChanged();
        });
    }

}
