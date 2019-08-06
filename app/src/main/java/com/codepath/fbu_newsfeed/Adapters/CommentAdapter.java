package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Helpers.CommentReactionHelper;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.CommentReaction;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> comments;
    private Share share;

    public CommentAdapter(Context context, List<Comment> comments, Share share) {

        this.context = context;
        this.comments = comments;
        this.share = share;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        final Comment comment = comments.get(position);
        holder.bind(comment);

        holder.tvUsernameComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("user_id", comment.getUser().getObjectId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvUsernameComment) TextView tvUsernameComment;
        @BindView(R.id.tvComment) TextView tvComment;
        @BindView(R.id.tvTimeComment) TextView tvTimeComment;
        @BindView(R.id.tvClapCount) TextView tvClapCount;
        @BindView(R.id.ibClap) ImageButton ibClap;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ibClap.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Comment comment = comments.get(position);

                switch (view.getId()) {
                    case R.id.ibClap:
                        updateCommentReactionText(comment, (User) ParseUser.getCurrentUser(), tvClapCount, ibClap);
                        break;
                }
            }
        }

        void bind(Comment comment) {
            tvUsernameComment.setText("@" + comment.getUser().getUsername());
            tvComment.setText(comment.getText());
            tvTimeComment.setText(comment.getRelativeTime());

            tvClapCount.setText(Integer.toString(comment.getClapCount()));
            ibClap.setSelected(false);
            CommentReaction commentReaction = CommentReactionHelper.getCommentReaction(ParseUser.getCurrentUser(), comment);
            if(commentReaction != null) {
                ibClap.setSelected(true);
            }
        }

    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    private void updateCommentReactionText(Comment comment, User currentUser, TextView textView, ImageButton imageButton) {
        CommentReaction commentReaction = CommentReactionHelper.getCommentReaction(currentUser, comment);
        int count;
        if (commentReaction != null) {
            count = CommentReactionHelper.destroyCommentReaction(commentReaction, comment);
            imageButton.setSelected(false);
            deleteNotification(share, comment, comment.getText());
        } else {
            count = CommentReactionHelper.createCommentReaction(comment);
            imageButton.setSelected(true);
            createNotification(share, comment, comment.getText());
        }
        textView.setText(Integer.toString(count));
    }

    private void createNotification(Share share, Comment comment, String typeText) {
        User commentedUser = (User) comment.getUser();
        if (ParseUser.getCurrentUser().getObjectId().equals(comment.getUser().getObjectId())) { return; }
        Notification notification = new Notification(Notification.COMMENT_REACTION, (User) ParseUser.getCurrentUser(), commentedUser, share, typeText);
        notification.saveInBackground();
    }

    private void deleteNotification(Share share, Comment comment, String typeText) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);

        query.whereEqualTo(Notification.KEY_TYPE, Notification.COMMENT_REACTION);
        query.whereEqualTo(Notification.KEY_TYPE_TEXT, typeText);
        query.whereEqualTo(Notification.KEY_SEND_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Notification.KEY_RECEIVE_USER, comment.getUser());

        Notification notification;

        try {
            List<Notification> result = query.find();
            if (result.size() > 0 ) {
                notification = result.get(0);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        notification.deleteInBackground();
    }

}
