package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myweather.Adapters.ForecastAdapter;
import com.example.myweather.Models.ForecastModel;
import com.example.myweather.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding bind;
    RequestQueue queue;
    String baseUrl = "https://api.weatherapi.com/v1/";
    String requestType, query, url;
    String key = "b97555c325c74f7fa4c71724232311";
    ArrayList<ForecastModel> forecastModels;
    ForecastAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        queue = Volley.newRequestQueue(this);
        query = "dibrugarh";
        url = baseUrl+"forecast.json?key="+key+"&q="+query;
        makeJSONRequest(url);
        bind.allForeCast.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        bind.refreshListener.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (url != null){
                    makeJSONRequest(url);
                }
                bind.refreshListener.setRefreshing(false);
            }
        });

        bind.searchV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                query = str;
                url = baseUrl+"forecast.json?key="+key+"&q="+query;
                makeJSONRequest(url);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    private String calculateDateFormate(String originalTime){
        try {
            SimpleDateFormat _24hdf = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12hdf = new SimpleDateFormat("hh:mm a");
            Date dateObj = _24hdf.parse(originalTime);
            return _12hdf.format(dateObj);
        }catch (Exception e){
            Toast.makeText(this, "Time exception occured !", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void makeJSONRequest(String url) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String name,region, wind_deg, wind_dir,wind_rate, condition, cond_icon, humidity, uv, sunrise, sunset,isDayNight;

                        if (response.isNull("error")){
                            try {
                                isDayNight = response.getJSONObject("current").getString("is_day");
                                humidity = response.getJSONObject("current").getString("humidity");
                                uv = response.getJSONObject("current").getString("uv");
                                condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                                cond_icon = "https:"+response.getJSONObject("current").getJSONObject("condition").getString("icon");
                                wind_rate = response.getJSONObject("current").getString("wind_kph");
                                wind_dir = response.getJSONObject("current").getString("wind_dir");
                                wind_deg = response.getJSONObject("current").getString("wind_degree");
                                name = response.getJSONObject("location").getString("name");
                                region = response.getJSONObject("location").getString("region");

                                sunrise = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunrise");
                                sunset = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunset");

                                if (Integer.valueOf(isDayNight) == 1){
                                    bind.weatherIcon.setImageResource(R.drawable.cloudy);
                                    bind.conditionStatus.setText(condition+" Day");
                                    Toast.makeText(MainActivity.this, "Day !", Toast.LENGTH_SHORT).show();
                                }else{
                                    bind.weatherIcon.setImageResource(R.drawable.night_theme);
                                    bind.conditionStatus.setText(condition+" Night");
                                    Toast.makeText(MainActivity.this, "Night !", Toast.LENGTH_SHORT).show();
                                }
                                String for_icon = null;
                                forecastModels = new ArrayList<>();
                                int forecastSize = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour").length();
                                for (int i =0;i<forecastSize;i++){
                                    String for_cond, for_time;
                                    for_cond = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour").getJSONObject(i).getJSONObject("condition").getString("text");
                                    for_icon = "https:"+response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour").getJSONObject(i).getJSONObject("condition").getString("icon");
                                    for_time = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour").getJSONObject(i).getString("time");
                                    String[] ac_time = for_time.split(" ",2);
                                    for_time = calculateDateFormate(ac_time[1]);
                                    forecastModels.add(new ForecastModel(for_icon,for_time, for_cond));
                                }
                                Glide.with(MainActivity.this).load(Uri.parse(cond_icon)).into(bind.weatherIcon);
                                adapter = new ForecastAdapter(MainActivity.this, forecastModels);
                                bind.allForeCast.setAdapter(adapter);
                                bind.allForeCast.scrollToPosition(12);
                                bind.humidityS.setText(humidity+"%");
                                bind.uvS.setText(uv+" of 10");
                                bind.sunriseS.setText(sunrise);
                                bind.sunsetS.setText(sunset);
                                bind.tmp.setText(response.getJSONObject("current").getString("temp_c")+"°C");
                                bind.tag.setText(name+", "+region);
                                bind.windStatus.setText("Winds "+wind_deg+" deg "+wind_dir+" at "+wind_rate+ " km/h");
                                Glide.with(MainActivity.this).load(Uri.parse(cond_icon)).into(bind.conditionIcon);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "No records found !", Toast.LENGTH_SHORT).show();
//                            bind.refreshListener.setVisibility(View.GONE);
//                            bind.recordsErrorPage.setVisibility(View.VISIBLE);
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }
}