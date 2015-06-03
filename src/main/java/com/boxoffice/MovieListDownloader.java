package com.boxoffice;

import java.util.List;

/**
 * Created by pulkit on 3/10/15.
 */
public interface MovieListDownloader {
    void init(Configuration configuration);

    List<Movie> getListOfMovies();
}
