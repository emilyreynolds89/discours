package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    static Context context;
    List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsernameComment;
        TextView tvComment;
        TextView tvTimeComment;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUsernameComment = itemView.findViewById(R.id.tvUsernameComment);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
        }

        public void bind(Comment comment) {
            tvUsernameComment.setText("@" + comment.getUser().getUsername());
            tvComment.setText(comment.getText());
            tvTimeComment.setText(comment.getRelativeTime());
        }
    }
}
