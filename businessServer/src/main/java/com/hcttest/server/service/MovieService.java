package com.hcttest.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcttest.server.model.core.Movie;
import com.hcttest.server.model.recom.Recommendation;
import com.hcttest.server.utils.Constant;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;


    private MongoCollection<Document> movieCollection;

    private MongoCollection<Document> getMovieCollection(){
        if (null == movieCollection)
            this.movieCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_MOVIE_COLLECTION);
        return this.movieCollection;
    }

    //将Movie转换成一个Document
    private Document movieToDocument(Movie movie){

        try {
            Document document = Document.parse(objectMapper.writeValueAsString(movie));
            return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    //将Docum转换成movie
    private Movie documentToMovie(Document document){
        try {
            Movie movie = objectMapper.readValue(JSON.serialize(document),Movie.class);
            Document score = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_AVERAGE_MOVIE).find(Filters.eq("mid",movie.getMid())).first();
            if (score == null || score.isEmpty()){
                movie.setScore(0D);
            }
            else{
                movie.setScore(score.getDouble("avg"));
            }
            return movie;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //根据Mid查找电影
    public List<Movie> getMoviesByMids(List<Integer> ids){
        List<Movie> result = new ArrayList<>();
        FindIterable<Document> documents = getMovieCollection().find(Filters.in("mid",ids));
        for (Document item:documents) {
            result.add(documentToMovie(item));
        }
        return  result;
    }

    //获取电影信息
    public Movie findMovieInfo(int mid){
        Document moviedocument = getMovieCollection().find(new Document("mid", mid)).first();
        if(moviedocument==null || moviedocument.isEmpty()){
            return null;
        }
        return documentToMovie(moviedocument);
    }

}
