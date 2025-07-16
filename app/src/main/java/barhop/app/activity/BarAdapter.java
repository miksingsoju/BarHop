package barhop.app.activity;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import barhop.app.model.Like;
import barhop.app.model.User;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import barhop.app.model.Bar;

import barhop.app.R;

public class BarAdapter extends RealmRecyclerViewAdapter<Bar, BarAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView barName, barAddress, barLikes, barUUID;
        ImageButton likeButton;

        ImageButton addToFavoriteButton;

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

        User user = realm.where(User.class).equalTo("uuid", userUUID).findFirst();
        if (user == null) return;

        holder.barName.setText(bar.getName());
        holder.barAddress.setText(bar.getLocation());

        realm = Realm.getDefaultInstance();
        // Boolean isFavorite = (user.getFavoriteBars().contains(bar));

        holder.likeButton.setOnClickListener(v -> {
            Like like = new Like();
            like.setUser(user);
            like.setBar(bar);
        });


        // initial
//        holder.addToFavoriteButton.setImageResource(
//                isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
//        );

        // after being clicked
//        holder.addToFavoriteButton.setOnClickListener(v -> {
//            if (isFavorite){
//                holder.addToFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
//                realm.executeTransaction(r -> {
//                    user.removeFromFavourites(bar);
//                });
//
//            } else {
//                holder.addToFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
//                realm.executeTransaction(r -> {
//                    user.addToFavourites(bar);
//                });
//            }
//        });
    }

}
