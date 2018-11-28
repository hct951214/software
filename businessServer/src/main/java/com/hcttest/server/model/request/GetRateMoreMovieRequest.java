package com.hcttest.server.model.request;

public class GetRateMoreMovieRequest {
    private int num;

    public GetRateMoreMovieRequest(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
