package com.londonappbrewery.climapm.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.sql.StatementEvent;

public class WeatherDataModel {

    // TODO: Declare the member variables here
    private String temperature;
    private int condition;
    private String city;
    private String iconName;

    // TODO: Create a WeatherDataModel from a JSON:
    public static WeatherDataModel fromJSON(JSONObject jsonObject){
        WeatherDataModel edm = new WeatherDataModel();
        try {
            edm.city = jsonObject.getString("name");
            edm.condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            edm.temperature = String.valueOf(Math.round((jsonObject.getJSONObject("main").getDouble("temp") - 273.15) * 10.0)/10.0);
            edm.iconName = updateWeatherIcon(edm.condition);
        } catch (JSONException e) {
            Log.d("Clima", "fromJSON: " + e.getLocalizedMessage());
        }

        return edm;
    }

    private WeatherDataModel(){}

    public WeatherDataModel(String temperature, int condition, String city, String iconName){
        this.temperature = temperature;
        this.condition = condition;
        this.city = city;
        this.iconName = iconName;
    }

    // TODO: Uncomment to this to get the weather image name from the condition:
    private static String updateWeatherIcon(int condition) {
        /** Lets do it MY way
        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
         */

        //Just a smaller alias, because this is gonna be a looong journey....
        int c = condition;

        return c >= 0 && c < 300 ? "tstorm1" :
                c >= 300 && c < 500 ? "light_rain" :
                        c >= 500 && c < 600 ? "shower3" :
                                c >= 600 && c <= 700 ? "snow4" :
                                        c >= 701 && c <= 771 ? "fog" :
                                                c >= 772 && c < 800 ? "tstorm3" :
                                                        c == 800 ? "sunny" :
                                                                c >= 801 && c <= 804 ? "cloudy2" :
                                                                        c >= 900 && c <= 902 ? "storm3" :
                                                                                c == 903 ? "snow5" :
                                                                                        c == 904 ? "sunny" :
                                                                                                c >= 905 && c <= 1000 ? "tstorm3" :
                                                                                                        "dunno";
    }

    // TODO: Create getter methods for temperature, city, and icon name:


    public String getCity() {
        return city;
    }

    public String getIconName() {
        return iconName;
    }

    public String getTemperature() {
        return temperature + "ºC";
    }
}
