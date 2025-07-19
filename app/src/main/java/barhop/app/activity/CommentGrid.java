package barhop.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import barhop.app.R;
import barhop.app.model.Comment;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CommentGrid extends RealmRecyclerViewAdapter<Comment, CommentGrid.ViewHolder> {

    Activity activity;
    Comment comment;
    String barUUID;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
        }
    }


    public CommentGrid(Activity activity, String barUUID, @Nullable OrderedRealmCollection<Comment> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = activity;
        this.barUUID = barUUID;
    }

    @NonNull
    @Override
    public CommentGrid.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.comment_photo, parent, false);
        return new CommentGrid.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentGrid.ViewHolder holder, int position) {
        comment = getItem(position);

        File cacheDir = activity.getExternalCacheDir();
        File commentPhoto = new File(cacheDir, comment.getUuid() + ".jpeg");
        if (commentPhoto.exists()) {
            Picasso.get()
                    .load(commentPhoto)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.postImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, CommentActivity.class);
            intent.putExtra("barUUID", barUUID);
            activity.startActivity(intent);
        });
    }
}