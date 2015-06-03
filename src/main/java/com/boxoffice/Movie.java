package com.boxoffice;

/**
 * Created by pulkit on 3/10/15.
 */
public class Movie {
    private String link;
    private String name;

    public Movie(String link, String name) {
        this.link = link;
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    private String apply(String name) {
        return name;
    }
}
