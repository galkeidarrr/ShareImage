package com.example.shareimage.Models;

public class NotificationModel {
    private String userIdFrom;
    private String userIdTo;
    private String text;
    private String postId;
    private boolean isPost;

    public NotificationModel(String userIdFrom,String userIdTo, String text, String postId, boolean isPost){
        this.userIdFrom =userIdFrom;
        this.userIdTo=userIdTo;
        this.text=text;
        this.postId=postId;
        this.isPost=isPost;
    }
    public NotificationModel(){}

    public String getUserIdFrom() { return userIdFrom; }

    public void setUserIdFrom(String userIdFrom) { this.userIdFrom = userIdFrom; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getPostId() { return postId; }

    public void setPostId(String postId) { this.postId = postId; }

    public boolean getIsPost() { return isPost; }

    public void setIsPost(boolean isPost) { this.isPost = isPost; }
    public String getUserIdTo() {
        return userIdTo;
    }

    public void setUserIdTo(String userIdTo) {
        this.userIdTo = userIdTo;
    }

}
