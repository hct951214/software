package com.hcttest.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcttest.server.model.core.User;
import com.hcttest.server.model.request.LoginUserRequest;
import com.hcttest.server.model.request.RegisterUserRequest;
import com.hcttest.server.model.request.UpdateUserRequest;
import com.hcttest.server.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;

//对于用户具体处理业务服务类
@Service
public class UserService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> getUserCollection(){
        if (null == userCollection)
            this.userCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_USER_COLLECTION);
        return this.userCollection;
    }

    //将User转换成一个Document
    private Document userToDocument(User user){

        try {
            Document document = Document.parse(objectMapper.writeValueAsString(user));
            return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    //将Docum转换成user
    private User documentToUser(Document document){
        try {
            User user = objectMapper.readValue(JSON.serialize(document),User.class);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 提供注册用户服务
     * @param request
     * @return
     */
    public boolean registerUser(RegisterUserRequest request){

        //判断是否有相同的用户注册
        if (getUserCollection().find(new Document("username",request.getUsername())).first() != null)
            return false;

        //创建一个用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirst(true);


        //插入一个用户
        Document document = userToDocument(user);
        if (null == document)
            return false;
        getUserCollection().insertOne(userToDocument(user));
        return true;

    }

    /**
     * 用于提供用户登陆
     * @param request
     * @return
     */
    public boolean loginUser(LoginUserRequest request){
        //需要找到用户
        Document document = getUserCollection().find(new Document("username",request.getUsername())).first();
        if (null == document)
            return false;

        User user = documentToUser(document);
        if (null == user)
            return false;

        //验证密码
        return user.getPassword().compareTo(request.getPassword()) == 0 ;
    }

    /**
     * 用于更新用户第一次登陆的喜爱电影类别
     * @param request
     * @return
     */
    public boolean updateUserGenres(UpdateUserRequest request){
        getUserCollection().updateOne(new Document("username",request.getUsername()),new Document().append("$set",new Document("$genres",request.getGenres())));
        getUserCollection().updateOne(new Document("username",request.getUsername()),new Document().append("$set",new Document("$first",false)));
        return true;
    }


    public User findUserByUserName(String username){
        Document document = getUserCollection().find(new Document("username",username)).first();
        if (null == document || document.isEmpty()){
            return null;
        }
        return documentToUser(document);
    }

}
