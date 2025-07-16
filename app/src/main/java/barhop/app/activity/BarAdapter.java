package barhop.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import barhop.app.model.User;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import barhop.app.model.Bar;

import barhop.app.R;

public class BarAdapter extends RealmRecyclerViewAdapter<Bar, BarAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView barName, barAddress;

        ImageButton addToFavoriteButton, editBarButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            barName = itemView.findViewById(R.id.barName);
            barAddress = itemView.findViewById(R.id.barAddress);
            addToFavoriteButton = itemView.findViewById(R.id.addToFavoriteButton);
            editBarButton = itemView.findViewById(R.id.editBarButton);
        }
    }

    Activity activity;
    String userUUID;

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
        final Bar bar = getItem(position);
        if (bar == null) return;

        holder.barName.setText(bar.getName());
        holder.barAddress.setText(bar.getLocation());

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(activity, "Clicked: " + bar.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, BarDetail.class);
            intent.putExtra("barUUID", bar.getUuid());
            activity.startActivity(intent);
        });

        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).equalTo("uuid", userUUID).findFirst();

        boolean isOwner = bar.getOwner().equals(user);

        if(isOwner){
            holder.editBarButton.setVisibility(View.VISIBLE);
        }else {
            holder.editBarButton.setVisibility(View.GONE);
        }

        holder.editBarButton.setOnClickListener(view -> {
            Toast.makeText(activity, "Clicked: " + bar.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, EditBar.class);
            intent.putExtra("barUUID", bar.getUuid());
            activity.startActivity(intent);
        });

        final boolean[] isFavorite = {false};
        if (user != null && user.getFavoriteBars() != null) {
            isFavorite[0] = user.getFavoriteBars().contains(bar);
        }

        holder.addToFavoriteButton.setImageResource(
                isFavorite[0] ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
        );

        holder.addToFavoriteButton.setOnClickListener(v -> {
            if (isFavorite[0]) {
                holder.addToFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                realm.executeTransaction(r -> user.removeFromFavourites(bar));
                isFavorite[0] = false;
            } else {
                holder.addToFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                realm.executeTransaction(r -> user.addToFavourites(bar));
                isFavorite[0] = true;
            }
        });
    }


}
