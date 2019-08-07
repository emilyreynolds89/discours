package com.codepath.fbu_newsfeed.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.DetailActivity;
import com.codepath.fbu_newsfeed.Fragments.ProfileFragment;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notifications;

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
        Share share =  notification.getShare();

        holder.bind(notification);

        final Share finalShare = share;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalShare != null) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("share", (Serializable) finalShare);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    goToUser(notification.getSendUser());
                }
            }
        });

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                while (motionEvent.isButtonPressed(MotionEvent.ACTION_DOWN)) {
                    view.setSelected(true);
                }
                view.setSelected(false);
                return false;
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
        @BindView(R.id.btnFriendStatus) Button btnFriendStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Notification notification) {
            User sender = notification.getSendUser();
            String username = "<b>@" + sender.getUsername() + "</b>";
            String typeText = notification.getTypeText();
            if (notification.getType().equals(Notification.COMMENT) || notification.getType().equals(Notification.REACTION) || notification.getType().equals(Notification.COMMENT_REACTION)) {
                btnFriendStatus.setVisibility(View.INVISIBLE);
                ivImageNotif.setVisibility(View.VISIBLE);
                tvDescriptionNotif.setText(Html.fromHtml(username + notification.notificationText(notification.getType()) + ": " + typeText, HtmlCompat.FROM_HTML_MODE_LEGACY));
            } else {
                btnFriendStatus.setVisibility(View.VISIBLE);
                ivImageNotif.setVisibility(View.INVISIBLE);
                tvDescriptionNotif.setText(Html.fromHtml(username + notification.notificationText(notification.getType()), HtmlCompat.FROM_HTML_MODE_LEGACY));
                String friendStatus = getFriendStatus(notification.getSendUser(), notification.getReceiveUser());
                if (friendStatus != null) {
                    btnFriendStatus.setText(friendStatus);
                }
            }

            final Share share = notification.getShare();

            if(share != null) {
                share.getArticle().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        Article article = (Article) object;
                        ParseFile image = article.getImage();
                        String imageUrl = article.getImageUrl();
                        if (image != null ) {
                            Glide.with(context).load(image.getUrl()).into(ivImageNotif);
                        } else if (imageUrl != null) {
                            Glide.with(context).load(imageUrl).into(ivImageNotif);
                        }
                    }
                });
            } else {
                ivImageNotif.setVisibility(View.INVISIBLE);
            }

            final User user = notification.getSendUser();
            ParseFile profileImage = user.getProfileImage();
            if (profileImage != null) {
                Glide.with(context).load(profileImage.getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImageNotif);
            }

            ivProfileImageNotif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToUser(user);
                }
            });

        }
    }

    public void clear() {
        notifications.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Notification> newNotifications) {
        notifications.addAll(newNotifications);
        notifyDataSetChanged();
    }

    private void goToUser(ParseUser user) {
        if (user.equals(ParseUser.getCurrentUser()))
            ((HomeActivity) context).bottomNavigationView.setSelectedItemId(R.id.action_profile);
        ((HomeActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, ProfileFragment.newInstance(user.getObjectId())).addToBackStack(ProfileFragment.TAG).commit();
    }

    public String getFriendStatus(User sender, User receiver) {
        ParseQuery<Friendship> query = new ParseQuery<Friendship>(Friendship.class);
        query.whereEqualTo(Friendship.KEY_USER1, sender);
        query.whereEqualTo(Friendship.KEY_USER2, receiver);
        try {
            Friendship friendship = query.getFirst();
            if (friendship.getState().equals(Friendship.State.Requested)) {
                return "Requested";
            } else {
                return "Friends";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Unfriended";
        }
    }
}
