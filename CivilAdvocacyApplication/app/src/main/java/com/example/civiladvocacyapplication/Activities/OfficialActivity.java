package com.example.civiladvocacyapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.civiladvocacyapplication.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OfficialActivity extends AppCompatActivity {
    String facebookId;
    String twitterId;
    String youtubeId;
    String partyLogoName;
    Map<String, Object> object;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        emailLab = findViewById(R.id.labelEmail);
        location = findViewById(R.id.OfficialLocation);
        officialPost = findViewById(R.id.OfficialPosition);
        partyLogo = findViewById(R.id.partyImg);
        officialName = findViewById(R.id.OfficialName);
        officialPartyName = findViewById(R.id.PartyName);
        email = findViewById(R.id.email);
        websiteLab = findViewById(R.id.labelWebsite);
        website = findViewById(R.id.website);
        facebook = findViewById(R.id.facebookImgClick);
        photo = findViewById(R.id.imageView);
        youtube = findViewById(R.id.youtubeImgClick);
        constraintLayout = findViewById(R.id.constraintLayout);
        addressLab = findViewById(R.id.labelAddress);
        address = findViewById(R.id.address);
        phoneNoLab = findViewById(R.id.labelPhone);
        phoneNo = findViewById(R.id.phoneNo);
        twitter = findViewById(R.id.twitterImgClick);
        Bundle bundle =getIntent().getBundleExtra("List");
        object = (Map<String, Object>) bundle.getSerializable("List");
        setValues();
    }


    @SuppressLint("ResourceType")
    public void photoClick(View view) {

        if(!object.get("photoUrl").equals("")) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("List", (Serializable) object);
            startActivity(new Intent(this, PhotoDetailActivity.class).putExtra("List", bundle));
        }
    }

    public void AddressClick(View view) {
        String address = "geo:0,0?q="+object.get("address");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(address)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(address)));
        }
    }

    public void facebookClick(View view) {
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookId;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + facebookId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(urlToUse)));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setValues() {
        location.setText((CharSequence) object.get("location"));
        officialPost.setText((CharSequence) object.get("postName"));
        officialName.setText((CharSequence) object.get("name"));

        addPartyLogo((String) object.get("party"));
        String emails = (String) object.get("email");

        if(!emails.equals("")){
            email.setText(emails);
        }else{
            emailLab.setVisibility(TextView.GONE);
            email.setVisibility(TextView.GONE);
        }

        handlePhoto((String) object.get("photoUrl"));

        if(!checkInternet()){
            photo.setImageResource(R.drawable.brokenimage);
        }

        addPhoneAndAddress((String) object.get("address"), (String) object.get("phone"), (String) object.get("url"));

        handleLogo((List<String[]>) object.get("channels"));

    }

    private void addPartyLogo(String party) {
        if(!party.equals("")) {
            officialPartyName.setText("(" + party + ")");
            if(party.contains("Democratic")){
                partyLogoName = "https://democrats.org/";
                partyLogo.setImageResource(R.drawable.dem_logo);
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.blue));
            }else if(party.contains("Republican")){
                partyLogoName = "https://www.gop.com/";
                partyLogo.setImageResource(R.drawable.rep_logo);
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.red));
            }else{
                partyLogo.setVisibility(ImageView.GONE);
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.black));
            }
        }
    }

    private void addPhoneAndAddress(String address1, String phone, String url) {
        if(!phone.equals("")){
            phoneNo.setText(phone);
        }else{
            phoneNoLab.setVisibility(TextView.GONE);
            phoneNo.setVisibility(TextView.GONE);
        }

        if(!url.equals("")){
            website.setText(url);
        }else{
            websiteLab.setVisibility(TextView.GONE);
            website.setVisibility(TextView.GONE);
        }
        if(!address1.equals("")){
            SpannableString spannableString = new SpannableString(address1);
            spannableString.setSpan(new UnderlineSpan(), 0,  spannableString.length(), 0);
            address.setText(spannableString);
        }else{
            addressLab.setVisibility(TextView.GONE);
            address.setVisibility(TextView.GONE);
        }
    }

    private void handlePhoto(String photoUrl) {
        if(!photoUrl.equals("")){
            String temp = photoUrl.replace("http:","https:");
            Picasso.get().load(temp).error(R.drawable.brokenimage).into(photo);
        }
    }

    private void handleLogo(List<String[]> list) {
        for(String[] str : list){
            if(str[0].equals("Facebook")){
                facebook.setImageResource(R.drawable.facebook);
                facebookId = str[1];
            }else if(str[0].equals("Twitter")){
                twitter.setImageResource(R.drawable.twitter);
                twitterId = str[1];
            }else if(str[0].equals("YouTube")){
                youtube.setImageResource(R.drawable.youtube);
                youtubeId = str[1];
            }
        }
    }

    public void youTubeClick(View view) {
        String name = youtubeId;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    TextView location, officialPost, officialName, officialPartyName;
    ImageView photo, partyLogo;

    public void twitterClick(View view) {
        Intent intent = null;
        String name = twitterId;
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    TextView addressLab, address, phoneNoLab, phoneNo, emailLab, email, websiteLab,  website;
    ImageView facebook, twitter, youtube;

    ConstraintLayout constraintLayout ;

    public void logoClick(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(partyLogoName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(partyLogoName)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkInternet() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}