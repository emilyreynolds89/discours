package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;

import com.codepath.fbu_newsfeed.Adapters.TabAdapter;
import com.codepath.fbu_newsfeed.Fragments.ArticleSearchFragment;
import com.codepath.fbu_newsfeed.Fragments.TagSearchFragment;
import com.codepath.fbu_newsfeed.Fragments.UserSearchFragment;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private TabAdapter tabAdapter;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;
    public @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new UserSearchFragment(), "Users");
        tabAdapter.addFragment(new ArticleSearchFragment(), "Articles");
        tabAdapter.addFragment(new TagSearchFragment(), "Tags");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }


}
