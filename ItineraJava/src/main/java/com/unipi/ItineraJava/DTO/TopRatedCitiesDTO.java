package com.unipi.ItineraJava.DTO;

public class TopRatedCitiesDTO {
    private String city;          // Nome della città
    private double averageRating; // Media delle valutazioni per la città
    private int totalPlaces;      // Numero totale di luoghi nella città

    // Costruttore
    public TopRatedCitiesDTO(String city, double averageRating, int totalPlaces) {
        this.city = city;
        this.averageRating = averageRating;
        this.totalPlaces = totalPlaces;
    }

    // Getter e Setter
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalPlaces() {
        return totalPlaces;
    }

    public void setTotalPlaces(int totalPlaces) {
        this.totalPlaces = totalPlaces;
    }

    // Metodo toString per debug
    @Override
    public String toString() {
        return "TopRatedCitiesDTO{" +
                "city='" + city + '\'' +
                ", averageRating=" + averageRating +
                ", totalPlaces=" + totalPlaces +
                '}';
    }
}

