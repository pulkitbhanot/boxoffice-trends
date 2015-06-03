package com.boxoffice;

/**
 * Created by pulkit on 3/10/15.
 */
public interface MovieViewSerializer {

    MovieViews deserializeFromString(String jsonString) throws MovieViewSerializationException;

    String serializeToJson(MovieViews movieViews) throws MovieViewSerializationException;

}
