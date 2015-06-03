package com.boxoffice;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pulkit on 3/9/15.
 */
public class HBOMovieListDownloader implements MovieListDownloader {

    private String url;
    private static final Logger logger = Logger.getLogger(HBOMovieListDownloader.class);
    private List<Movie> listOfMovies;

    @Override
    public void init(Configuration configuration) {
        url = configuration.getListURL();

    }

    @Override
    public List<Movie> getListOfMovies() {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            listOfMovies = new ArrayList<Movie>(links.size());
            for (Element link : links) {
                Movie movie = new Movie(link.attr("href"), link.text());
                listOfMovies.add(movie);
            }
        } catch (IOException ex) {
            logger.error("Unable to download content from url" + url, ex);
        }

        return listOfMovies;
    }
}
