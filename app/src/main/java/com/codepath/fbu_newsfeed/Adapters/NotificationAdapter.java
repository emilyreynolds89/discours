package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseFile;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    static Context context;
    List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsernameNotif;
        TextView tvDescriptionNotif;
        ImageView ivImageNotif;
        Button btnViewNotif;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsernameNotif = itemView.findViewById(R.id.tvUsernameNotif);
            tvDescriptionNotif = itemView.findViewById(R.id.tvDescriptionNotif);
            ivImageNotif = itemView.findViewById(R.id.ivImageNotif);
            btnViewNotif = itemView.findViewById(R.id.btnViewNotif);
        }

        public void bind(Notification notification) {
            User sender = notification.getSendUser();
            String username = sender.getUsername();
            tvUsernameNotif.setText(username);
            tvDescriptionNotif.setText(username + notification.notificationText(notification.getType()));

            ParseFile image = notification.getShare().getArticle().getImage();
            if (image != null ) {
                Glide.with(context).load(image.getUrl()).into(ivImageNotif);
            }
        }
    }

    public void clear() {
        notifications.clear();
        notifyDataSetChanged();
    }
}
