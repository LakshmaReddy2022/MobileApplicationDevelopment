package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HelperActivity extends AppCompatActivity {

    Map<String, Object> daysList;
    WeeklyRecylcerViewAdapter weeklyRecylcerViewAdapter;
    RecyclerView weeklyRecyclerView;
    List<Map<String,String>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        Bundle bundle =getIntent().getBundleExtra("DayList");
        daysList = (Map<String, Object>) bundle.getSerializable("DayList");
        setTitle((CharSequence) daysList.get("address") + " 15 Day");


        ArrayList<Map<String, Object>> daysData = (ArrayList<Map<String, Object>>) daysList.get("daysList");
        for(int i = 0; i < daysData.size(); i++){
            String dayDate = (String) daysData.get(i).get("dateTimeEpoch");
            String formattedDayDate = convertDateFormat(dayDate);
            String minTemp  = (String) daysData.get(i).get("tempMin");
            String maxTemp  = (String) daysData.get(i).get("tempMax");
            String description = (String) daysData.get(i).get("description");
            String precipprob = (String) daysData.get(i).get("precipprob");
            String uvIndex = (String) daysData.get(i).get("uvIndex");
            String icon = (String) daysData.get(i).get("icon");
            ArrayList<Map<String,String>> hourlyArrayList = (ArrayList<Map<String, String>>) daysData.get(i).get("hourlyArrayList");
            String morning = hourlyArrayList.get(8).get("temp");
            String afternoon = hourlyArrayList.get(13).get("temp");
            String evening = hourlyArrayList.get(17).get("temp");
            String night = hourlyArrayList.get(hourlyArrayList.size()-1).get("temp");
            Map<String, String> map = new HashMap<>();
            map.put("dayDate",formattedDayDate);
            map.put("Temp",maxTemp+"/"+minTemp);
            map.put("description",description);
            map.put("precipprob","("+precipprob+"% precip.)");
            map.put("uvIndex","UV Index: "+uvIndex);
            map.put("icon",icon);
            map.put("morning",morning);
            map.put("afternoon",afternoon);
            map.put("evening",evening);
            map.put("night",night);
            list.add(map);
            if(list.size()==15)
                break;
        }
        weeklyRecyclerView = findViewById(R.id.weeklyRecycler);
        weeklyRecylcerViewAdapter = new WeeklyRecylcerViewAdapter(this, list);
        weeklyRecyclerView.setAdapter(weeklyRecylcerViewAdapter);
        weeklyRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    private String convertDateFormat(String dayDate) {
        long datetimeEpoch = Long.parseLong(dayDate);
        Date dateTime = new Date(datetimeEpoch * 1000);
        SimpleDateFormat dayDateformat = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault());
        return dayDateformat.format(dateTime);
    }

    public class WeeklyRecylcerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
        HelperActivity helperActivity;
        List<Map<String, String>> daysList;

        public WeeklyRecylcerViewAdapter(HelperActivity helperActivity, List<Map<String, String>> daysList) {
            this.helperActivity = helperActivity;
            this.daysList = daysList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.weekly_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.weekday.setText(daysList.get(position).get("dayDate"));
            holder.weekUVIndex.setText(daysList.get(position).get("uvIndex"));
            holder.weekminMax.setText(daysList.get(position).get("Temp"));
            holder.weekNightTemp.setText(daysList.get(position).get("night"));
            holder.Weekprecip.setText(daysList.get(position).get("precipprob"));
            String icon = daysList.get(position).get("icon");
            icon = icon.replace("-", "_");
            int iconResId = helperActivity.getResources().getIdentifier(icon,
                    "drawable", helperActivity.getPackageName());
            holder.weekMorningTemp.setText(daysList.get(position).get("morning"));
            holder.weekAfternoonTemp.setText(daysList.get(position).get("afternoon"));
            holder.weekDescription.setText(daysList.get(position).get("description"));
            holder.weekEveningTemp.setText(daysList.get(position).get("evening"));
            holder.imageView.setImageResource(iconResId);
        }

        @Override
        public int getItemCount() {
            return daysList.size();
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView weekday,weekminMax,weekDescription,Weekprecip, weekUVIndex, weekMorningTemp, weekAfternoonTemp,weekEveningTemp,weekNightTemp;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weekday = itemView.findViewById(R.id.weekday);
            weekDescription = itemView.findViewById(R.id.weekDescription);
            weekAfternoonTemp = itemView.findViewById(R.id.weeknoon);
            weekEveningTemp = itemView.findViewById(R.id.weekeven);
            Weekprecip = itemView.findViewById(R.id.Weekprecip);
            weekminMax = itemView.findViewById(R.id.weekminMax);
            weekUVIndex = itemView.findViewById(R.id.weekUVIndex);
            weekMorningTemp = itemView.findViewById(R.id.weekmorning);
            imageView = itemView.findViewById(R.id.imageView);
            weekNightTemp = itemView.findViewById(R.id.nightWeek);

        }
    }
}