package com.unipi.ItineraJava.model;

public class ReviewSummary {
    private double overall_rating;
    private int tot_rev_number;

    public ReviewSummary(double overall_rating, int tot_rev_number) {
        this.overall_rating = overall_rating;
        this.tot_rev_number = tot_rev_number;
    }

    public double getOverall_rating() {
        return overall_rating;
    }

    public void setOverall_rating(double overall_rating) {
        this.overall_rating = overall_rating;
    }

    public int getTot_rev_number() {
        return tot_rev_number;
    }

    public void setTot_rev_number(int tot_rev_number) {
        this.tot_rev_number = tot_rev_number;
    }
}
