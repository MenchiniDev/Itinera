package com.unipi.ItineraJava.model;

public class ReviewSummary {
    private double averageRating;
    private int totalReviews;

    public ReviewSummary(double averageRating, int totalReviews) {
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }
}
