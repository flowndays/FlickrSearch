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
import android.widget.Toast;

import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.rodrigo.flickr.presenter.MainPresenter;
import com.rodrigo.flickr.view.wedget.SwipeRefreshLayoutBottom;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainMvpView {
    private static final String KEY_KEYWORD = "KEY_KEYWORD";
    private static final String KEY_SWIPE_REFRESHING = "MainActivity.KEY_SWIPE_REFRESHING";
    private static final String KEY_SHOWING_MESSAGE = "MainActivity.KEY_SHOWING_MESSAGE";
    private static final String KEY_MESSAGE = "MainActivity.KEY_MESSAGE";
    private static final int SPAN_COUNT_PORTRAIT = 3;
    private static final int SPAN_COUNT_LANDSCAPE = 4;

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
                String query = intent.getStringExtra(SearchManager.QUERY);
                presenter.saveSearchQuery(query);

                keyword = newKeyword;

                presenter.reset();
                presenter.searchPhotos(keyword);
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = setupPresenter();

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        resultGrid = (RecyclerView) findViewById(R.id.search_result_grid);
        setupResultGrid();
        setupSwipeRefreshLayout();
        messageView = (TextView) findViewById(R.id.message);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            // By default, no search is executed. When user input in SearchView and start
            // searching, onNewIntent() will be called. Note: this requires a SingleTop launchMode.
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
        int spanCount = getResources().getBoolean(R.bool.isPortrait)
                        ? SPAN_COUNT_PORTRAIT
                        : SPAN_COUNT_LANDSCAPE;

        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL, false);
        resultGrid.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(getPhotoClickListener(), spanCount);
        photoAdapter.setFixedSizeInPixels(
                getResources().getDimensionPixelSize(R.dimen.photo_width),
                getResources().getDimensionPixelSize(R.dimen.photo_height));
        resultGrid.setHasFixedSize(true);
        resultGrid.addItemDecoration(new GridSpacingItemDecoration(spanCount, 10));
        resultGrid.setAdapter(photoAdapter);
    }

    private PhotoAdapter.PhotoClickAction getPhotoClickListener() {
        return photo -> PhotoDetailActivity.start(MainActivity.this, photo);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayoutBottom) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!TextUtils.isEmpty(keyword)) {
                presenter.searchPhotos(keyword);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);

        MenuItem searchItem = menu.findItem(R.id.actionbar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if (!TextUtils.isEmpty(keyword)) {
            searchItem.expandActionView();
            searchView.setQuery(keyword, false);
            searchView.clearFocus();
        }

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                reset();
                return true;
            }
        });
        return true;
    }

    private void reset() {
        keyword = null;
        presenter.reset();
        photoAdapter.setPhotos(Collections.emptyList());
        showMessage(R.string.search_from_action_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionbar_clear) {
            presenter.clearSearchHistory();
            reset();
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public void appendPhotos(List<Photo> photos) {
        photoAdapter.addPhotos(photos);
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
        photoAdapter.setPhotos(Collections.emptyList());
        swipeRefreshLayout.setRefreshing(false);
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(stringId);
    }

    @Override
    public void showNoMoreResult() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this, R.string.no_more_result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressIndicator() {
        swipeRefreshLayout.setRefreshing(true);
        messageView.setVisibility(View.GONE);
    }

}
