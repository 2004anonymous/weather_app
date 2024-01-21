package com.example.myweather.ApiRequest;

import static com.example.myweather.utils.Utility.key;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myweather.Adapters.ForecastAdapter;
import com.example.myweather.Models.ForecastModel;
import com.example.myweather.R;
import com.example.myweather.databinding.ActivityMainBinding;
import com.example.myweather.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Request {
    static ArrayList<ForecastModel> forecastModels;
    static RequestQueue queue;
    static Context ctx;
    static ForecastAdapter adapter;
    public static void makeJSONRequest(Context context, String url, ActivityMainBinding bind) {
        ctx = context;
        queue = Volley.newRequestQueue(context);
        bind.progBarLayout.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        bind.refreshListener.setVisibility(View.VISIBLE);
                        bind.recordsErrorPage.setVisibility(View.GONE);
                        bind.progBarLayout.setVisibility(View.GONE);
                        String name,region, wind_deg, wind_dir,wind_rate, condition, cond_icon, humidity, uv, sunrise, sunset,isDayNight;
                        Log.d("onResponse",response.toString());
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
                                    Toast.makeText(context, "Day !", Toast.LENGTH_SHORT).show();
                                }else{
                                    bind.weatherIcon.setImageResource(R.drawable.night_theme);
                                    bind.conditionStatus.setText(condition+" Night");
                                    Toast.makeText( context, "Night !", Toast.LENGTH_SHORT).show();
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
                                Glide.with(context).load(Uri.parse(cond_icon)).into(bind.weatherIcon);
                                adapter = new ForecastAdapter(context, forecastModels);
                                bind.allForeCast.setAdapter(adapter);
                                bind.allForeCast.scrollToPosition(12);
                                bind.humidityS.setText(humidity+"%");
                                bind.uvS.setText(uv+" of 10");
                                bind.sunriseS.setText(sunrise);
                                bind.sunsetS.setText(sunset);
                                bind.tmp.setText(response.getJSONObject("current").getString("temp_c")+"°C");
                                bind.tag.setText(name+", "+region);
                                bind.windStatus.setText("Winds "+wind_deg+" deg "+wind_dir+" at "+wind_rate+ " km/h");
                                Glide.with(context).load(Uri.parse(cond_icon)).into(bind.conditionIcon);

                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
                                Log.d("onResponse",e.getMessage());
                            }
                        }else{
                            Toast.makeText(context, "No records found !", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bind.progBarLayout.setVisibility(View.GONE);
                if (error.networkResponse.statusCode == 400 ){
                    bind.refreshListener.setVisibility(View.GONE);
                    bind.recordsErrorPage.setVisibility(View.VISIBLE);
                }else{
                    Utility.showToast(context, "Error !");
                }
            }
        });
        queue.add(request);
    }

    private static String calculateDateFormate(String originalTime){
        try {
            SimpleDateFormat _24hdf = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12hdf = new SimpleDateFormat("hh:mm a");
            Date dateObj = _24hdf.parse(originalTime);
            return _12hdf.format(dateObj);
        }catch (Exception e){
            Utility.showToast(ctx,"Time exception occured !");
        }
        return null;
    }

}
