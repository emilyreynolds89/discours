package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.codepath.fbu_newsfeed.Adapters.TabAdapter;
import com.codepath.fbu_newsfeed.Adapters.UserAdapter;
import com.codepath.fbu_newsfeed.Fragments.ArticleSearchFragment;
import com.codepath.fbu_newsfeed.Fragments.TagSearchFragment;
import com.codepath.fbu_newsfeed.Fragments.UserSearchFragment;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.User;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private TabAdapter tabAdapter;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

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

    }


}
