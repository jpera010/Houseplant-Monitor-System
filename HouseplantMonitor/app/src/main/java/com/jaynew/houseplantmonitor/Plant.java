package com.jaynew.houseplantmonitor;

public class Plant {
    private int temperature;
    private int moisture_level;

    public Plant(int temperature, int moisture_level) {             //Plant class to keep track of data of plants, updated by FirebaseHelper
        this.temperature = temperature;
        this.moisture_level = moisture_level;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getMoisture() {
        return moisture_level;
    }

    public void updateTemperature(int new_temperature) {
        this.temperature = new_temperature;
    }

    public void updateMoisture(int new_moisture_level) {
        this.moisture_level = new_moisture_level;
    }
}
