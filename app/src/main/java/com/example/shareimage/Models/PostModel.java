package com.example.shareimage.Models;

import java.util.ArrayList;
import java.util.List;

public class PostModel {
    private String postId;
    private String postImage;
    private String description;
    private String publisher;


    public ArrayList<String> likes;


    public PostModel(String postId, String postImage, String description, String publisher) {
        this.postId = postId;
        this.postImage = postImage;
        this.description = description;
        this.publisher = publisher;

        likes=new ArrayList<>();

    }

    public PostModel() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }



}
