package com.boxoffice;

import java.io.*;
import java.util.List;

/**
 * Created by pulkit on 3/10/15.
 */
public class FileJsonWriter implements Writer {

    private String outputFile;
    private BufferedWriter bufferedWriter;
    private MovieViewSerializer serializer;

    @Override
    public void init(Configuration configuration) throws Exception {
        outputFile = configuration.getOutputFile();
        try {
            serializer = configuration.getOutputViewSerializerClass().newInstance();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(configuration.getOutputFile()), "utf-8"));
        } catch (Exception ex) {
            throw new Exception("Unable to open the file for output", ex);
        }

    }

    @Override
    public void write(List<MovieViews> sortedViews) throws RecordPersistenceException {
        if (bufferedWriter != null) {
            try {
                for (MovieViews currentMovie : sortedViews) {
                    bufferedWriter.write(serializer.serializeToJson(currentMovie));
                }
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (Exception ex) {
                throw new RecordPersistenceException("Unable to write record to outpu", ex);
            }
        }
    }
}
