package models;

public class Rating {
    private int ratingId;
    private int rating;
    private String comment;

    public Rating(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}