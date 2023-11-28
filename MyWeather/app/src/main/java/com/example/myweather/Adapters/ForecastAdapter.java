package com.example.myweather.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myweather.Models.ForecastModel;
import com.example.myweather.R;

import java.util.ArrayList;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.MyHolder> {
    Context context;
    ArrayList<ForecastModel> forecastModels;

    public ForecastAdapter(Context context, ArrayList<ForecastModel> forecastModels) {
        this.context = context;
        this.forecastModels = forecastModels;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.forecast_layout,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Glide.with(context).load(Uri.parse(forecastModels.get(position).getIconUrl())).into(holder.icon);
        holder.time.setText(forecastModels.get(position).getTime());
        holder.condition.setText(forecastModels.get(position).getCondition());
    }

    @Override
    public int getItemCount() {
        return forecastModels.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView time, condition;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.for_icon);
            time = itemView.findViewById(R.id.for_time);
            condition = itemView.findViewById(R.id.for_cond);
        }
    }
}
