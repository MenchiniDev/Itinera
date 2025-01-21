package com.unipi.ItineraJava.DTO;



public class PostSuggestionDto {

    private String postId;
    private String preview;
    private String community;

    public PostSuggestionDto(String postId, String preview, String community) {
        this.postId = postId;
        this.preview = preview;
        this.community = community;
    }

    public String getId() {
        return postId;
    }

    public void setId(String postId) {
        this.postId = postId;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
