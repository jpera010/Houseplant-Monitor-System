package com.jaynew.houseplantmonitor;

public class Plant {                    //Plant class to keep track of data of plants, updated by FirebaseHelper
    private String name;
    private int temperature;
    private int moisture_level;


    public Plant(String name, int temperature, int moisture_level) {
        this.name = name;
        this.temperature = temperature;
        this.moisture_level = moisture_level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getMoisture() {
        return moisture_level;
    }

    public void setMoisture(int moisture_level) {
        this.moisture_level = moisture_level;
    }
}
