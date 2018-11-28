package com.hcttest.server.rest;


import com.hcttest.server.model.core.Movie;
import com.hcttest.server.model.core.Rating;
import com.hcttest.server.model.core.Tag;
import com.hcttest.server.model.core.User;
import com.hcttest.server.model.recom.Recommendation;
import com.hcttest.server.model.request.*;
import com.hcttest.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


//处理电影相关功能
@Controller
@RequestMapping("/rest/movies")
public class MovieRestApi {

    @Autowired
    private RecommenderService recommenderService;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TagService tagService;

    @Autowired
    private RatingService ratingService;

    //首页功能******************

    /**
     * //提供获取时时推荐信息的接口 (冷启动问题)
     * 访问 url:// /rest/movies/stream?username=abc&number=5
     * 返回 {success:true,movie:[]}
     * @param username
     * @param model
     * @return
     */
    @RequestMapping(path = "/stream", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getRealtimeRecommendations(@RequestParam("username") String username, @RequestParam("number") int sum, Model model){
        User user = userService.findUserByUserName(username);
        List<Recommendation> recommendations = recommenderService.getStreamRecsMovies(new GetStreamRecsRequest(user.getUid(),sum));
        if (recommendations.size()==0){
            Random random = new Random();
            int selectgenres = random.nextInt(user.getGenres().size());
            recommendations = recommenderService.getGenresTopMovies(new GetGenresTopMoviesRequest(user.getGenres().get(selectgenres),sum));
        }

        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec:recommendations) {
            ids.add(rec.getMid());
        }

        List<Movie> result = movieService.getMoviesByMids(ids);

        model.addAttribute("success",true);
        model.addAttribute("movie",result);
        return model;
    }

    /**
     *     //离线
     * @param username
     * @param model
     * @return
     */
    @RequestMapping(path = "/offline", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getOfflineRecommendations(@RequestParam("username") String username, @RequestParam("number") int sum,Model model){
        User user = userService.findUserByUserName(username);
        List<Recommendation> recommendations = recommenderService.getUserCFMovies(new GetUserCFMoviesRequest(user.getUid(),sum));
        if (recommendations.size()==0) {
            Random random = new Random();
            int selectgenres = random.nextInt(user.getGenres().size());
            recommendations = recommenderService.getGenresTopMovies(new GetGenresTopMoviesRequest(user.getGenres().get(selectgenres),sum));
        }

        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec:recommendations) {
            ids.add(rec.getMid());
        }

        List<Movie> result = movieService.getMoviesByMids(ids);

        model.addAttribute("success",true);
        model.addAttribute("movie",result);
        return model;
    }

    //热门推荐
    @RequestMapping(path = "/hot", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getHotRecommendations(@RequestParam("number") int num,Model model){

        model.addAttribute("success",true);
        model.addAttribute("movie",recommenderService.getHotRecommendations(new GetHotRecommendationRequest(num)));
        return model;
    }

    //优质电影
    @RequestMapping(path = "/rate", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getRateMoreRecommendations(@RequestParam("number") int num,Model model){

        model.addAttribute("success",true);
        model.addAttribute("movie",recommenderService.getRateMoreMovies(new GetRateMoreMovieRequest(num)));
        return model;
    }

    //最新电影
    @RequestMapping(path = "/new", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getNewRecommendations(@RequestParam("number") int num,Model model){
        model.addAttribute("success",true);
        model.addAttribute("movie",recommenderService.getRateMoreMovies(new GetRateMoreMovieRequest(num)));
        return model;
    }

    //检索功能******************

    //基于名称或描述模糊检索
    @RequestMapping(path = "/query", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getFuzzySearchMovies(@RequestParam("query") String query,@RequestParam("number") int num,Model model){
        model.addAttribute("success",true);
        model.addAttribute("movie",recommenderService.getFuzzyMovies(new GetFuzzyMovieRequest(query,num)));
        return model;
    }


    //电影详细*****************

    //获取单个电影数据
    @RequestMapping(path = "/info/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMovieInfo(@PathVariable("mid") int mid, Model model){
        model.addAttribute("success",true);
        model.addAttribute("movie",movieService.findMovieInfo(mid));
        return model;
    }

    //给电影打标签
    @RequestMapping(path = "/addtag/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model addTagsToMovie(@PathVariable("mid") int mid, @RequestParam("username") String username, @RequestParam("tagname") String tagname, Model model){
        User user = userService.findUserByUserName(username);
       Tag tag = new Tag(user.getUid(),mid,tagname,System.currentTimeMillis()/1000);
        tagService.addTagToMovie(tag);
        return model;
    }

    //获取所有标签
    @RequestMapping(path = "/tags/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMovieTags(@PathVariable("mid") int mid, Model model){
        model.addAttribute("success",true);
        model.addAttribute("movie",tagService.getMovieTags(mid));
        return model;
    }

    //获取相似电影
    @RequestMapping(path = "/same/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getSimMoviesRecommendations(@PathVariable("mid") int mid, @RequestParam("number") int num,Model model){
        model.addAttribute("success",true);
        model.addAttribute("movie",recommenderService.getHybirdRecommendations(new GetHybirdRecommendationRequest(0.5,mid,num)));
        return model;
    }

    //给电影打分的功能
    @RequestMapping(path = "/rate/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model rateMovie(@RequestParam("username") String username, @PathVariable("mid") int mid,@RequestParam("score")  double score, Model model){
        User user = userService.findUserByUserName(username);
        Rating rating = new Rating(user.getUid(),mid,score,(int)System.currentTimeMillis()/1000);
        ratingService.rateToMovie(rating);

        return model;
    }

    //根据类别查找电影
    @RequestMapping(path = "/genres", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getGenresMovies(@RequestParam("genres") String genres, @RequestParam("number") int num, Model model){
        model.addAttribute("success",true);
//        List<Recommendation> mids = recommenderService.getGenresMovies(new GetGenresMovieRequest(genres,num));
        model.addAttribute("movie",recommenderService.getGenresMovies(new GetGenresMovieRequest(genres,num)) );

        return model;
    }

    //用户空间页面
    //根据用户查找电影评分记录
    @RequestMapping(path = "/findrate", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getUserRatings(String username, Model model){
        User user = userService.findUserByUserName(username);
        model.addAttribute("success",true);
        model.addAttribute("rating",ratingService.findRatingByUser(user));

        return model;
    }

    //需要获取图表数据
    public Model getUserChart(String username, Model model){
        return model;
    }

}
