package com.codepath.fbu_newsfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

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
    @BindView(R.id.miBookmark)
    ImageButton miBookmark;

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
                FragmentManager fragmentManager = getSupportFragmentManager();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);

                boolean swipeRight = true;

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flContainer);
                Fragment fragment = new FeedFragment();
                String fragmentTag = null;
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        if (!(currentFragment instanceof FeedFragment)) {
                            swipeRight = false;
                            fragmentTag = FeedFragment.TAG;
                            fragment = fragmentManager.findFragmentByTag(fragmentTag);
                            if (fragment == null) {
                                fragment = new FeedFragment();
                            }
                        }
                        break;
                    case R.id.action_trending:
                        if (!(currentFragment instanceof TrendsFragment)) {
                            if (currentFragment instanceof CreateFragment || currentFragment instanceof NotificationFragment || currentFragment instanceof ProfileFragment) {
                                swipeRight = false;
                            }
                            fragmentTag = TrendsFragment.TAG;

                            fragment = fragmentManager.findFragmentByTag(fragmentTag);
                            if (fragment == null) {
                                fragment = new TrendsFragment();
                            }
                        }
                        break;
                    case R.id.action_compose:
                        if (!(currentFragment instanceof CreateFragment)) {
                            if (currentFragment instanceof NotificationFragment || currentFragment instanceof ProfileFragment) {
                                swipeRight = false;
                            }
                            fragmentTag = CreateFragment.TAG;
                            fragment = new CreateFragment();

                        }
                        break;
                    case R.id.action_notification:
                        if (!(currentFragment instanceof NotificationFragment)) {
                            if (currentFragment instanceof ProfileFragment) {
                                swipeRight = false;
                            }
                            fragmentTag = NotificationFragment.TAG;
                            fragment = fragmentManager.findFragmentByTag(fragmentTag);
                            if (fragment == null) {
                                fragment = new NotificationFragment();
                            }
                        }
                        break;
                    case R.id.action_profile:
                        fragmentTag = ProfileFragment.TAG;
                        fragment = fragmentManager.findFragmentByTag(fragmentTag);
                        if (fragment == null) {
                            fragment = ProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId());
                        }
                        break;
                    default:
                        break;
                }
                if (swipeRight) {
                    fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
                } else {
                    fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out, R.anim.left_in, R.anim.right_out);
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
            String url = intent.getStringExtra("url");

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
                Fragment newProfileFragment = ProfileFragment.newInstance(user_id);

                fragmentTransaction.replace(R.id.flContainer, newProfileFragment).addToBackStack(ProfileFragment.TAG).commit();
            }

            if (url != null) {
                Bundle bundle = new Bundle();
                CreateFragment createFragment = new CreateFragment();
                bundle.putString("url", url);

                createFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, createFragment).commit();
            }

            onSharedIntent();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null)
            onSharedIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getSupportFragmentManager().findFragmentById(R.id.flContainer) instanceof FeedFragment) {
            getMenuInflater().inflate(R.menu.menu_feed, menu);
            miBookmark.setVisibility(View.GONE);

        } else if ((getSupportFragmentManager().findFragmentById(R.id.flContainer) instanceof TrendsFragment)) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            miBookmark.setVisibility(View.VISIBLE);

            miBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(HomeActivity.this, BookmarksActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            });
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            miBookmark.setVisibility(View.GONE);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miFilter:
                return false;
        }
        return false;
    }

    private void onSharedIntent() {
        Intent receivedIntent = getIntent();
        dumpIntent(receivedIntent);
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, createFragment).addToBackStack(CreateFragment.TAG).commit();

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public void onBackStackChanged() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if(backStackEntryCount > 0){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }else{
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void dumpIntent(Intent data) {

        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
    }

}




