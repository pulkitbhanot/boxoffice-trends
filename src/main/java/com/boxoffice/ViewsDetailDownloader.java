package com.boxoffice;

import java.util.List;

/**
 * Created by pulkit on 3/10/15.
 */
public interface ViewsDetailDownloader {

    void init(Configuration configuration);

    void start(List<Movie> allMovies);

    List<MovieViews> getMergedList();
}
