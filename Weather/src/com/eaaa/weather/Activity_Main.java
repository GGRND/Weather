package com.eaaa.weather;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.eaaa.weather.weatherapi.OpenWeatherMap;
import com.eaaa.weather.weatherapi.WeatherAPI;
import com.eaaa.weather.weatherapi.WeatherJSON;
import com.eaaa.weather.weatherapi.WeatherObject;

public class Activity_Main extends Activity {

    //The weatherAPI in use, currently OpenWeatherMap
    private WeatherAPI weatherAPI = OpenWeatherMap.getInstance();

    //Used to keep track of when the device last received an update.
    private long timerForLastUpdate;
    //The time that needs to pass before another update can be done, in seconds.
    private static final long UPDATE_TIME_LIMIT = 5000;

    //ImageView for the weatherIcon
    private ImageView imgImageIcon;

    //TextViews for the weather text values
    private TextView txtTemperature, txtWindSpeed, txtWindDirection, txtRain;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Ensure the screen stays on.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //initialize the View, and it's components.
        initView();

        //Starts an Async task that request the weather from the active #WeatherAPI.
        new AsyncWeatherTask().execute();
    }

    /**
     * Initialize the ImageView and TextViews.
     */
    private void initView() {
        setContentView(R.layout.activity_main);
        //Changing the scaleType in order to enlarge the otherwise smaller images.
        imgImageIcon = (ImageView) findViewById(R.id.imageView);
        imgImageIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        txtTemperature = (TextView) findViewById(R.id.textView21);
        txtWindSpeed = (TextView) findViewById(R.id.textView22);
        txtWindDirection = (TextView) findViewById(R.id.textView23);
        txtRain = (TextView) findViewById(R.id.textView24);
    }

    /**
     * Used to create an async task that once finished will update the ImageView and TextViews.
     */
    private class AsyncWeatherTask extends AsyncTask<String, String, WeatherObject> {

        @Override
        protected WeatherObject doInBackground(String... strings) {
            WeatherObject weather = null;
            //A comparison on when the last request was made is done
            //To make sure the screen doesn't updates too frequently
            if (timerForLastUpdate - System.currentTimeMillis() < -UPDATE_TIME_LIMIT) {
                //currently locked to getting information from Aarhus in Denmark
                weather = WeatherJSON.getWeatherBy("Denmark", "Aarhus", weatherAPI);
                timerForLastUpdate = weather.getTime();
            }

            return weather;
        }

        @Override
        protected void onPostExecute(WeatherObject result) {
            super.onPostExecute(result);

            //Opdates the values the Texts- and ImageView
            if (result != null) {
                result.setText(WeatherObject.TEMPERATURE, txtTemperature);
                result.setText(WeatherObject.WIND_SPEED, txtWindSpeed);
                result.setText(WeatherObject.WIND_DIRECTION, txtWindDirection);
                result.setText(WeatherObject.RAIN, txtRain);
                result.setIcon(imgImageIcon);
            }
        }
    }

}