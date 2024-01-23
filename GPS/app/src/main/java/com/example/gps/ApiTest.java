package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiTest extends AppCompatActivity {

    TextView tv;
    Button getBtn, postBtn, deleteBtn;
    OkHttpClient client;
    String getUrl, postUrl, deleteUrl;
    ListView list;
    ArrayList<String> rec;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);
        tv = findViewById(R.id.txt);
        getBtn = findViewById(R.id.getBtn);
        postBtn = findViewById(R.id.postBtn);
        client = new OkHttpClient();
        list = findViewById(R.id.list);
        deleteBtn = findViewById(R.id.deleteBtn);

        getUrl = "https://empanel.onrender.com/users";
        postUrl = "https://empanel.onrender.com/user";
        deleteUrl = "https://empanel.onrender.com/delete/4";


        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get(getUrl);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post(postUrl);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(deleteUrl);
            }
        });
    }

    public void get(String url){
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Toast.makeText(ApiTest.this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                try {
                    JSONArray result = new JSONArray(response.body().string());
                    Log.d("xxx",result.getJSONObject(0).getString("email"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int len = result.length();
                                rec = new ArrayList<>();
                                for (int i =0;i<len;i++){
                                    rec.add(result.getJSONObject(i).getString("name"));
                                }
                                adapter = new ArrayAdapter<>(ApiTest.this, android.R.layout.simple_list_item_1,rec);
                                list.setAdapter(adapter);
                                tv.setText(String.format("length : %d",result.length()));
                            } catch (JSONException e) {
                                Log.d("uiError",e.getMessage());
                            }
                        }
                    });
                }catch (Exception e){
                    Log.d("eXXX",e.getMessage());
                }
            }
        });
    }
    public void post(String url){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("name","Kalpana Gogoi");
            json.put("email","kalpana@gmail.com");
            json.put("password","kp@123");
        } catch (JSONException e) {
            Log.d("er",e.getMessage());
        }
        RequestBody body = RequestBody.create(String.valueOf(json),JSON);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Toast.makeText(ApiTest.this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    Log.d("xxx",result.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ApiTest.this, "Record inserted !", Toast.LENGTH_SHORT).show();
                            tv.setText(result.toString());
                            Log.d("uierror",result.toString());
                        }
                    });
                }catch (Exception e){
                    Log.d("eee",e.getMessage());
                }
            }
        });
    }
    public void delete(String url){
        Request request = new Request.Builder().url(url).delete().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("delete",e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tv.setText(response.body().string());
                        } catch (IOException e) {
                            Log.d("resErr",e.getMessage());
                        }
                    }
                });
            }
        });
    }

}