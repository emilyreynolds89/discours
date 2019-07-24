package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
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

import butterknife.BindView;
import butterknife.ButterKnife;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

        @BindView(R.id.tvDescriptionNotif) TextView tvDescriptionNotif;
        @BindView(R.id.ivImageNotif) ImageView ivImageNotif;
        @BindView(R.id.ivProfileImageNotif) ImageView ivProfileImageNotif;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Notification notification) {
            User sender = notification.getSendUser();
            String username = sender.getUsername();
            String typeText = notification.getTypeText();
            tvDescriptionNotif.setText("@" + username + notification.notificationText(notification.getType()) + ": " + typeText);

            try {
                Share share = getShare(notification.getShare().getObjectId());
                ParseFile image = share.getArticle().getImage();
                if (image != null ) {
                    Glide.with(context).load(image.getUrl()).into(ivImageNotif);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                User user = getUser(notification.getSendUser().getObjectId());
                ParseFile image = user.getProfileImage();
                if (image != null) {
                    Glide.with(context).load(image.getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImageNotif);
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

    public User getUser(String userId) throws ParseException {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.include(User.KEY_USERNAME);
        query.include(User.KEY_PROFILEIMAGE);
        query.include(User.KEY_FULLNAME);

        query.whereEqualTo("objectId", userId);
        return query.getFirst();
    }

}
