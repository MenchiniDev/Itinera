package com.unipi.ItineraJava.DTO;



public class PostSuggestionDto {

    private Long id;
    private String preview;
    private String community;

    public PostSuggestionDto(Long id, String preview, String community) {
        this.id = id;
        this.preview = preview;
        this.community = community;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
