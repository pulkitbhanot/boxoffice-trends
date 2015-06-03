package com.boxoffice;

/**
 * Created by pulkit on 3/10/15.
 */
public class MovieViews {
    private int views;
    private String name;

    public MovieViews() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getViews() {
        return views;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "MovieViews{" +
                "views=" + views +
                ", name='" + name + '\'' +
                '}';
    }
}
