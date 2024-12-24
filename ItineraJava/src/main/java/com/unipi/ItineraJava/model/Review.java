package com.unipi.ItineraJava.model;

public class Review {
    private String revId;
    private String user;
    private int stars;
    private String text;
    private long timestamp;
    private boolean reported;

    public String getRevId() {
        return revId;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public int getStars() {
        return stars;
    }

    public boolean isReported() {
        return reported;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setRevId(String revId) {
        this.revId = revId;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }
}
