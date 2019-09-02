package com.example.shareimage.Models;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String id;
    private String email;
    private String password;
    private String userName;
    private String fullName;
    private String imageUrl;
    private String bio;

    public List<String> follows;
    public List<String> followers;




    public UserModel(String id, String email, String password, String userName, String fullName, String imageUrl, String bio) {
        this.id =id;
        this.email=email;
        this.password=password;
        this.userName = userName;
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.bio = bio;
        follows=new ArrayList<>();
        followers=new ArrayList<>();

    }

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullname) {
        this.fullName = fullname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageurl) {
        this.imageUrl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.bio = password;
    }
    public List<String> getFollows() {
        return follows;
    }

    public void setFollows(List<String> follows) {
        this.follows = follows;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
}