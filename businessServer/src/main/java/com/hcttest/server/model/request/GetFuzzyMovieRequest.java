package com.hcttest.server.model.request;

public class GetFuzzyMovieRequest {
    private String query;
    private int num;

    public GetFuzzyMovieRequest(String query, int num) {
        this.query = query;
        this.num = num;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
