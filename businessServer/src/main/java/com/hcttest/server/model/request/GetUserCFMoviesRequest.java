package com.hcttest.server.model.request;

//获取ALS推荐矩阵
public class GetUserCFMoviesRequest {
    private int uid;
    private int num;

    public GetUserCFMoviesRequest(int uid, int num) {
        this.uid = uid;
        this.num = num;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
