package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout HomeRL;
    private ProgressBar LoadingPB;
    private ImageView BackIV,SearchIV,IconIV;
    private TextView cityNameTV,TemperatureTV,ConditionTV;
    private TextInputEditText CityEdt;
    private RecyclerView WeatherRV;
    private WeatherRVAdaptor weatherRVAdaptor;
    private ArrayList<WaetherRVModel> waetherRVModelArrayList;
    private LocationManager locationManager;
    private int PERMISSION_CODE=1;
    private  String CityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        HomeRL=findViewById(R.id.RLHome);
        LoadingPB=findViewById(R.id.PBLaoding);
        BackIV=findViewById(R.id.IVBack);
        SearchIV=findViewById(R.id.IVSearch);
        IconIV=findViewById(R.id.IVIcon);
        cityNameTV=findViewById(R.id.TVCityName);
        TemperatureTV=findViewById(R.id.TVTemperature);
        ConditionTV=findViewById(R.id.TVCondition);
        WeatherRV=findViewById(R.id.RVWeather);
        CityEdt=findViewById(R.id.EDtCity);
        waetherRVModelArrayList=new ArrayList<>();
        weatherRVAdaptor=new WeatherRVAdaptor(this,waetherRVModelArrayList);
        WeatherRV.setAdapter(weatherRVAdaptor);

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        CityName=getCityName(location.getLongitude(),location.getLatitude());
        getWeatherinfo(CityName);
        SearchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = CityEdt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "please enter city name", Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(CityName);
                     getWeatherinfo(city);

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "permission grandted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "please provide the permission", Toast.LENGTH_SHORT).show();
             finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String CityName="Not Found ";
        Geocoder gcd= new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses= gcd.getFromLocation(longitude,latitude,10);
            for(Address ads:addresses) {
                if (ads != null){
                    String city= ads.getLocality();
                    if(city!=null&& !city.equals("")){
                        CityName=city;
                    }else{
                        Log.d("TAG"," CITY NOT FOUND");
                        Toast.makeText(this, "user city not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
         }
        return CityName;
    }


    private void getWeatherinfo(String CityName){

        //write the url of weather api//
        String url="http://api.weatherapi.com/v1/forecast.json?key=9fe86933f8a548148bd151616230311&q="+CityName+"&days=1&aqi=yes&alerts=yes";
       cityNameTV.setText(CityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LoadingPB.setVisibility(View.GONE);
                HomeRL.setVisibility(View.VISIBLE);
                waetherRVModelArrayList.clear();
              try {
                  String temperature = response.getJSONObject("current").getString("temp_c");
                  TemperatureTV.setText(temperature+"°c");
                  int isDay=response.getJSONObject("current").getInt("is_day");
                  String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                  String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                  Picasso.get().load("http:".concat(conditionIcon)).into(IconIV);
                  ConditionTV.setText(condition);
                  if(isDay==1){
                      Picasso.get().load("C:\\Users\\gouta\\OneDrive\\Desktop\\Downloads\\pawel-nolbert-xe-ss5Tg2mo-unsplash.jpg").into(BackIV);//Url of image of morninig
                  }else{
                      Picasso.get().load("C:\\Users\\gouta\\OneDrive\\Desktop\\Downloads\\sebastian-garcia-o8SzLhKUEBU-unsplash.jpg").into(BackIV);//Url of image of evening
                  }
                JSONObject forcasteObj= response.getJSONObject("forcaste");
                  JSONObject forcasteO=forcasteObj.getJSONArray("forcasteday").getJSONObject(0);
                  JSONArray hourArray=forcasteO.getJSONArray("hour");

                  for(int i=0;i<hourArray.length();i++){
                      JSONObject hourObj=hourArray.getJSONObject(i);
                      String time= hourObj.getString("time");
                      String temper= hourObj.getString("temp_c");
                      String img= hourObj.getJSONObject("condition").getString("icon");
                      String wind= hourObj.getString("wind_km/h");
                       waetherRVModelArrayList.add(new WaetherRVModel(time,temper,img,wind));

                  }
                  weatherRVAdaptor.notifyDataSetChanged();

              }catch (JSONException e){
                  e.printStackTrace();
              }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }
}






