package com.rodrigo.flickr.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.rodrigo.flickr.presenter.MainPresenter;
import com.rodrigo.flickr.view.wedget.SwipeRefreshLayoutBottom;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainMvpView {
    private static final String KEY_KEYWORD = "KEY_KEYWORD";
    private static final String KEY_SWIPE_REFRESHING = "MainActivity.KEY_SWIPE_REFRESHING";
    private static final String KEY_SHOWING_MESSAGE = "MainActivity.KEY_SHOWING_MESSAGE";
    private static final String KEY_MESSAGE = "MainActivity.KEY_MESSAGE";
    private MainPresenter presenter;
    private TextView messageView;
    private RecyclerView resultGrid;
    private PhotoAdapter photoAdapter;
    private SwipeRefreshLayoutBottom swipeRefreshLayout;

    private String keyword;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String newKeyword = intent.getStringExtra(SearchManager.QUERY);
            if (!newKeyword.equals(keyword)) {
                keyword = newKeyword;
                presenter.reset();
                presenter.searchPhotos(keyword);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = setupPresenter();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultGrid = (RecyclerView) findViewById(R.id.search_result_grid);
        setupResultGrid();
        messageView = (TextView) findViewById(R.id.message);

        swipeRefreshLayout = (SwipeRefreshLayoutBottom) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!TextUtils.isEmpty(keyword)) {
                presenter.searchPhotos(keyword);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            // We don't start search by default, until user input a keyword, and start search,
            // in which case onNewIntent() will be called. Note: this requires a SingleTop launchMode.
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        keyword = savedInstanceState.getString(KEY_KEYWORD);
        invalidateOptionsMenu();

        boolean isRefreshing = savedInstanceState.getBoolean(KEY_SWIPE_REFRESHING);
        if (isRefreshing) {
            swipeRefreshLayout.setRefreshing(true);
        }

        messageView.setText(savedInstanceState.getString(KEY_MESSAGE));
        if (savedInstanceState.getBoolean(KEY_SHOWING_MESSAGE)) {
            messageView.setVisibility(View.VISIBLE);
        } else {
            messageView.setVisibility(View.GONE);
        }

        photoAdapter.setPhotos(presenter.getAllPhotoList());
    }

    @NonNull
    private MainPresenter setupPresenter() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(MainPresenter.TAG);
        if (fragment != null) {
            return (MainPresenter) fragment;
        } else {
            MainPresenter mainPresenter = new MainPresenter();
            fragmentManager.beginTransaction().add(mainPresenter, MainPresenter.TAG).commit();
            return mainPresenter;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);

        MenuItem search = menu.findItem(R.id.actionbar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        if (!TextUtils.isEmpty(keyword)) {
            search.expandActionView();
            searchView.setQuery(keyword, true);
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_KEYWORD, keyword);
        outState.putBoolean(KEY_SWIPE_REFRESHING, swipeRefreshLayout.isRefreshing());
        outState.putBoolean(KEY_SHOWING_MESSAGE, messageView.getVisibility() == View.VISIBLE);
        outState.putString(KEY_MESSAGE, messageView.getText().toString());
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
        resultGrid.scrollToPosition(0);
        swipeRefreshLayout.setRefreshing(false);
        messageView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(int stringId) {
        swipeRefreshLayout.setRefreshing(false);
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(stringId);
    }

    @Override
    public void showProgressIndicator() {
        swipeRefreshLayout.setRefreshing(true);
        messageView.setVisibility(View.GONE);
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
