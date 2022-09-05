package com.appsinventiv.verifypeadmin.Models;

public class BannerModel {
    String id, message, imageUrl;

    public BannerModel(String id, String message, String imageUrl) {
        this.id = id;
        this.message = message;
        this.imageUrl = imageUrl;
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
