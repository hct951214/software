package com.hcttest.server.service;

import com.hcttest.server.model.recom.Recommendation;
import com.hcttest.server.model.request.*;
import com.hcttest.server.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RecommenderService {

    @Autowired
    private MongoClient mongoClient;

    private MongoDatabase mongoDatabase;
    private MongoDatabase getMongoDatabase(){
        if (mongoDatabase == null)
            mongoDatabase = mongoClient.getDatabase(Constant.MONGO_DATABASE);
        return mongoDatabase;
    }

    @Autowired
    private TransportClient esClient;

    /**
     *混合推荐结果，相似电影
     * @param request
     * @return
     */
    public List<Recommendation> getHybirdRecommendations(GetHybirdRecommendationRequest request){

        //获取相似矩阵中的结果
        List<Recommendation> ItemCF = getItemCFMovies(new GetItemCFMoviesRequest(request.getMid(),request.getNum()));


        //电影 内容推荐
        List<Recommendation> contentBased = getContentBasedRecommendation(new GetContentBasedRecommendationRequest(request.getMid(),request.getNum()));


        List<Recommendation> result = new ArrayList<>();
        result.addAll(ItemCF.subList(0,(int)Math.round(ItemCF.size()*request.getCfShare())));
        result.addAll(contentBased.subList(0,(int)Math.round(contentBased.size()*(1-request.getCfShare()))));
        //返回结果

        return result;
    }


    public List<Recommendation> getItemCFMovies(GetItemCFMoviesRequest request){
        MongoCollection<Document> itemRecsCollection = getMongoDatabase().getCollection(Constant.MONGO_MOVIE_RECS_COLLECTION);
        Document document = itemRecsCollection.find(new Document("mid",request.getMid())).first();
        return parseDocument(document,request.getNum());
    }


    /**
     * 获取当前用户的实时推荐
     * @param request
     * @return
     */
    public List<Recommendation> getStreamRecsMovies(GetStreamRecsRequest request){

        MongoCollection<Document> streamRecsCollection = getMongoDatabase().getCollection(Constant.MONGO_STREAM_RECS_COLLECTION);
        Document document = streamRecsCollection.find(new Document("uid",request.getUid())).first();
        List<Recommendation> result = new ArrayList<>();
        for (String str:document.getString("recs").split("\\|")) {
            String para[] = str.split(":");
            result.add(new Recommendation(Integer.parseInt(para[0]),Double.parseDouble(para[1])));
        }
        return result.subList(0,result.size()>request.getNum()?request.getNum():result.size());
    }


    /**
     * 返回ALS离线推荐结果
     * @param request
     * @return
     */
    public List<Recommendation> getUserCFMovies(GetUserCFMoviesRequest request){
        MongoCollection<Document> userRecsCollection = getMongoDatabase().getCollection(Constant.MONGO_STREAM_RECS_COLLECTION);
        Document document = userRecsCollection.find(new Document("uid",request.getUid())).first();
        return parseDocument(document,request.getNum());
    }

    //用于解析Document
    private List<Recommendation> parseDocument(Document document, int sum){
        List<Recommendation> result = new ArrayList<>();
        if (null == document|| document.isEmpty()){
            return result;
        }
        ArrayList<Document> documents = document.get("uid",ArrayList.class);
        for (Document doc:documents) {
            result.add(new Recommendation(doc.getInteger("rid"),doc.getDouble("r")));
        }
        return result.subList(0,result.size()>sum?sum:result.size());
    }

    /**
     * //获得内容推荐借过
     * @param request
     * @return
     */
    public List<Recommendation> getContentBasedRecommendation(GetContentBasedRecommendationRequest request){
        MoreLikeThisQueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(new MoreLikeThisQueryBuilder.Item[]{
                new MoreLikeThisQueryBuilder.Item(Constant.ES_INDEX,Constant.ES_TYPE,String.valueOf(request.getMid()))
        });
        SearchResponse response =  esClient.prepareSearch(Constant.ES_INDEX).setQuery(queryBuilder).setSize(request.getNum()).execute().actionGet();
        return parseResponse(response);
    }


    //用于解析Response
    private List<Recommendation> parseResponse(SearchResponse response) {
        List<Recommendation> recommendations = new ArrayList<>();
        for (SearchHit hit:response.getHits()) {
            Map<String,Object> hitcontent =  hit.getSourceAsMap();
            recommendations.add(new Recommendation((int)hitcontent.get("mid"),0D));
        }
        return recommendations;
    }

    /**
     * 获取电影类别top电影
     * @param request
     * @return
     */
    public List<Recommendation> getGenresTopMovies(GetGenresTopMoviesRequest request){
        Document document = getMongoDatabase().getCollection(Constant.MONGO_GENRES_TOP_MOVIES).find(new Document("genres",request.getGenres())).first();
        List<Recommendation> recommendations = new ArrayList<>();
        if (document == null || document.isEmpty()){
            return recommendations;
        }
        return parseDocument(document,request.getNum());
    }


    /**
     * 最热电影
     * @param request
     * @return
     */
    public List<Recommendation> getHotRecommendations(GetHotRecommendationRequest request){
        FindIterable<Document> documents = getMongoDatabase().getCollection(Constant.MONGO_RATE_MORE_RECENTLY_MOVIES).find().sort(Sorts.descending("yeahmonth"));
        List<Recommendation> recommendations = new ArrayList<>();
        for (Document item:documents) {
            recommendations.add(new Recommendation(item.getInteger("mid"),0D));

        }
        return  recommendations.subList(0,recommendations.size()>request.getNum()?request.getNum():recommendations.size());
    }


    /**
     *获取优质电影
     * @param request
     * @return
     */
    public List<Recommendation> getRateMoreMovies(GetRateMoreMovieRequest request){
        FindIterable<Document> documents = getMongoDatabase().getCollection(Constant.MONGO_RATE_MORE_MOVIES).find().sort(Sorts.descending("count"));
        List<Recommendation> recommendations = new ArrayList<>();
        for (Document item:documents) {
            recommendations.add(new Recommendation(item.getInteger("mid"),0D));

        }
        return  recommendations.subList(0,recommendations.size()>request.getNum()?request.getNum():recommendations.size());

    }


    /**
     * 获取最新电影
     * @param request
     * @return
     */
    public List<Recommendation> getNewMovies(GetNewMoviesRequest request){
        FindIterable<Document> documents = getMongoDatabase().getCollection(Constant.MONGO_MOVIE_COLLECTION).find().sort(Sorts.descending("issue"));
        List<Recommendation> recommendations = new ArrayList<>();
        for (Document item:documents) {
            recommendations.add(new Recommendation(item.getInteger("mid"),0D));

        }
        return  recommendations.subList(0,recommendations.size()>request.getNum()?request.getNum():recommendations.size());

    }

    public List<Recommendation> getFuzzyMovies(GetFuzzyMovieRequest request){
        FuzzyQueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("name",request.getQuery());
        SearchResponse searchResponse = esClient.prepareSearch(Constant.ES_INDEX).setQuery(queryBuilder).setSize(request.getNum()).execute().actionGet();
        return parseResponse(searchResponse);
    }

    public List<Recommendation> getGenresMovies(GetGenresMovieRequest request){
        FuzzyQueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("genres",request.getGenres());
        SearchResponse searchResponse = esClient.prepareSearch(Constant.ES_INDEX).setQuery(queryBuilder).setSize(request.getNum()).execute().actionGet();
        return parseResponse(searchResponse);

    }

}
