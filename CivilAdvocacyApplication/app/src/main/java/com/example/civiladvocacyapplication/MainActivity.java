package com.example.civiladvocacyapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.civiladvocacyapplication.Activities.AboutActivity;
import com.example.civiladvocacyapplication.Activities.OfficialActivity;
import com.example.civiladvocacyapplication.AdapterPackage.Adapter;
import com.example.civiladvocacyapplication.DataPackage.DataDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView locations;
    RecyclerView recyclerView;
    FusedLocationProviderClient fusedLocationProviderClient;
    Context context = this;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locations = findViewById(R.id.locationDetails);
        recyclerView = findViewById(R.id.recycleView);
        if(!checkInternet()){
            setContentView(R.layout.activity_internet);
            setTitle("Know Your Government");
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.search:
                if (!checkInternet()){
                    setContentView(R.layout.activity_internet);
                    setTitle("Know Your Government");
                    return true;
                }
                final EditText txtUrl = new EditText(this);
                alertDialog(txtUrl);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    String loc = "Chicago,IL";
    double latitude, longitude;
    private final static int REQUEST_CODE = 100;

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, OfficialActivity.class);
        Bundle bundle =  new Bundle();
        int position = recyclerView.getChildAdapterPosition(v);
        Map<String, Object> ob = (Map<String, Object>) officialsList.get(position);
        bundle.putSerializable("List", (Serializable) ob);
        intent.putExtra("List",  bundle);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkInternet() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    List<Object> officialsList = new ArrayList<>();
    Adapter mainAdapter;

    public void updateDetails(List<Object> map) {
        if(map == null){
            Toast.makeText(MainActivity.this, "Please Enter valid City Name", Toast.LENGTH_SHORT).show();
            setTitle("Civil Advocacy");
            Map<String, Object> ob = (Map<String, Object>) officialsList.get(0);
            locations.setText((String) ob.get("location"));
            mainAdapter = new Adapter(this, officialsList);
            recyclerView.setAdapter(mainAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }else {
            officialsList = new ArrayList<>();
            Map<String, Object> ob = (Map<String, Object>) map.get(0);
            officialsList = map;
            setTitle("Civil Advocacy");
            locations.setText((String) ob.get("location"));
            mainAdapter = new Adapter(this, officialsList);
            recyclerView.setAdapter(mainAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    public void getData(String str) {
        DataDetails.fetchData(this, str);
    }

    private void getLocation() {
        final String[] locat = {loc};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            //TODO: UI updates.
                        }
                    }
                }
            };
            LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                try {
                                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    latitude=addresses.get(0).getLatitude();
                                    longitude=addresses.get(0).getLongitude();
                                    locat[0] = addresses.get(0).getAddressLine(0);
                                    getData(addresses.get(0).getAddressLine(0));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(MainActivity.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void alertDialog(EditText txtUrl) {
        new AlertDialog.Builder(this)
                .setTitle("Enter Address")
                .setView(txtUrl)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txtUrl.setTextColor(R.color.white);
                        if(!checkInternet()){
                            setContentView(R.layout.activity_internet);
                            return;
                        }else{
                            setContentView(R.layout.activity_main);
                            locations = findViewById(R.id.locationDetails);
                            recyclerView = findViewById(R.id.recycleView);
                        }
                        getData(txtUrl.getText().toString());
                    }
                })
                .setNegativeButton("CANCEL",null)
                .show();
    }
}