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

import java.text.SimpleDateFormat;

import barhop.app.model.Bar;
import barhop.app.model.Comment;
import barhop.app.model.User;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

import barhop.app.R;

public class CommentAdapter extends RealmRecyclerViewAdapter<Comment, CommentAdapter.ViewHolder> {
    TextView postUser, postUsername, postCaption, postUploadDate;
    ImageView postImage, userPfp;

    Comment comment;

    Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postUser = itemView.findViewById(R.id.postUser);
            postUsername = itemView.findViewById(R.id.postUsername);
            postCaption = itemView.findViewById(R.id.postCaption);
            postImage = itemView.findViewById(R.id.postImage);
            postUploadDate = itemView.findViewById(R.id.postUploadDate);
        }
    }

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

        SimpleDateFormat formatter = new SimpleDateFormat("dd mon yyyy");
        postUploadDate.setText(formatter.format(comment.getTimestamp()));
    }
}
