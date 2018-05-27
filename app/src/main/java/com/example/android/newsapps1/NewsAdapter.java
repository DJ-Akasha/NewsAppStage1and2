package com.example.android.newsapps1;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends ArrayAdapter<News> {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app.
     * @param newsItems is the list of news stories, which is the data source of the adapter.
     */
    public NewsAdapter(Activity context, ArrayList<News> newsItems){
        super(context, 0, newsItems);
    }

    /**
     * Returns a list item view that displays info about the news item at the given position
     * in the list of news items.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /** Check if there is an existing list item view (called convertView) at the given position
         in the list of news items. */
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item,
                    parent, false);
        }

        // Find the News object at the given position in the list of news items.
        News currentNewsItem = getItem(position);

        ImageView thumbnailView = listItemView.findViewById(R.id.thumbnail);
        // Found out about Picasso when looking for ways to display images.
        // Code found at https://stackoverflow.com/questions/5776851/load-image-from-url
        // Author ρяσѕρєя K, date 14 Jan 2016
        Picasso.get()
                .load(currentNewsItem.getImageThumbnail())
                .into(thumbnailView);

        // Find the TextView with the view ID section_id
        TextView sectionNameView = listItemView.findViewById(R.id.section_name);
        sectionNameView.setText(currentNewsItem.getSectionName());

        // Find the TextView with the view ID web_title
        TextView webTitleView = listItemView.findViewById(R.id.web_title);
        webTitleView.setText(currentNewsItem.getWebTitle());

        // Find the TextView with the view ID web_publication_date
        TextView webPublicationDate = listItemView.findViewById(R.id.web_publication_date);

        // Find the TextView with the view ID author
        TextView author = listItemView.findViewById(R.id.author);
        author.setText(currentNewsItem.getAuthor());

        // Find the TextView with the view ID body
        TextView body = listItemView.findViewById(R.id.body);
        body.setText(currentNewsItem.getBody());

        // Bits of this code found on the forum.
        // https://discussions.udacity.com/t/display-the-date-in-the-news-app/227647
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                ("yyyy-MM-dd'T'kk:mm:ss'Z'");
        Date date = null;
        try {
            date = simpleDateFormat.parse(currentNewsItem.getPubDate());
        } catch (ParseException e) {
            Log.e(LOG_TAG, "getView: Problem parsing the date", e);
        }
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        String finalDate = newDateFormat.format(date);
        if (date != null) {
            webPublicationDate.setText(finalDate);
        }

        // Return the listItemView.
        return listItemView;
    }
}
