package com.example.android.newsapps1;

/**
 * {@link News} represents a news item. It holds the details of that
 * news item such as section id, the web title, the mDate it was
 * published, the article url, the author, the bodyTextSummary and the image.
 */
public class News {

    /** Section ID of the news item */
    private final String mSectionName;

    /** Web title of the news item */
    private final String mWebTitle;

    /** Date the news item was published */
    private final String mDate;

    /** Website URL of the news item */
    private String mUrl;

    /** Author of the news item */
    private String mAuthor;

    /** BodyTextSummary of the news item */
    private String mBody;

    /** Image from the news item */
    private String mImageThumbnail;

    /**
     * Constructs a new {@link News} object.
     * @param sectionName
     * @param webTitle
     * @param webPubDate
     * @param author
     * @param body
     * @param imageThumbnail
     */
    public News(String sectionName, String webPubDate, String webTitle, String url,
                String author, String body, String imageThumbnail) {
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mDate = webPubDate;
        mUrl = url;
        mAuthor = author;
        mBody = body;
        mImageThumbnail = imageThumbnail;
    }

    /**
     * Returns the section ID for the news story.
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the title of the news story.
     */
    public String getWebTitle() {
        return mWebTitle;
    }

    /**
     * Returns the date the news story was published.
     */
    public String getPubDate() {
        return mDate;
    }

    /**
     * Returns the website URL to go to the full article/news story.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the author's name that wrote the article/news story.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the bodyTextSummary for the article/news story.
     */
    public String getBody() {
        return mBody;
    }

    /**
     * Returns the image thumbnail for the article/news story.
     */
    public String getImageThumbnail() {
        return mImageThumbnail;
    }
}
