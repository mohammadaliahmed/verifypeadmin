package com.appsinventiv.verifypeadmin.Models;

public class BannerModel {
    String id, message, imageUrl,url;

    public BannerModel(String id, String message, String imageUrl, String url) {
        this.id = id;
        this.message = message;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BannerModel() {

    }
}
