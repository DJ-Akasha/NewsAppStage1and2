package com.example.android.newsapps1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<News>> {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** URL to query the Guardian dataset for news information. */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?q=news&show-fields=byline%2Cthumbnail&show-blocks=body&api-key=7ecb02f7-9c05-4cbb-846d-f22da708ee4d";

    /** Constant value for the news loader ID. */
    private static final int NEWS_LOADER_ID = 1;

    /** Adapter for the list of news items. */
    private NewsAdapter mAdapter;

    /** TextView that is displayed when the list is empty. */
    private TextView mEmptyStateTextView;

    /** SwipeRefreshLayout allows the user to refresh data. */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /** ListView that populates the list on the main screen of the app */
    ListView newsItemListView;

    /** View that shows the loadingIndicator */
    View loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise initResources so that string resources in QueryUtils can be accessed.
        QueryUtils.initResources(this);

        // Find a reference to the {@link ListView} in the layout.
        newsItemListView = findViewById(R.id.list);

        // Find a reference to the {@link TextView} in the layout with the ID empty_view
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsItemListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news items as input.
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView} so the list can be populated in the user
        // interface.
        newsItemListView.setAdapter(mAdapter);

        // Find the layout with the id swipe_refresh
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);

        // Setup the refresh listener for the SwipeRefreshLayout which triggers data
        // loading for new news. Information on how to do this was found here:
        // https://developer.android.com/training/swipe/
        // and on the Forums.
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader
                        (NEWS_LOADER_ID,null, MainActivity.this);
                mSwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with the full news story for the selected news item.
        newsItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news item that was clicked on.
                News currentNewsItem = mAdapter.getItem(position);

                try {
                    // Convert the String URL into an URI object.
                    Uri newsItemUri = Uri.parse(currentNewsItem.getUrl());

                    // Create a new intent to view the news item URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsItemUri);

                    // Send the intent to launch a new activity.
                    startActivity(websiteIntent);
                } catch (NullPointerException e) {
                    Log.e(LOG_TAG, "OnItemClick: Problem retrieving the URL", e);
                }
            }
        });

        try {
            // Get a reference to the ConnectivityManager to check the state of the network
            // connectivity.
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            // Get the details on the currently active default data network.
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // If there is a network connection, fetch data.
            if (networkInfo != null && networkInfo.isConnected()) {
                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getLoaderManager();

                // Initialize the loader.
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            } else {
                // Otherwise, display error.
                // First, hide the loading indicator so error message will be visible
                loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.GONE);

                // Update empty state with no connection error message.
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "OnCreate: Problem retrieving ActiveNetworkInfo", e);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        // Retrieve the preferences the user has chosen.
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Here we retrieve the user's preferences with regards to number of news items.
        String maxAmount = sharedPrefs.getString(getString(R.string.settings_max_amount_key),
                getString(R.string.settings_max_amount_default));

        // Here we retrieve the user's preferences with regards to how they want it ordered by.
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        // Here we retrieve the user's preferences with regards to what query they are
        // interested in.
        String customizeFeed =
                sharedPrefs.getString(getString(R.string.settings_customize_feed_key),
                getString(R.string.settings_customize_feed_default));

        // parse breaks apart the URI string that's passed into this parameter.
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildupon prepares the baseUri that we just parsed so we can add query parameters to it.
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value which was taken from the user preferences.
        uriBuilder.appendQueryParameter("page-size", maxAmount);
        uriBuilder.appendQueryParameter("q", customizeFeed);
        uriBuilder.appendQueryParameter("order-by", orderBy);

        // Return the completed URI.
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        // Set the progress indicator visibility to GONE once finished loading information.
        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous news items data.
        mAdapter.clear();

        try {
            // Get a reference to the ConnectivityManager to check the state of the network
            // connectivity.
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);

            // Get the details on the currently active default data network.
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Thanks to @Greta Gr abnd on Slack for the help to get this to work.
            // If there is a valid list of {@link News} items, then add them to the adapter's
            // dataset. This will trigger the ListView to update.
            if (news != null && !news.isEmpty()) {
                mAdapter.addAll(news);
            } else {
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Otherwise, display error.
                    // Set empty state text to display "No news items found".
                    mEmptyStateTextView.setText(R.string.no_news_items_found);
                } else {
                    // Otherwise, display error.
                    // Set empty state text to display "No internet connection".
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "onLoadFinished: Problem retrieving ActiveNetworkInfo", e);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset to clear out the existing data.
        mAdapter.clear();
    }

    @Override
    // This method initializes the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in the xml
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
