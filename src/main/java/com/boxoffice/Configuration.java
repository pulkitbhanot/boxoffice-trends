package com.boxoffice;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static com.boxoffice.Constants.*;

/**
 * Created by pulkit on 3/9/15.
 */
public class Configuration {

    private Properties allProps;
    private String listUrl;
    private String movieDetailUrl;
    private String configurationName;
    private String outputFile;
    private Integer detailDownloadPoolSize;
    private Class<? extends MovieViewSerializer> serializerClass;
    private Class<? extends MovieViewSerializer> outputViewSerializer;
    private Class<? extends MovieListDownloader> downloaderClass;
    private Class<? extends ViewsDetailDownloader> viewershipDownloaderClass;
    private Class<? extends Writer> outputWriter;

    private Configuration(Properties properties) {
        allProps = new Properties(properties);
    }

    public static Configuration newConfiguration(Properties properties) {
        return new Configuration(properties);
    }

    public String getListURL() {
        if (listUrl == null) {
            listUrl = allProps.getProperty(LIST_URL);
        }
        if (listUrl.isEmpty()) {
            throw new InvalidConfigurationException("Missing property-" + LIST_URL);
        }
        return listUrl;
    }

    public String getMovieDetailURL() {
        if (movieDetailUrl == null) {
            movieDetailUrl = allProps.getProperty(MOVIE_DETAIL_URL);
        }
        if (movieDetailUrl == null || movieDetailUrl.isEmpty()) {
            throw new InvalidConfigurationException("Missing property-" + MOVIE_DETAIL_URL);
        }
        return movieDetailUrl;
    }

    public int getDetailDownloadPoolSize() {
        if (detailDownloadPoolSize == null) {
            detailDownloadPoolSize = Integer.parseInt(allProps.getProperty(DETAIL_DOWNLOAD_POOL_SIZE, "1"));
        }
        return detailDownloadPoolSize;
    }

    public String getConfigurationName() {
        if (configurationName == null) {
            configurationName = allProps.getProperty(CONFIGURATION_NAME, UUID.randomUUID().toString().substring(0, 5));
        }
        return configurationName;
    }

    public Class<? extends MovieViewSerializer> getSerializerClass() {
        if (serializerClass == null) {
            try {
                serializerClass = (Class<? extends MovieViewSerializer>) Class.forName(allProps.getProperty(MOVIE_DETAIL_SERIALIZER_CLASS, DefaultMovieViewSerializer.class.getName()));
            } catch (ClassNotFoundException cnfe) {
                serializerClass = DefaultMovieViewSerializer.class;
            }
        }
        return serializerClass;
    }

    public Class<? extends MovieViewSerializer> getOutputViewSerializerClass() {
        if (outputViewSerializer == null) {
            try {
                outputViewSerializer = (Class<? extends MovieViewSerializer>) Class.forName(allProps.getProperty(MOVIE_DETAIL_SERIALIZER_CLASS, DefaultMovieViewSerializer.class.getName()));
            } catch (ClassNotFoundException cnfe) {
                outputViewSerializer = DefaultMovieViewSerializer.class;
            }
        }
        return outputViewSerializer;
    }

    public Class<? extends Writer> getOutputWriterClass() {
        if (outputWriter == null) {
            try {
                outputWriter = (Class<? extends Writer>) Class.forName(allProps.getProperty(MOVIE_DETAIL_SERIALIZER_CLASS, FileJsonWriter.class.getName()));
            } catch (ClassNotFoundException cnfe) {
                outputWriter = FileJsonWriter.class;
            }
        }
        return outputWriter;
    }

    private Class<? extends MovieListDownloader> getDownloaderClass() {
        if (downloaderClass == null) {
            try {
                downloaderClass = (Class<? extends MovieListDownloader>) Class.forName(allProps.getProperty(MOVIE_LIST_DOWNLOADER));
            } catch (Exception cnfe) {
                throw new InvalidConfigurationException("Unable to load class from property-" + MOVIE_LIST_DOWNLOADER);
            }
        }
        return downloaderClass;
    }

    private Class<? extends ViewsDetailDownloader> getViewershipDownloaderClass() {
        if (viewershipDownloaderClass == null) {
            try {
                viewershipDownloaderClass = (Class<? extends ViewsDetailDownloader>) Class.forName(allProps.getProperty(MOVIE_VIEWERSHIP_DOWNLOADER));
            } catch (Exception cnfe) {
                throw new InvalidConfigurationException("Unable to load class from property-" + MOVIE_LIST_DOWNLOADER);
            }
        }
        return viewershipDownloaderClass;
    }

    public String getOutputFile() {
        if (outputFile == null) {
            outputFile = allProps.getProperty(OUTPUT_FILE_LOCATION);
        }
        if (outputFile == null || outputFile.isEmpty()) {
            throw new InvalidConfigurationException("Missing outputFile-" + OUTPUT_FILE_LOCATION);
        }
        return outputFile;
    }


    public void run() {
        try {
            MovieListDownloader movieListDownloader = getDownloaderClass().newInstance();
            movieListDownloader.init(this);
            List<Movie> allMovies = movieListDownloader.getListOfMovies();
            ViewsDetailDownloader downloader = getViewershipDownloaderClass().newInstance();
            downloader.init(this);
            downloader.start(allMovies);
            List<MovieViews> sortedViews = downloader.getMergedList();

            Writer writer = getOutputWriterClass().newInstance();
            writer.init(this);
            writer.write(sortedViews);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
