package com.codepath.fbu_newsfeed;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.recyclerview.widget.RecyclerView;
        import androidx.recyclerview.widget.StaggeredGridLayoutManager;
        import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

        import android.os.Bundle;
        import android.view.Menu;
        import android.view.View;
        import android.widget.TextView;

        import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
        import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
        import com.codepath.fbu_newsfeed.Models.Article;
        import com.codepath.fbu_newsfeed.Models.Bookmark;
        import com.parse.FindCallback;
        import com.parse.ParseException;
        import com.parse.ParseQuery;
        import com.parse.ParseUser;

        import java.util.ArrayList;
        import java.util.List;

        import butterknife.BindView;
        import butterknife.ButterKnife;

public class BookmarksActivity extends AppCompatActivity {

    @BindView(R.id.tvTagHeader)
    TextView tvTagHeader;
    @BindView(R.id.rvArticles)
    RecyclerView rvArticles;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    public @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<Article> articles;
    TrendsAdapter adapter;

    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        ButterKnife.bind(this);

        articles = new ArrayList<>();
        adapter = new TrendsAdapter(this, articles);

        rvArticles.setAdapter(adapter);
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        queryBookmarks(0);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryBookmarks(page);
            }
        };

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvArticles.smoothScrollToPosition(0);
            }
        });


        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

    private void queryBookmarks(int offset) {
        ParseQuery<Bookmark> query = ParseQuery.getQuery("Bookmark");
        query.whereEqualTo(Bookmark.KEY_USER, ParseUser.getCurrentUser());
        query.include(Bookmark.KEY_ARTICLE);
        query.setLimit(Article.LIMIT);
        query.setSkip(offset * Article.LIMIT);
        query.orderByDescending("createdAt");


        query.findInBackground(new FindCallback<Bookmark>() {
            @Override
            public void done(List<Bookmark> objects, ParseException e) {
                for (Bookmark bookmark : objects) {
                    articles.add(bookmark.getArticle());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchTimelineAsync() {
        adapter.clear();
        queryBookmarks(0);
        swipeContainer.setRefreshing(false);
        scrollListener.resetState();
    }


}
