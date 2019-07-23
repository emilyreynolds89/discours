package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.DetailActivity;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.Serializable;
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
        final Notification notification = notifications.get(position);
        Share share = null;
        try {
            share = getShare(notification.getShare().getObjectId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.bind(notification);

        final Share finalShare = share;
        holder.btnViewNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalShare != null) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("share", (Serializable) finalShare);
                    context.startActivity(intent);
                }
            }
        });
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
            tvDescriptionNotif.setText("@" + username + notification.notificationText(notification.getType()));

            try {
                Share share = getShare(notification.getShare().getObjectId());
                ParseFile image = share.getArticle().getImage();
                if (image != null ) {
                    Glide.with(context).load(image.getUrl()).into(ivImageNotif);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    public void clear() {
        notifications.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Notification> newNotifications) {
        notifications.addAll(newNotifications);
    }

    public Share getShare(String shareId) throws ParseException {
        ParseQuery<Share> query = ParseQuery.getQuery(Share.class);
        query.include(Share.KEY_ARTICLE);
        query.whereEqualTo("objectId", shareId);
        return query.getFirst();
    }

}
