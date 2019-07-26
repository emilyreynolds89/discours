package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    private final String TAG ="HomeActivity";

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
                String fragmentTag = FeedFragment.TAG;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new FeedFragment();
                        fragmentTag = FeedFragment.TAG;
                        break;
                    case R.id.action_trending:
                        fragment = new TrendsFragment();
                        fragmentTag = TrendsFragment.TAG;
                        break;
                    case R.id.action_compose:
                        fragment = new CreateFragment();
                        fragmentTag = CreateFragment.TAG;
                        break;
                    case R.id.action_notification:
                        fragment = new NotificationFragment();
                        fragmentTag = NotificationFragment.TAG;
                        break;
                    case R.id.action_profile:
                        fragment = ProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId());
                        fragmentTag = ProfileFragment.TAG;
                        break;
                    default:
                        break;
                }
                if (!Objects.equals(fragmentTag, FeedFragment.TAG)) {
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(fragmentTag).commit();
                } else {
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);

        setSupportActionBar(toolbar);

        if (intent != null) {
            Article article = (Article) intent.getSerializableExtra("article");
            String user_id = intent.getStringExtra("user_id");

            if (article != null) {
                bottomNavigationView.setSelectedItemId(R.id.action_compose);

                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                ComposeFragment composeFragment = new ComposeFragment();
                composeFragment.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.flContainer, composeFragment).addToBackStack(ComposeFragment.TAG).commit();
            }

            if (user_id != null) {
                if (user_id.equals(ParseUser.getCurrentUser().getObjectId()))
                    bottomNavigationView.setSelectedItemId(R.id.action_profile);
                Fragment newProfileFragment = ProfileFragment.newInstance(user_id);

                fragmentManager.beginTransaction().replace(R.id.flContainer, newProfileFragment).addToBackStack(ProfileFragment.TAG).commit();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }


}
