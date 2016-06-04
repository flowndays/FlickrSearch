package com.rodrigo.flickr.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.rodrigo.flickr.presenter.MainPresenter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainMvpView {

    private MainPresenter presenter;
    private TextView messageView;
    private RecyclerView resultGrid;
    private PhotoAdapter photoAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MainPresenter();
        presenter.attachView(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultGrid = (RecyclerView) findViewById(R.id.search_result_grid);
        setupResultGrid();
        messageView = (TextView) findViewById(R.id.message);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.resetPage();
            presenter.searchPhotos(keyword);
        });

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            keyword = intent.getStringExtra(SearchManager.QUERY);
            presenter.searchPhotos(keyword);
        }
    }

    private void setupResultGrid() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        resultGrid.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter();
        photoAdapter.setFixedSizeInPixels(
                getResources().getDimensionPixelSize(R.dimen.photo_width),
                getResources().getDimensionPixelSize(R.dimen.photo_height));
        resultGrid.setHasFixedSize(true);
        resultGrid.addItemDecoration(new GridSpacingItemDecoration(3, 10));
        resultGrid.setAdapter(photoAdapter);

        resultGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = resultGrid.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!presenter.isLoading()) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount - visibleItemCount) {
                            presenter.searchPhotos(keyword);
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);

        MenuItem search = menu.findItem(R.id.actionbar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public void appendPhotos(List<Photo> images) {
        photoAdapter.addPhotos(images);
        swipeRefreshLayout.setRefreshing(false);
        messageView.setVisibility(View.GONE);
    }

    @Override
    public void setPhotos(List<Photo> images) {
        photoAdapter.setPhotos(images);
        swipeRefreshLayout.setRefreshing(false);
        messageView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(int stringId) {
        swipeRefreshLayout.setVisibility(View.GONE);
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(stringId);
    }

    @Override
    public void showProgressIndicator() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
        messageView.setVisibility(View.GONE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    private class SearchHistoryListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }
}
