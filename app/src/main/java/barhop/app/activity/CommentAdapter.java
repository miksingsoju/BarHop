package barhop.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import barhop.app.model.Bar;
import barhop.app.model.Comment;
import barhop.app.model.User;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import barhop.app.R;

public class CommentAdapter extends RealmRecyclerViewAdapter<Comment, CommentAdapter.ViewHolder> {

    TextView postUser, postUsername, postCaption, postUploadDate;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage, userPfp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postUser = itemView.findViewById(R.id.postUser);
            postUsername = itemView.findViewById(R.id.postUsername);
            postCaption = itemView.findViewById(R.id.postCaption);
            postImage = itemView.findViewById(R.id.postImage);
            postUploadDate = itemView.findViewById(R.id.postUploadDate);
        }
    }
    Comment comment;
    Activity activity;


    public CommentAdapter(Activity activity, @Nullable OrderedRealmCollection<Comment> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = activity.getLayoutInflater().inflate(R.layout.comment_card, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        comment = getItem(position);

        postUser.setText(comment.getCommenter().getDisplayName());
        postUsername.setText(comment.getCommenter().getDisplayName());
        postCaption.setText(comment.getText());

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        postUploadDate.setText(formatter.format(comment.getTimestamp()));

        File cacheDir = activity.getExternalCacheDir();
        File commentPhoto = new File(cacheDir, comment.getUuid()+".jpeg");
        if (commentPhoto.exists())
        {
            // this will put the image saved to the file system to the imageview
            Picasso.get()
                    .load(commentPhoto)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.postImage);
        }
    }
}
