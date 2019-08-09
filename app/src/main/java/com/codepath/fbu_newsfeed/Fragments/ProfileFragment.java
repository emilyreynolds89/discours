package com.codepath.fbu_newsfeed.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.Adapters.ShareAdapter;
import com.codepath.fbu_newsfeed.FriendsListActivity;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.LoginActivity;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    private ParseUser user;

    @BindView(R.id.ivProfileImageNotif) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvFullName) TextView tvFullName;
    @BindView(R.id.tvBio) TextView tvBio;
    @BindView(R.id.tvFriends) TextView tvFriends;
    @BindView(R.id.btnLogout) Button btnLogout;
    @BindView(R.id.btnRequest) Button btnRequest;
    @BindView(R.id.btnEdit) ImageButton btnEdit;
    @BindView(R.id.btnUnfriend) ImageButton btnUnfriend;
    @BindView(R.id.btnReport) ImageButton btnReport;
    @BindView(R.id.rvProfilePosts) RecyclerView rvProfilePosts;
    @BindView(R.id.ivBadge) ImageView ivBadge;
    @BindView(R.id.progressBarHolder) FrameLayout progressBarHolder;

    private ShareAdapter shareAdapter;
    protected @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    private Unbinder unbinder;

    public static ProfileFragment newInstance(String user_id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String user_id = getArguments().getString("user_id");

        if (user_id.equals(ParseUser.getCurrentUser().getObjectId()))
            ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(4).setChecked(true);

        Log.d(TAG, "get profile for: " + user_id);
        try {
            user = getUser(user_id);
            Log.d(TAG, "we're getting this user: " + user_id);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user, showing current user", e);
            user = ParseUser.getCurrentUser();
        }

        tvUsername.setText("@" + user.getString(User.KEY_USERNAME));
        tvFullName.setText(user.getString(User.KEY_FULLNAME));
        tvBio.setText(user.getString(User.KEY_BIO));

        if (user.getParseFile("profileImage") != null) {
            Glide.with(getContext()).load(user.getParseFile("profileImage").getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);
        } else {
            Glide.with(getContext()).load(R.drawable.profileplaceholder).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);
        }


        ArrayList<Share> mShare = new ArrayList<>();

        this.shareAdapter = new ShareAdapter(mShare);
        rvProfilePosts.setAdapter(this.shareAdapter);
        this.shareAdapter.clear();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvProfilePosts.setLayoutManager(linearLayoutManager);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
                scrollListener.resetState();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryShares(false, page);
            }
        };

        ((HomeActivity) getActivity()).toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvProfilePosts.smoothScrollToPosition(0);
            }
        });

        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            // if profile is for current user
            tvFriends.setVisibility(View.VISIBLE);
            tvFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendsList();
                }
            });

            btnEdit.setVisibility(View.VISIBLE);
            btnUnfriend.setVisibility(View.GONE);
            btnReport.setVisibility(View.GONE);
            ivBadge.setVisibility(View.VISIBLE);

            btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reportUser();
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editUser();
                }
            });
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOut();
                }
            });
            ivBadge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showUserStats();
                }
            });

        } else {
            btnEdit.setVisibility(View.INVISIBLE);
            btnReport.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.INVISIBLE);
            btnRequest.setVisibility(View.VISIBLE);
            btnUnfriend.setVisibility(View.GONE);
            tvFriends.setVisibility(View.GONE);
            ivBadge.setVisibility(View.GONE);

            btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reportUser();
                }
            });

            if (isFriends()) {
                btnRequest.setText("Friends!");
                btnUnfriend.setVisibility(View.VISIBLE);
                tvFriends.setVisibility(View.VISIBLE);
                tvFriends.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        while (motionEvent.isButtonPressed(MotionEvent.ACTION_DOWN)) {
                            view.setSelected(true);
                        }
                        view.setSelected(false);
                        return false;
                    }
                });
                btnRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        friendsList();
                    }
                });

                btnUnfriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unfriend();
                    }
                });


            } else if (sentRequest()) {
                btnRequest.setText("Requested");
            } else if (canAccept()) {
                btnRequest.setText("Accept request");
                btnRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acceptRequest(user);
                    }
                });
            } else {
                    btnRequest.setText("Request friend");
                    btnRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestFriend(user);
                        }
                    });
                }
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getContext(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                queryShares(true, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return anim;
    }

    private void unfriend() {
        Friendship friendship = findFriendship();
        if (friendship != null) {
            friendship.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getContext(), "Unfriended @" + user.getUsername(), Toast.LENGTH_SHORT).show();
                    getFragmentManager()
                            .beginTransaction()
                            .detach(ProfileFragment.this)
                            .attach(ProfileFragment.this)
                            .commit();
                }
            });
        }
        deleteNotification(Notification.ACCEPT_REQUEST, (User) user, (User) ParseUser.getCurrentUser());
        deleteNotification(Notification.ACCEPT_REQUEST, (User) ParseUser.getCurrentUser(), (User) user);
        deleteNotification(Notification.FRIEND_REQUEST, (User) user, (User) ParseUser.getCurrentUser());
        deleteNotification(Notification.FRIEND_REQUEST, (User) ParseUser.getCurrentUser(), (User) user);

    }

    private void friendsList() {
        Intent intent = new Intent(getActivity(), FriendsListActivity.class);
        intent.putExtra("user_id", user.getObjectId());
        getActivity().startActivity(intent);
        (getActivity()).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void reportUser() {
        FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        ReportUserFragment userReportDialog = ReportUserFragment.newInstance(user.getObjectId());
        userReportDialog.show(fm, "fragment_user_report");
    }

    private void showUserStats() {
        /*progressBarHolder.setVisibility(View.VISIBLE);
        FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        UserStatsDialogFragment userStatsDialog = UserStatsDialogFragment.newInstance();
        userStatsDialog.setTargetFragment(ProfileFragment.this, 1337);
        userStatsDialog.show(fm, "fragment_user_stats");*/

        FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        UserStatsDialogDemoFragment userStatsDialogDemo = UserStatsDialogDemoFragment.newInstance();
        userStatsDialogDemo.show(fm, "fragment_user_stats_demo");
    }

    private void editUser() {
        //FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
        EditProfileDialogFragment editProfileDialog = EditProfileDialogFragment.newInstance(user.getObjectId());
        editProfileDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .detach(ProfileFragment.this)
                        .attach(ProfileFragment.this)
                        .commit();
            }
        });
        editProfileDialog.show(fragmentTransaction, "fragment_edit_profile");
    }

    private void requestFriend(final ParseUser potentialFriend) {
        Friendship friendship = new Friendship(ParseUser.getCurrentUser(), potentialFriend, Friendship.State.Requested);
        friendship.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Friend request sent to " + potentialFriend.getObjectId());
                    btnRequest.setOnClickListener(null);
                    btnRequest.setText("Requested");
                } else {
                    Log.d(TAG, "Friend request failed", e);
                }
            }
        });
        createFriendNotification(Notification.FRIEND_REQUEST, (User) potentialFriend);
    }

    private void acceptRequest(final ParseUser requestingUser) {
        ParseQuery<Friendship> query = ParseQuery.getQuery("Friendship");
        query.whereEqualTo("user2",  ParseUser.getCurrentUser());
        query.whereEqualTo("user1", user);
        try {
            List<Friendship> results = query.find();
            if (results.size() > 0) {
                Friendship friendship = results.get(0);
                friendship.setState(Friendship.State.Accepted);
                friendship.save();
                btnRequest.setText("Friends!");
                btnRequest.setOnClickListener(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        createFriendNotification(Notification.ACCEPT_REQUEST, (User) requestingUser);
    }

    private void logOut() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }
        Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void fetchTimelineAsync() {

        queryShares(true, 0);
        swipeContainer.setRefreshing(false);
    }
    private void queryShares(final boolean refresh, int offset) {
        Log.d(TAG, "this should only be getting shares from user: " + this.user.getObjectId());

        if (refresh) {
            shareAdapter.clear();
        }

        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.whereEqualTo("user", this.user);
        query.include("user");
        query.include("article");
        query.include("sourceObject");
        query.setLimit(Share.LIMIT);
        query.setSkip(offset * Share.LIMIT);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Share>() {
            @Override
            public void done(List<Share> shareList, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Got " + shareList.size() + " shares");

                    shareAdapter.addAll(shareList);
                }
                if (!refresh) scrollListener.resetState();
            }
        });
    }

    // if the current user has requested this user to be their friend
    private boolean sentRequest() {
        ParseQuery<Friendship> query = ParseQuery.getQuery("Friendship");
        query.whereEqualTo("user1",  ParseUser.getCurrentUser());
        query.whereEqualTo("user2", user);
        try {
            List<Friendship> results = query.find();
            return results.size() > 0;
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return false;
        }
    }

    private Friendship findFriendship() {
        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo("user1", ParseUser.getCurrentUser());
        query1.whereEqualTo("user2", user);
        query1.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        query2.whereEqualTo("user1", user);
        query2.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);

        try {
            List<Friendship> result = mainQuery.find();
            return result.get(0);
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return null;
        }
    }

    private boolean isFriends() {
        return findFriendship() != null;

    }

    // if this user has requested the current user to be their friend
    private boolean canAccept() {
        ParseQuery<Friendship> query = ParseQuery.getQuery("Friendship");
        query.whereEqualTo("user2",  ParseUser.getCurrentUser());
        query.whereEqualTo("user1", user);
        try {
            List<Friendship> results = query.find();
            return results.size() > 0;
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return false;
        }
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

    private void createFriendNotification(String type, User friend) {
        Log.d(TAG, "Creating friend notification of type: " + type);
        Notification notification = new Notification(type, (User) ParseUser.getCurrentUser(), friend, type);
        notification.saveInBackground();
    }

    private void deleteNotification(String type, User sendUser, User receiverUser) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);

        query.whereEqualTo(Notification.KEY_TYPE, type);
        query.whereEqualTo(Notification.KEY_SEND_USER, sendUser);
        query.whereEqualTo(Notification.KEY_RECEIVE_USER, receiverUser);

        Notification notification;

        try {
            List<Notification> result = query.find();
            if (result.size() > 0 ) {
                notification = result.get(0);
            } else {
                return;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
            return;
        }

        notification.deleteInBackground();
    }

}
