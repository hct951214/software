package com.hcttest.server.utils;

//定义整个业务系统的常量
public class Constant {
    //********** Mongodb中的表名 ********

    public static final  String MONGO_DATABASE = "DYTJ";


    public static final  String MONGO_MOVIE_COLLECTION = "Movie";

    public static final String MONGO_RATING_COLLECTION = "Rating";

    public static final String MONGO_TAGS_COLLECTION = "Tag";


    //UserTable
    public static final String MONGO_USER_COLLECTION = "User";

    //Average Ratings
    public static final String MONGO_AVERAGE_MOVIE = "AverageMovies";

    //电影类别Top10
    public static final String MONGO_GENRES_TOP_MOVIES = "GenresTopMovies";


    //优质电影表
    public static final String MONGO_RATE_MORE_MOVIES = "RateMoreMovies";

    //最热电影表
    public static final String MONGO_RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentMovies";

    //用户推荐矩阵
    public static final String MONGO_USER_RECS_COLLECTION = "UserRecs";

    //电影相似矩阵
    public static final String MONGO_MOVIE_RECS_COLLECTION = "MovieRecs";

    //时时推荐表
    public static final String MONGO_STREAM_RECS_COLLECTION = "StreamRecs";

    //**************ES*************

    //index
    public static final String ES_INDEX = "dytj";

    //type
    public static final String ES_TYPE = "Movie";

    //Redis

    public static final int USER_RATING_QUEUE_SIZE = 20;

    //LOG

    public static final String USER_RATING_LOG_PREFIX = "USER_RATING_LOG_PREFIX:";
}
