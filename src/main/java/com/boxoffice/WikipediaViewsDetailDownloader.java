package com.boxoffice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;


/**
 * Created by pulkit on 3/10/15.
 */
public class WikipediaViewsDetailDownloader implements ViewsDetailDownloader {

    private List<List<MovieViews>> entireList;
    private Configuration configuration;
    private ExecutorService viewDownloaderPool;
    private static final Logger logger = Logger.getLogger(WikipediaViewsDetailDownloader.class);
    private AtomicBoolean success = new AtomicBoolean(false);

    @Override
    public void init(final Configuration configuration) {
        this.configuration = configuration;
        viewDownloaderPool = Executors.newFixedThreadPool(configuration.getDetailDownloadPoolSize(), new ThreadFactory() {
            private volatile int threadNum;

            public Thread newThread(Runnable r) {
                threadNum++;
                return new Thread(r, configuration.getConfigurationName() + "-" + threadNum);
            }
        });
        entireList = new ArrayList<List<MovieViews>>(configuration.getDetailDownloadPoolSize());

    }

    @Override
    public void start(List<Movie> allMovies) {
        logger.info("Need to download page views for " + allMovies.size() + " movies");
        logger.info("Initializing countdown with  " + configuration.getDetailDownloadPoolSize());
        CountDownLatch latch = new CountDownLatch(configuration.getDetailDownloadPoolSize());
        for (int i = 0; i < configuration.getDetailDownloadPoolSize(); i++) {
            List<MovieViews> myMovieViewList = new ArrayList<MovieViews>((allMovies.size() / configuration.getDetailDownloadPoolSize()) + configuration.getDetailDownloadPoolSize());
            MovieViewRunnable runnable = new MovieViewRunnable(configuration, allMovies, i, myMovieViewList, latch);
            entireList.add(myMovieViewList);
            viewDownloaderPool.submit(runnable);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("Completed processing all requests");
        success.set(true);
        viewDownloaderPool.shutdown();
    }

    @Override
    public List<MovieViews> getMergedList() {
        int listsOfMovies = configuration.getDetailDownloadPoolSize();
        do {
            int newSize = getNewArraySize(listsOfMovies);
            List<List<MovieViews>> tmpLists = new ArrayList<List<MovieViews>>(newSize);
            CountDownLatch latch = new CountDownLatch(newSize);
            List<ListMergerRunnable> allRunnables = new ArrayList<ListMergerRunnable>(newSize);
            for (int i = 0; i < listsOfMovies; i = i + 2) {
                ListMergerRunnable merger = new ListMergerRunnable(entireList.get(i), ((i + 1) < listsOfMovies ? entireList.get(i + 1) : null), latch);
                allRunnables.add(merger);
                viewDownloaderPool.submit(merger);
            }
            try {
                latch.await();
                logger.info("Done waiting on latch with count" + newSize);
            } catch (InterruptedException ie) {

            }
            for (ListMergerRunnable currentMerger : allRunnables) {
                tmpLists.add(currentMerger.getResult());
            }
            listsOfMovies = newSize;
            entireList = null;
            entireList = tmpLists;
        } while (listsOfMovies != 1);
        viewDownloaderPool.shutdown();
        return entireList.get(0);
    }

    private int getNewArraySize(int listsOfMovies) {
        if (listsOfMovies % 2 == 0) {
            return listsOfMovies / 2;
        } else {
            return listsOfMovies / 2 + 1;
        }

    }

}
