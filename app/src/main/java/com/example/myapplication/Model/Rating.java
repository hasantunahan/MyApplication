package com.example.myapplication.Model;

public class Rating {
    private String userPhone;
    private String userName;
    private String foodId;
    private String rateValues;
    private String comment;
    private String time;

    public Rating() {
    }

    public Rating(String userPhone, String userName, String foodId, String rateValues, String comment, String time) {
        this.userPhone = userPhone;
        this.userName = userName;
        this.foodId = foodId;
        this.rateValues = rateValues;
        this.comment = comment;
        this.time = time;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValues() {
        return rateValues;
    }

    public void setRateValues(String rateValues) {
        this.rateValues = rateValues;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
