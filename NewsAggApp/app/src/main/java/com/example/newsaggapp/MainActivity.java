package com.example.newsaggapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsaggapp.Adapter.NewsAdapterService;
import com.example.newsaggapp.Entity.NewsEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    private ListView drawerList;

    private ActionBarDrawerToggle drawerToggle;

    private String[] sources;

    private DrawerLayout drawerLayout;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignStart();

        drawerList.setOnItemClickListener((parent, view, position, id) -> {
            this.setTitle(newsArticle.get(position).getName());
            getArticleData(newsArticle.get(position).getId());
            findViewById(R.id.content_frame).setBackgroundColor(Color.parseColor("#ffffff"));
            drawerLayout.closeDrawer(drawerList);
        });
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private NewsEntity[] newsSources;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private static ArrayList<NewsEntity> newsArticle = new ArrayList<>();
    Map<Integer, String> filterMap = new HashMap<>();
    private ArrayAdapter<String> arrayAdapter;
    ConstraintLayout content_frame;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateData(String item, int id) {
        if (id == 0) return;

        String prev = filterMap.get(id);
        filterMap.put(id, item);

        NewsEntity[] filteredSources = Arrays.stream(newsSources)
                .filter(source -> (source.getCategory().equals(filterMap.get(1)) || filterMap.get(1).equals("all")))
                .toArray(NewsEntity[]::new);
        newsArticle = new ArrayList<>();
        Collections.addAll(newsArticle, filteredSources);
        String[] nSources = Arrays.stream(filteredSources).map(NewsEntity::getName).toArray(String[]::new);

        if (nSources.length == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("No News Sources")
                    .setMessage("no news sources exist that match the specified")
                    .setPositiveButton("OK", (dialog, which) -> filterMap.put(id, prev)).show();
        } else {
            sources = nSources;
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer, sources) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ((TextView)view.findViewById(android.R.id.text1)).setTextColor(newsEntity.getColor(filteredSources[position].getCategory()));
                    return view;
                }
            };

            drawerList.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            setTitle("News Gateway (" + sources.length + ")");
            recyclerView.setAdapter(null);
            content_frame.setBackgroundResource(R.drawable.newsback);
        }
    }

    private Menu topics;
    public NewsEntity newsEntity;

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMenu(NewsEntity[] newsSources) {
        Arrays.stream(newsSources).map(NewsEntity::getCategory).distinct().forEach((topic) -> {
            SpannableString str = new SpannableString(topic);
            str.setSpan(new ForegroundColorSpan(newsEntity.getColor(topic)), 0, str.length(), 0);
            topics.add(1, 0, 0, str);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        topics = menu;
        topics.add(1, 0, 0, "all");
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: DrawerToggle " + item);
            return true;
        }
        updateData(item.getTitle().toString(), item.getGroupId());
        return super.onOptionsItemSelected(item);
    }

    private NewsAdapterService newsAdapterService;
    RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void assignStart() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        recyclerView = findViewById(R.id.dataList);
        content_frame = findViewById(R.id.content_frame);
        filterMap.put(1, "all");
        getSourceData();
        newsEntity = new NewsEntity();
        newsEntity.setColor(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setNewsSource(List<NewsEntity> newsSources) {
        newsArticle = new ArrayList<>(newsSources);
        sources = new String[newsSources.size()];
        for (int i = 0; i < sources.length; i++)
            sources[i] = newsSources.get(i).getName();
        this.setTitle("News Gateway " + "(" +sources.length+")");
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer, sources) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView)view.findViewById(android.R.id.text1)).setTextColor(newsEntity.getColor(newsSources.get(position).getCategory()));
                return view;
            }
        };
        drawerList.setAdapter(arrayAdapter);
        NewsEntity[] newsData = new NewsEntity[newsSources.size()];
        for(int i = 0; i < newsSources.size(); i++){
            newsData[i] = newsSources.get(i);
        }
        this.newsSources = newsData;
        updateMenu(newsData);
    }

    private static RequestQueue queue, queuee;
    private String key ="85eb9171af5e4e278c109056eb3ad68b";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getSourceData() {
        queue = Volley.newRequestQueue(this);
        Uri.Builder builder = Uri.parse("https://newsapi.org/v2/top-headlines/sources").buildUpon();
        builder.appendQueryParameter("apiKey", key);
        String url = builder.build().toString();
        Response.Listener<JSONObject> jsonObjectListener = response -> {
            try {
                JSONArray jSources = new JSONObject(response.toString()).getJSONArray("sources");
                for(int i = 0; i < jSources.length(); i++) {
                    JSONObject News = (JSONObject) jSources.get(i);
                    news.add(new NewsEntity(News.getString("id"),News.getString("name"),News.getString("category")));
                }
                setNewsSource(news);
            }catch (Exception e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            setNewsSource(null);
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }
    private static List<NewsEntity> news = new ArrayList<>();
    ArrayList<NewsEntity> articles;

    public void getArticleData(String source){
        queuee = Volley.newRequestQueue(this);
        Uri.Builder builder = Uri.parse("https://newsapi.org/v2/top-headlines").buildUpon();
        builder.appendQueryParameter("sources", source);
        builder.appendQueryParameter("apiKey", key);
        String url = builder.build().toString();
        Response.Listener<JSONObject> jsonObjectListener = response -> {
            articles = new ArrayList<>();
            try {
                JSONArray newsSources = new JSONObject(response.toString()).getJSONArray("articles");
                for(int i = 0; i < newsSources.length(); i++) {
                    JSONObject article = (JSONObject) newsSources.get(i);
                    articles.add(new NewsEntity(article.getString("title"),article.getString("author"),article.getString("description"),
                            article.getString("publishedAt"),article.getString("urlToImage"),article.getString("url")));

                }
                newsAdapterService = new NewsAdapterService(this);
                newsAdapterService.setList(articles);
                recyclerView.setAdapter(newsAdapterService);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }catch (Exception e){
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {
            recyclerView.setAdapter(newsAdapterService);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            return;
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,jsonObjectListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        queuee.add(jsonObjectRequest);
    }



}