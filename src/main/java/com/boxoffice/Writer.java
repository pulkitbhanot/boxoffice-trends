package com.boxoffice;

import java.util.List;

/**
 * Created by pulkit on 3/10/15.
 */
public interface Writer {
    void init(Configuration configuration) throws Exception;

    void write(List<MovieViews> sortedViews) throws RecordPersistenceException;
    
}
