package com.boxoffice;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.StringReader;


/**
 * Created by pulkit on 3/10/15.
 */
public class DefaultMovieViewSerializer implements MovieViewSerializer {

    private final Gson gson;

    public DefaultMovieViewSerializer() {
        gson = new Gson();
    }

    @Override
    public MovieViews deserializeFromString(String jsonString) throws MovieViewSerializationException {
        if (!jsonString.isEmpty()) {
            JsonReader reader = new JsonReader(new StringReader(jsonString));
            try {
                return createAndPopulateViewObject(reader);
            } catch (IOException ex) {
                throw new MovieViewSerializationException("Unable to deserialize record", ex);
            }
        } else {
            return null;
        }
    }

    private MovieViews createAndPopulateViewObject(JsonReader reader) throws IOException {
        reader.beginObject();
        int views = 0;
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.BEGIN_OBJECT)) {
                reader.beginObject();
                JsonToken nextToken = reader.peek();
                if ("daily_views".equals(nextToken.name())) {
                    //read all the values
                    reader.beginObject();
                    while (reader.peek() != JsonToken.END_OBJECT) {
                        reader.peek();
                        views += reader.nextLong();
                    }
                } else {
                    //this is some token we have not supported log the token and iterate till the corresponding endObject
                    while (reader.peek() != JsonToken.END_OBJECT) {

                    }
                }
            } else if (token.equals(JsonToken.NAME)) {
                String tokenName = reader.nextName();
                if ("daily_views".equals(tokenName)) {
                    //read all the values
                    reader.beginObject();
                    while (reader.peek() != JsonToken.END_OBJECT) {
                        reader.nextName();
                        views += reader.nextInt();
                    }
                    reader.endObject();
                } else {
                    //this is some token we have not supported skip it
                    reader.skipValue();

                }
            }
        }
        MovieViews movieViews = new MovieViews();
        movieViews.setViews(views);
        return movieViews;

    }

    @Override
    public String serializeToJson(MovieViews movieViews) throws MovieViewSerializationException {
        return gson.toJson(movieViews);
    }
}
