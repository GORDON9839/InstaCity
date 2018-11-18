package com.example.taruc.instacity;

public class FeedsClass{

    public String caption;
    public String date;
    public String postImage;
    public String profileImage;
    public String time;
    public String uid;
    public String userName;

    public FeedsClass(){

    }

    public FeedsClass(String caption, String date, String postImage, String profileImage, String time, String uid, String userName) {
        this.caption = caption;
        this.date = date;
        this.postImage = postImage;
        this.profileImage = profileImage;
        this.time = time;
        this.uid = uid;
        this.userName = userName;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getCaption() {
        return caption;
    }

    public String getDate() {
        return date;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getTime() {
        return time;
    }

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }






}
