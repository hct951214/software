package com.hcttest.server.rest;


import org.springframework.ui.Model;


//处理电影相关功能
public class MovieRestApi {

    //首页功能******************

    //提供获取时时推荐信息的接口
    public Model getRealtimeRecommendations(String username, Model model){
        return null;
    }

    //离线
    public Model getOfflineRecommendations(String username, Model model){
        return null;
    }

    //热门推荐
    public Model getHotRecommendations(Model model){
        return null;
    }

    //优质电影
    public Model getRateMoreRecommendations(Model model){
        return null;
    }

    //最新电影
    public Model getNewRecommendations(Model model){
        return null;
    }

    //检索功能******************

    //基于名称或描述模糊检索
    public Model getFuzzySearchMovies(String query,Model model){
        return null;
    }

    //电影详细*****************

    //获取单个电影数据
    public Model getMovieInfo(int mid, Model model){
        return null;
    }

    //给电影打标签
    public Model addTagsToMovie(int mid, String tag, Model model){
        return null;
    }

    //获取所有标签
    public Model getMovieTags(int mid, Model model){
        return null;
    }

    //获取相似电影
    public Model getSimMoviesRecommendations(int mid, Model model){
        return null;
    }

    //给电影打分的功能
    public Model rateMovie(int mid, double score, Model model){
        return null;
    }

    //根据类别查找电影
    public Model getGenresMovies(String genres, Model model){
        return null;
    }

    //用户空间页面
    //根据用户查找电影评分记录
    public Model getUserRatings(String username, Model model){
        return null;
    }

    //需要获取图表数据
    public Model getUserChart(String username, Model model){
        return null;
    }

}
