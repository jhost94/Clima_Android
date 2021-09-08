package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.londonappbrewery.climapm.model.WeatherDataModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    private final String APP_ID = "547f6c353aa99220656ef896bf355869";
    // Time between location updates (5000 milliseconds or 5 seconds)
    private final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    private final float MIN_DISTANCE = 1000;
    private final int REQUEST_CODE = 42;

    // TODO: Set LOCATION_PROVIDER here:
    private final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private final String ACCESS_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    // Member Variables:
    private TextView mCityLabel;
    private ImageView mWeatherImage;
    private TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(event -> {
            Intent myIntent = new Intent(WeatherController.this, ChangeCityActivity.class);
            startActivity(myIntent);
        });
    }


    // TODO: Add onResume() here:

    @Override
    protected void onResume() {
        super.onResume();

        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("City");

        if (city == null) {
            log("Searching by location");
            getWeatherForCurrentLocation();
        } else {
            log("Searching my city: " + city);
            getWeatherForNewCity(city);
        }

    }

    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city){
        prepareRequest(city);
    }

    // TODO: Add getWeatherForCurrentLocation() here:

    private void getWeatherForCurrentLocation() {
        //this is quite dangerous but ok
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                log("LOCATION CHANGED");
                prepareRequest(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                log("STATUS CHANGED");
                Location location = requestLocation(true);
                if (location != null){
                    prepareRequest(location);
                }
            }

            @Override
            public void onProviderEnabled(String s) {
                log("PROVIDER ENABLED");
                Location location = requestLocation(true);
                if (location != null){
                    prepareRequest(location);
                }
            }

            @Override
            public void onProviderDisabled(String s) {
                log("PROVIDER DISABLED");
            }
        };
        requestLocation();
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void prepareRequest(Location location){
        String lon = String.valueOf(location.getLongitude());
        String lat = String.valueOf(location.getLatitude());

        RequestParams params = new RequestParams();
        params.put("lat", lat);
        params.put("lon", lon);

        log("Lon: " + lon);
        log("Lat: " + lat);

        sendRequest(params);
    }

    private void prepareRequest(String city){
        RequestParams params = new RequestParams();

        params.put("q", city);

        sendRequest(params);
    }

    private void sendRequest(RequestParams params){
        params.put("appid", APP_ID);

        AsyncHttpClient req = new AsyncHttpClient();
        log("Sending request to " + WEATHER_URL + "?" + params.toString());
        req.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                log(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                WeatherDataModel wdm = WeatherDataModel.fromJSON(response);
                log("Temp: " + wdm.getTemperature());
                updateUI(wdm);
            }
        });
    }

    private Location requestLocation(boolean doReturn){
        if (ActivityCompat.checkSelfPermission(this, ACCESS_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_LOCATION}, REQUEST_CODE);

            return null;
        }
        return locationManager.getLastKnownLocation(LOCATION_PROVIDER);
    }

    private void requestLocation(){
        if (ActivityCompat.checkSelfPermission(this, ACCESS_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{ACCESS_LOCATION}, REQUEST_CODE);

            return;
        }
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }
    // TODO: Add updateUI() here:

    private void updateUI(WeatherDataModel wdm){
        mTemperatureLabel.setText(wdm.getTemperature());
        mCityLabel.setText(wdm.getCity());
        mWeatherImage.setImageResource(getResources().getIdentifier(wdm.getIconName(), "drawable", getPackageName()));
    }

    // TODO: Add onPause() here:

    @Override
    protected void onPause() {
        super.onPause();

        if (locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        log("Trying to get permitions");
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getWeatherForCurrentLocation();
                log("PERMITED");
            } else {
                log("DENIED");
            }
        } else {
            log("something bad happened");
        }
    }

    //To avoid boilerplate
    private void log(String msg) {
        Log.d("Clima", msg);
    }
}
