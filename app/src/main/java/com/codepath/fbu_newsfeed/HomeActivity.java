package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

public class HomeActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private final String TAG ="HomeActivity";
    private androidx.appcompat.widget.ShareActionProvider shareActionProvider;


    public @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;
    public @BindView(R.id.toolbar) Toolbar toolbar;

    static boolean isSinglePane = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        //final FragmentManager fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flContainer);
                Fragment fragment = new FeedFragment();
                String fragmentTag = null;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        if (!(currentFragment instanceof FeedFragment)) {
                            fragment = new FeedFragment();
                            fragmentTag = FeedFragment.TAG;
                        }
                        break;
                    case R.id.action_trending:
                        if (!(currentFragment instanceof TrendsFragment)) {
                            fragment = new TrendsFragment();
                            fragmentTag = TrendsFragment.TAG;
                        }
                        break;
                    case R.id.action_compose:
                        if (!(currentFragment instanceof CreateFragment)) {
                            fragment = new CreateFragment();
                            fragmentTag = CreateFragment.TAG;
                        }
                        break;
                    case R.id.action_notification:
                        if (!(currentFragment instanceof NotificationFragment)) {
                            fragment = new NotificationFragment();
                            fragmentTag = NotificationFragment.TAG;
                        }
                        break;
                    case R.id.action_profile:
                        if (!(currentFragment instanceof ProfileFragment)) {
                            fragment = ProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId());
                            fragmentTag = ProfileFragment.TAG;
                        }
                        break;
                    default:
                        break;
                }
                if (!Objects.equals(fragmentTag, FeedFragment.TAG) && fragmentTag != null) {
                    fragmentTransaction.replace(R.id.flContainer, fragment).addToBackStack(fragmentTag).commit();
                    //fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(fragmentTag).commit();
                } else if (fragmentTag != null){
                    fragmentTransaction.replace(R.id.flContainer, fragment).addToBackStack(null).commit();
                    //fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);

        setSupportActionBar(toolbar);

        if (intent != null) {
            Article article = (Article) intent.getSerializableExtra("article");
            String user_id = intent.getStringExtra("user_id");

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);

            if (article != null) {
                bottomNavigationView.setSelectedItemId(R.id.action_compose);

                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                ComposeFragment composeFragment = new ComposeFragment();
                composeFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.flContainer, composeFragment).addToBackStack(ComposeFragment.TAG).commit();
            }

            if (user_id != null) {
                if (user_id.equals(ParseUser.getCurrentUser().getObjectId()))
                    bottomNavigationView.setSelectedItemId(R.id.action_profile);
                Fragment newProfileFragment = ProfileFragment.newInstance(user_id);

                fragmentTransaction.replace(R.id.flContainer, newProfileFragment).addToBackStack(ProfileFragment.TAG).commit();
            }

            onSharedIntent();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
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

    public void onBackStackChanged() {
        int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        if(backStackEntryCount > 0){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }else{
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

}




