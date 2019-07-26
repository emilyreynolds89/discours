package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.fbu_newsfeed.Fragments.ComposeFragment;
import com.codepath.fbu_newsfeed.Fragments.CreateFragment;
import com.codepath.fbu_newsfeed.Fragments.FeedFragment;
import com.codepath.fbu_newsfeed.Fragments.NotificationFragment;
import com.codepath.fbu_newsfeed.Fragments.ProfileFragment;
import com.codepath.fbu_newsfeed.Fragments.TrendsFragment;
import com.codepath.fbu_newsfeed.Models.Article;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    private final String TAG ="HomeActivity";
    private androidx.appcompat.widget.ShareActionProvider shareActionProvider;


    public @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;
    public @BindView(R.id.toolbar) Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = new FeedFragment();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new FeedFragment();
                        break;
                    case R.id.action_trending:
                        fragment = new TrendsFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new CreateFragment();
                        break;
                    // TODO: notifications fragment
                    case R.id.action_notification:
                        fragment = new NotificationFragment();
                        break;
                    case R.id.action_profile:
                        fragment = ProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId());
                        break;
                    default:
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);

        setSupportActionBar(toolbar);

        if (intent != null) {
            Article article = (Article) intent.getSerializableExtra("article");
            String user_id = intent.getStringExtra("user_id");

            if (article != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                ComposeFragment composeFragment = new ComposeFragment();
                composeFragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.flContainer, composeFragment).commit();
            }

            if (user_id != null) {
                Fragment newProfileFragment = ProfileFragment.newInstance(user_id);

                fragmentManager.beginTransaction().replace(R.id.flContainer, newProfileFragment).commit();
            }
            onSharedIntent();
        }


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void onSharedIntent() {
        Intent receivedIntent = getIntent();
        Log.e(TAG, receivedIntent.toString());
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();

        if (receivedAction != null) {

            if (receivedAction.equals(Intent.ACTION_SEND)) {
                // check mime type
                if (receivedType.startsWith("text/")) {
                    String receivedText = receivedIntent
                            .getStringExtra(Intent.EXTRA_TEXT);
                    if (receivedText != null) {
                        Log.d(TAG, "We are at ReceivedText: " + receivedText);
                        Bundle bundle = new Bundle();
                        CreateFragment createFragment = new CreateFragment();
                        bundle.putString("url", receivedText);

                        createFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, createFragment).commit();

                    }
                } else if (receivedType.startsWith("image/")) {
                    Log.d(TAG, "Received Image");

                    Uri receiveUri = (Uri) receivedIntent
                            .getParcelableExtra(Intent.EXTRA_STREAM);

                    if (receiveUri != null) {
                        Log.d(TAG, "Received URI");




                        Log.d(TAG, receiveUri.toString());
                    }
                }

            } else if (receivedAction.equals(Intent.ACTION_MAIN)) {

                Log.e(TAG, "onSharedIntent: nothing shared");
            }
        }
    }
}
