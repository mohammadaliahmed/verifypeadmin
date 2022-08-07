package com.appsinventiv.verifypeadmin.Models;

import java.util.List;

public class ChatModel {
    String id, message, senderId, sendTo,msgType;
    long time;
    List<ObjectModel> objectList;


    public ChatModel() {
    }

    public ChatModel(String id, String message, String senderId, String sendTo,String msgType,
                     List<ObjectModel> objectList, long time) {
        this.id = id;
        this.objectList = objectList;
        this.message = message;
        this.msgType = msgType;
        this.senderId = senderId;
        this.sendTo = sendTo;
        this.time = time;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public List<ObjectModel> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<ObjectModel> objectList) {
        this.objectList = objectList;
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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
