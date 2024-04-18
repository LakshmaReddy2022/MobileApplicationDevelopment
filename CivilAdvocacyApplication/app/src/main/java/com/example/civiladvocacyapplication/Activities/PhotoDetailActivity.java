package com.example.civiladvocacyapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.civiladvocacyapplication.R;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class PhotoDetailActivity extends AppCompatActivity {

    Map<String, Object> object;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        setID();
        Bundle bundle =getIntent().getBundleExtra("List");
        object = (Map<String, Object>) bundle.getSerializable("List");
        setValues();

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkInternet() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    TextView person, location, post;
    ImageView personalPhoto, partyLogo;
    ConstraintLayout constraintLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setValues() {
        String party = (String) object.get("party");
        if(party.contains("Democratic")){
            partyLogo.setImageResource(R.drawable.dem_logo);
            constraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.blue));
        }else if(party.contains("Republican")){
            partyLogo.setImageResource(R.drawable.rep_logo);
            constraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.red));
        }else{
            partyLogo.setVisibility(ImageView.GONE);
            constraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.black));
        }
        location.setText((CharSequence) object.get("location"));
        post.setText((CharSequence) object.get("postName"));
        person.setText((CharSequence) object.get("name"));
        String temp2 =  ((String) object.get("photoUrl")).replace("http:","https:");
        Picasso.get().load(temp2).error(R.drawable.brokenimage).into(personalPhoto);
        if(!checkInternet()){
            personalPhoto.setImageResource(R.drawable.brokenimage);
        }
    }

    public void setID() {
        constraintLayout = findViewById(R.id.constraintLayout);
        personalPhoto = findViewById(R.id.image);
        partyLogo = findViewById(R.id.photoLogoInfo);
        location = findViewById(R.id.photoAddressInfo);
        post  = findViewById(R.id.photoPostInfo);
        person =  findViewById(R.id.photoNameInfo);
    }
}