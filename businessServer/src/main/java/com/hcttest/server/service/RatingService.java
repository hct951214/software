package com.hcttest.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcttest.server.model.core.Rating;
import com.hcttest.server.model.core.Tag;
import com.hcttest.server.model.core.User;
import com.hcttest.server.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RatingService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Jedis jedis;



    private MongoCollection<Document> ratingCollection;

    private MongoCollection<Document> getRatingCollection(){
        if (null == ratingCollection)
            this.ratingCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_RATING_COLLECTION);
        return this.ratingCollection;
    }

    private Document ratingToDocument(Rating rating){
        try {
            Document document = Document.parse(objectMapper.writeValueAsString(rating));
            return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Rating documentToRating(Document document){
        try {
            Rating rating = objectMapper.readValue(JSON.serialize(document),Rating.class);
            return rating;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void rateToMovie(Rating rating){
        getRatingCollection().insertOne(ratingToDocument(rating));

        //更新Radis
        //updateRedis(rating);
    }

    private void updateRedis(Rating rating){
        if (jedis.llen("uid"+rating.getUid()) >= Constant.USER_RATING_QUEUE_SIZE){
            jedis.rpop("uid"+rating.getUid());
        }
        jedis.lpush("uid"+rating.getUid(),rating.getMid()+":"+rating.getScore());
    }

    /**
     *根据用户查找电影评分记录
     * @param user
     * @return
     */
    public List<Rating> findRatingByUser(User user){
        List<Rating> ratings = new ArrayList<>();
        FindIterable<Document> documents = getRatingCollection().find(new Document("uid",user.getUid())).sort(Sorts.descending("score"));
        for (Document item:documents) {
            ratings.add(documentToRating(item));
        }
        return ratings;

    }

}
