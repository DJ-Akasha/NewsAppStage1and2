package com.example.android.newsapps1;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * {@link NewsLoader} extends an {@link AsyncTaskLoader} to provide asynchronous loading of
 * data to all activities and fragments that may need it.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     * @param context of the activity.
     * @param url to load data from.
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news items.
        List<News> news = QueryUtils.fetchNewsData(mUrl);
        return news;
    }
}
