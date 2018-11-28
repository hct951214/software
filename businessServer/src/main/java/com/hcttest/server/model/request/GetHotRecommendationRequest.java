package com.hcttest.server.model.request;

public class GetHotRecommendationRequest {
    private int num;

    public GetHotRecommendationRequest(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
