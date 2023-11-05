package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;

public class WeatherRVAdaptor extends RecyclerView.Adapter<WeatherRVAdaptor.ViewHolder> {
    private Context context;
    private ArrayList<WaetherRVModel> waetherRVModelArrayList;

    public WeatherRVAdaptor(Context context, ArrayList<WaetherRVModel> waetherRVModelArrayList) {
        this.context = context;
        this.waetherRVModelArrayList = waetherRVModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(context).inflate(R.layout.weatherrv_item,parent,false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdaptor.ViewHolder holder, int position) {
        WaetherRVModel model=waetherRVModelArrayList.get(position);
        holder.TemperatureTV.setText(model.getTemperature()+ "°c");
        Picasso.get().load("http".concat(model.getIcon())).into(holder.ConditionIV);
        holder.WindSpeedTV.setText(model.getWindspeed()+ "Km/h");
        SimpleDateFormat input= new SimpleDateFormat("yyyy-MM-dd hh:mn");
        SimpleDateFormat output= new SimpleDateFormat("hh:mn aa");
        try {
            Date t =input.parse(model.getTime());
            holder.TimeTV.setText(output.format(t));

        }catch(ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TimeTV,TemperatureTV,WindSpeedTV;
        private ImageView ConditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            WindSpeedTV=itemView.findViewById(R.id.TVWindspeed);
            TimeTV=itemView.findViewById(R.id.TVTime);
            TemperatureTV=itemView.findViewById(R.id.TVTemperature);
            ConditionIV=itemView.findViewById(R.id.IVCondition);
        }
    }
}
