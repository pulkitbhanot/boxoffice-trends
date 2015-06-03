package com.boxoffice;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by pulkit on 3/10/15.
 */
public class MovieViewRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(MovieViewRunnable.class);

    private final List<Movie> movieList;
    private final int myNumber;
    private final int incrementBy;
    private final List<MovieViews> viewList;
    private final CountDownLatch latch;
    private String serverUrl;
    private MovieViewSerializer serializer;

    public MovieViewRunnable(Configuration configuration, List<Movie> movieList, int myNumber, List<MovieViews> movieViewsList, CountDownLatch latch) {
        this.serverUrl = configuration.getMovieDetailURL();
        this.movieList = movieList;
        this.myNumber = myNumber;
        this.incrementBy = configuration.getDetailDownloadPoolSize();
        this.viewList = movieViewsList;
        this.latch = latch;
        try {
            this.serializer = configuration.getSerializerClass().newInstance();
        } catch (Exception ex) {
            throw new InvalidConfigurationException("Unable to create instance of serializer" + configuration.getSerializerClass());
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        int executions = 0;
        for (int i = myNumber; i < movieList.size(); i = i + incrementBy) {
            try {
                executions++;
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                String movieViewURL = serverUrl + URLEncoder.encode(movieList.get(i).getName(), "UTF-8");
                HttpGet request = new HttpGet(movieViewURL);
                request.addHeader("content-type", "application/json");
                HttpResponse result = httpClient.execute(request);
                if (result.getStatusLine().getStatusCode() == 200) {
                    MovieViews currentMovieView = serializer.deserializeFromString(EntityUtils.toString(result.getEntity(), "UTF-8"));
                    currentMovieView.setName(movieList.get(i).getName());
                    viewList.add(currentMovieView);
                } else {
                    logger.warn("NON-OK status for request " + movieViewURL + " status code " + result.getStatusLine().getStatusCode() + " will attempt by removing character '/' ");
                    movieViewURL = serverUrl + URLEncoder.encode(movieList.get(i).getName().replaceAll("/", ""), "UTF-8");
                    request = new HttpGet(movieViewURL);
                    request.addHeader("content-type", "application/json");
                    result = httpClient.execute(request);
                    if (result.getStatusLine().getStatusCode() == 200) {
                        logger.info("Successfully processed now");
                        MovieViews currentMovieView = serializer.deserializeFromString(EntityUtils.toString(result.getEntity(), "UTF-8"));
                        currentMovieView.setName(movieList.get(i).getName());
                        viewList.add(currentMovieView);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        long timeTaken = System.currentTimeMillis() - startTime;

        Collections.sort(viewList, new Comparator<MovieViews>() {
            @Override
            public int compare(MovieViews o1, MovieViews o2) {
                return o2.getViews() - o1.getViews();
            }
        });
        logger.info("Time taken for executing " + executions + " requests is " + timeTaken);
        logger.info("Counting down");
        latch.countDown();
    }
}
