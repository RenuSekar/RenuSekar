package com.example.getfeed;

import java.util.List;

public class Shop {
    private String shopName;
    private List<String> questions;
    private List<String> ratings;

    public Shop() {
        // Required empty public constructor for Firestore
    }

    public Shop(String shopName, List<String> questions, List<String> ratings) {
        this.shopName = shopName;
        this.questions = questions;
        this.ratings = ratings;
    }

    public Shop(String shopName){
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getRatings() {
        return ratings;
    }

    public void setRatings(List<String> ratings) {
        this.ratings = ratings;
    }
}
