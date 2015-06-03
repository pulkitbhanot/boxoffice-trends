package com.boxoffice;

import java.util.Properties;

import static com.boxoffice.Constants.*;

/**
 * Created by pulkit on 3/10/15.
 */
public class LaunchPad {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(LIST_URL, "http://xfinitytv.comcast.net/movie.widget");
        properties.put(MOVIE_DETAIL_URL, "http://stats.grok.se/json/en/201402/");
        properties.put(DETAIL_DOWNLOAD_POOL_SIZE, "10");
        properties.put(OUTPUT_FILE_LOCATION, "output.txt");
        properties.put(MOVIE_LIST_DOWNLOADER, HBOMovieListDownloader.class.getName());
        properties.put(MOVIE_VIEWERSHIP_DOWNLOADER, WikipediaViewsDetailDownloader.class.getName());
        properties.put(OUTPUT_FILE_LOCATION, "output.txt");

        Configuration configuration = Configuration.newConfiguration(properties);
        configuration.run();
        System.exit(0);
    }
}
