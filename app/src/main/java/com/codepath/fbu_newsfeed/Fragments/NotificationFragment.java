package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Adapters.NotificationAdapter;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    public static final String TAG = "NotificationFragment";

    ArrayList<Notification> notifications;
    NotificationAdapter notificationAdapter;
    RecyclerView rvNotifications;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        rvNotifications = view.findViewById(R.id.rvNotifications);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notifications = new ArrayList<Notification>();
        notificationAdapter = new NotificationAdapter(getContext(), notifications);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(linearLayoutManager);
        rvNotifications.setAdapter(notificationAdapter);

        queryNotifications(false);

    }

    private void queryNotifications(final boolean refresh) {
        ParseQuery<Notification> notificationQuery = ParseQuery.getQuery(Notification.class);
        notificationQuery.include(Notification.KEY_TYPE);
        notificationQuery.include(Notification.KEY_SEND_USER);
        notificationQuery.include(Notification.KEY_RECEIVE_USER);
        notificationQuery.include(Notification.KEY_SHARE);
        notificationQuery.whereEqualTo(Notification.KEY_RECEIVE_USER, ParseUser.getCurrentUser());

        notificationQuery.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> newNotifications, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in notification query");
                    e.printStackTrace();
                    return;
                }
                notificationAdapter.addAll(newNotifications);
                notificationAdapter.notifyDataSetChanged();
            }
        });
    }
}
