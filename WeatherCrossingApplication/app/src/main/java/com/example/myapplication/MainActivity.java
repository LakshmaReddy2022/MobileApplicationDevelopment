package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView currentDate,temperatureInfo,feelsLikeTemp,weatherDescription,WindInfo,humidityInfo,uvIndexInfo,visibilityInfo;
    TextView morningTemp,afternoonTemp,eveningTemp,nightTemp,sunriseInfo,sunsetInfo;
    RecyclerView hourlyRecyclerView;
    ImageView imageView;

    String location = "Chicago, Illinois";
    String unitType = "us";

    Map<String, Object> daysData = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        if(!checkInternet()){
            setContentView(R.layout.no_internet_view);
        }else {
            setContentView(R.layout.activity_main);
            setID();
            getSavedData();
            DataActivity.fetchData(this, location, unitType);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public void saveData(){
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences = getSharedPreferences("PreviousData", MODE_APPEND);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("location", location);
        myEdit.putString("unitType", unitType);
        myEdit.apply();
    }

    public void getSavedData(){
        SharedPreferences sharedPreferences = getSharedPreferences("PreviousData", MODE_PRIVATE);
        location = sharedPreferences.getString("location", "Chicago, Illinois");
        unitType = sharedPreferences.getString("unitType", "us");
    }

    SimpleDateFormat fullDate = new SimpleDateFormat("EEE MMM dd h:mm a, yyyy", Locale.getDefault());
    SimpleDateFormat timeOnly = new SimpleDateFormat("h:mm a", Locale.getDefault());
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    SimpleDateFormat hourOnly = new SimpleDateFormat("h a", Locale.getDefault());

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkInternet(){
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.unit:
                if(checkInternet()){
                    if(unitType.equals("us")){
                        unitType = "metric";
                        item.setIcon(R.drawable.units_c);
                    }else{
                        unitType = "us";
                        item.setIcon(R.drawable.units_f);
                    }
                    saveData();
                    DataActivity.fetchData(this, location, unitType);
                }else{
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.calender:
                if(checkInternet()){
                    Intent intent = new Intent(this, HelperActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DayList", (Serializable) daysData);
                    intent.putExtra("DayList", bundle);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.location:
                if(checkInternet()){
                    final EditText txtUrl = new EditText(this);
                    new AlertDialog.Builder(this)
                            .setTitle("Enter a Location")
                            .setMessage("For US locations, enter as 'City',or 'City,State'.\n"+"For international locations enter as 'City,Country'\n")
                            .setView(txtUrl)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    location = txtUrl.getText().toString();
                                    saveData();
                                    DataFetch();
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                }
                else{
                    Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void DataFetch(){
        DataActivity.fetchData(this, location, unitType);
    }

    SwipeRefreshLayout swipeRefreshLayout;
    HourlyWeatherRecyclerView hourlyWeatherRecyclerView;

    private String getDirection(double degrees) { if (degrees >= 337.5 || degrees < 22.5)
        return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDetails(Map<String, Object> o) {

        Map<String, Object> map = o;
        Map<String,String> currentCondition = (Map<String, String>) map.get("currentCondition");
        String currCondition = currentCondition.get("datetimeEpoch");

        setTitle((CharSequence) map.get("address"));

        currentDate.setText(TimeConversion(currCondition, fullDate));
        imageView.setImageResource(getIcon(currentCondition.get("icon")));
        temperatureInfo.setText(currentCondition.get("temp"));
        humidityInfo.setText("Humidity: "+ currentCondition.get("humidity")+"%");
        uvIndexInfo.setText("UV Index: "+ currentCondition.get("uvindex"));
        feelsLikeTemp.setText("Feels Like " + currentCondition.get("feelsLike"));
        weatherDescription.setText(formatString(currentCondition.get("conditions")+ " ( "+ currentCondition.get("cloudcover") +"% clouds)"));
        WindInfo.setText("Winds: "+getDirection(Double.parseDouble(currentCondition.get("winddir")))+ " at "+ currentCondition.get("windspeed") + " mph " + "gusting to "+currentCondition.get("windgust")+ " mph");
        visibilityInfo.setText("Visibility: "+ currentCondition.get("visibility")+" mi");
        sunriseInfo.setText("Sunrise: "+TimeConversion(currentCondition.get("sunriseEpoch"), timeOnly));
        sunsetInfo.setText("Sunset: "+TimeConversion(currentCondition.get("sunsetEpoch"), timeOnly));
        ArrayList<Map<String, Object>> daysList = (ArrayList<Map<String, Object>>) map.get("daysList");
        ArrayList<Map<String,String>> hourlyArrayList = (ArrayList<Map<String, String>>) daysList.get(0).get("hourlyArrayList");
        morningTemp.setText(hourlyArrayList.get(8).get("temp"));
        afternoonTemp.setText(hourlyArrayList.get(13).get("temp"));
        eveningTemp.setText(hourlyArrayList.get(17).get("temp"));
        nightTemp.setText(hourlyArrayList.get(23).get("temp"));
        daysData = o;

        ArrayList<Map<String, String>> hourlyRecyclerData = new ArrayList<>();
        boolean check = false;
        for(int i = 0; i < daysList.size(); i++){
            if(hourlyRecyclerData.size() == 48)
                break;

            ArrayList<Map<String,String>>  days = (ArrayList<Map<String,String>>) daysList.get(i).get("hourlyArrayList");

            for(int j = 0; j < days.size(); j++) {
                Map<String, String> temp = new HashMap<>();
                if(check) {
                    if (i == 0) {
                        temp.put("Day", "Today");
                    } else {
                        temp.put("Day", TimeConversion(days.get(j).get("hoursDatetimeEpoch"), dayFormat));
                    }
                    temp.put("time", TimeConversion(days.get(j).get("hoursDatetimeEpoch"), timeOnly));
                    temp.put("icon", days.get(j).get("hoursIcon"));
                    temp.put("temp", days.get(j).get("temp"));
                    temp.put("Description", days.get(j).get("condition"));
                    hourlyRecyclerData.add(temp);
                    if (hourlyRecyclerData.size() == 48)
                        break;
                }
                if(TimeConversion(currCondition, hourOnly).equals(TimeConversion(days.get(j).get("hoursDatetimeEpoch"), hourOnly))){
                    check = true;
                }

            }
        }
        hourlyWeatherRecyclerView = new HourlyWeatherRecyclerView(this, hourlyRecyclerData);
        hourlyRecyclerView.setAdapter(hourlyWeatherRecyclerView);
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    public String formatString(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String TimeConversion(String str, SimpleDateFormat date){
        return date.format(new Date(Long.parseLong(str) * 1000));
    }
    public int getIcon(String icon){
        icon = icon.replace("-", "_");
        int iconID =
                this.getResources().getIdentifier(icon, "drawable", this.getPackageName());
        if (iconID == 0) {
            Log.d("Main", "parseCurrentRecord: CANNOT FIND ICON " + icon);
        }
        return iconID;
    }

    public void setID() {
        morningTemp = findViewById(R.id.morningTemprature);
        afternoonTemp = findViewById(R.id.afternoonTemp);
        hourlyRecyclerView = findViewById(R.id.hourlyView);
        eveningTemp = findViewById(R.id.eveningTemp);
        nightTemp = findViewById(R.id.nightTemp);
        currentDate = findViewById(R.id.dateValue);
        temperatureInfo = findViewById(R.id.temperatureValue);
        weatherDescription = findViewById(R.id.descriptionInformation);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        WindInfo = findViewById(R.id.windInformation);
        humidityInfo = findViewById(R.id.humidityValue);
        uvIndexInfo = findViewById(R.id.uvIndexValue);
        visibilityInfo = findViewById(R.id.visibilityValue);
        sunriseInfo = findViewById(R.id.sunriseInformation);
        feelsLikeTemp = findViewById(R.id.feelsValue);
        sunsetInfo = findViewById(R.id.sunsetInformation);
        imageView = findViewById(R.id.icon);
    }

    public class HourlyWeatherRecyclerView extends RecyclerView.Adapter<ViewHolder> {
        MainActivity mainActivity;
        ArrayList<Map<String, String>> daysList = new ArrayList<>();

        public HourlyWeatherRecyclerView(MainActivity mainActivity, ArrayList<Map<String, String>> daysList) {
            this.mainActivity = mainActivity;
            this.daysList = daysList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hourly_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String icon = daysList.get(position).get("icon");
            icon = icon.replace("-", "_");
            int iconResId = mainActivity.getResources().getIdentifier(icon,
                    "drawable", mainActivity.getPackageName());
            holder.DayInformation.setText(daysList.get(position).get("Day"));
            holder.timeInformation.setText(daysList.get(position).get("time"));
            holder.tempInformation.setText(daysList.get(position).get("temp"));
            holder.descInformation.setText(daysList.get(position).get("Description"));
            holder.weatherIcon.setImageResource(iconResId);
        }

        @Override
        public int getItemCount() {
            return daysList.size();
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView DayInformation, timeInformation,tempInformation, descInformation;
        ImageView weatherIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            DayInformation = itemView.findViewById(R.id.DayInformation);
            timeInformation = itemView.findViewById(R.id.timeInformation);
            tempInformation = itemView.findViewById(R.id.tempInformation);
            descInformation = itemView.findViewById(R.id.descInformation);
            weatherIcon = itemView.findViewById(R.id.icon);
        }
    }
}