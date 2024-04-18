package com.example.civiladvocacyapplication.DataPackage;

import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.civiladvocacyapplication.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDetails {
    private static MainActivity mainActivity;
    private static RequestQueue queue;
    private static List<Object> finalList = new ArrayList<>();
    private static String key = "AIzaSyD9LbHpKIF6jtJk81UYCMtLdCeSK2m05Vs";
    private static String url = "https://www.googleapis.com/civicinfo/v2/representatives";

    public static void fetchData(MainActivity activity, String location) {
        mainActivity = activity;
        queue = Volley.newRequestQueue(mainActivity);
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("key", key);
        builder.appendQueryParameter("address",location);
        String url = builder.build().toString();
        Response.Listener<JSONObject> jsonObjectListener = response -> parseJSON(response.toString());
        Response.ErrorListener errorListener = error -> {
            mainActivity.updateDetails(null);
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener);
        queue.add(jsonObjectRequest);
    }

    private static void parseJSON(String toString) {
        try {
            List<Map<String,Object>> list = new ArrayList<>();
            finalList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(toString);

            JSONObject normalizedObj = jsonObject.getJSONObject("normalizedInput");
            JSONArray jsonArray = jsonObject.getJSONArray("offices");
            JSONArray officialJsonArr = jsonObject.getJSONArray("officials");

            String location = normalizedObj.getString("line1") + " " + normalizedObj.getString("city") + ", " + normalizedObj.getString("state")
                    + " " + normalizedObj.getString("zip");
            for (int i = 0; i < officialJsonArr.length(); i++) {
                Map<String, Object> map = new HashMap<>();
                String photoUrl = "";
                String url = "";
                String phone = "";
                String email = "";
                String address = "";
                JSONObject officialObj = (JSONObject) officialJsonArr.get(i);
                if(officialObj.has("photoUrl")){
                    photoUrl = officialObj.getString("photoUrl");
                }
                if(officialObj.has("emails")){
                    email = officialObj.getJSONArray("emails").get(0).toString();
                }
                if(officialObj.has("urls")){
                    url = officialObj.getJSONArray("urls").get(0).toString();
                }
                if(officialObj.has("phones")){
                    phone = officialObj.getJSONArray("phones").get(0).toString();
                }

                map.put("email",email);
                map.put("url",url);
                map.put("location",location);
                map.put("name",officialObj.getString("name"));
                map.put("photoUrl",photoUrl);
                map.put("phone",phone);
                map.put("party",officialObj.getString("party"));

                if(officialObj.has("address")){
                    JSONObject o = (JSONObject) officialObj.getJSONArray("address").get(0);
                    address = o.getString("line1")+" "+o.getString("city")+" "+o.getString("state")+" "+o.getString("zip");
                }
                map.put("address",address);
                List<String[]> list1 = new ArrayList<>();
                if(officialObj.has("channels")){
                    JSONArray array = officialObj.getJSONArray("channels");
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = (JSONObject) array.get(j);
                        String[] channel = {object.getString("type"), object.getString("id")};
                        list1.add(channel);
                    }
                }
                map.put("channels",list1);
                list.add(map);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                JSONArray array = object.getJSONArray("officialIndices");
                for (int j = 0; j < array.length(); j++) {
                    int pos = Integer.parseInt(array.get(j).toString());
                    Map<String, Object> ob = (Map<String, Object>) list.get(pos);
                    ob.put("postName",object.getString("name"));
                    finalList.add(ob);
                }
            }
            mainActivity.updateDetails(finalList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
