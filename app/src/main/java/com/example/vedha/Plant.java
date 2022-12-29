package com.example.vedha;

public class Plant {
    private String PlantName;
    private String ImageUrl;

    public Plant(String plantName, String imageUrl) {
        PlantName = plantName;
        ImageUrl = imageUrl;
    }

    public Plant() {
    }

    public String getPlantName() {
        return PlantName;
    }

    public void setPlantName(String plantName) {

        PlantName = plantName;
    }

    public String getImageUrl() {

        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {

        ImageUrl = imageUrl;
    }
}
