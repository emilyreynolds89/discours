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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.fbu_newsfeed.Adapters.NotificationAdapter;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotificationFragment extends Fragment {

    public static final String TAG = "NotificationFragment";

    @BindView(R.id.rvNotifications) RecyclerView rvNotifications;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private Unbinder unbinder;

    private ArrayList<Notification> notifications;
    private NotificationAdapter notificationAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(3).setChecked(true);

        notifications = new ArrayList<Notification>();
        notificationAdapter = new NotificationAdapter(getContext(), notifications);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(linearLayoutManager);
        rvNotifications.setAdapter(notificationAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationAdapter.clear();
                queryNotifications(0);
                scrollListener.resetState();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);

        queryNotifications(0);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryNotifications(page);
            }
        };

        rvNotifications.addOnScrollListener(scrollListener);

        ((HomeActivity) getActivity()).toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void queryNotifications(int offset) {
        ParseQuery<Notification> notificationQuery = ParseQuery.getQuery(Notification.class);
        notificationQuery.include(Notification.KEY_SEND_USER);
        notificationQuery.include(Notification.KEY_RECEIVE_USER);
        notificationQuery.include(Notification.KEY_SHARE);
        notificationQuery.whereEqualTo(Notification.KEY_RECEIVE_USER, ParseUser.getCurrentUser());
        notificationQuery.setLimit(Notification.LIMIT);
        notificationQuery.setSkip(offset * Notification.LIMIT);
        notificationQuery.orderByDescending("createdAt");

        notificationQuery.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> newNotifications, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in notification query");
                    e.printStackTrace();
                    return;
                }
                notificationAdapter.addAll(newNotifications);
            }
        });
    }
}
