package com.hackathon.recumeet.Models;

public class Like {

    String uId;

    public Like() {
    }

    public Like(String uId) {
        this.uId = uId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
