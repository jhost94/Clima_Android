package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChangeCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);

        final EditText editCityField = findViewById(R.id.queryET);
        final ImageButton backBtn = findViewById(R.id.backButton);

        setFinishEvent(backBtn);
        setEditFieldEvent(editCityField);
    }

    private void setFinishEvent(ImageButton imgBtn) {
        imgBtn.setOnClickListener(event -> {
            finish();
        });
    }

    private void setEditFieldEvent(EditText et){
        et.setOnEditorActionListener((tv, aID, event) -> {
            Intent myIntent = new Intent(ChangeCityActivity.this, WeatherController.class);
            myIntent.putExtra("City", et.getText().toString());
            startActivity(myIntent);
            return true;
        });
    }
}