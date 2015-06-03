package com.boxoffice;

/**
 * Created by pulkit on 3/10/15.
 */
public class RecordPersistenceException extends Exception {
    public RecordPersistenceException(String message, Exception ex) {
        super(message, ex);
    }
}
