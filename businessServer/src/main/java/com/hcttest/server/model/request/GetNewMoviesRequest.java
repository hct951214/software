package com.hcttest.server.model.request;

public class GetNewMoviesRequest {
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public GetNewMoviesRequest(int num) {
        this.num = num;
    }
}