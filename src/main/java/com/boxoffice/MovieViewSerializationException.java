package com.boxoffice;

import java.io.IOException;

/**
 * Created by pulkit on 3/10/15.
 */
public class MovieViewSerializationException extends Exception {
    public MovieViewSerializationException(String message, IOException ex) {
        super(message, ex);
    }
}
