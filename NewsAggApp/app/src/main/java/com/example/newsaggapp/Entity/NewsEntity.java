package com.example.newsaggapp.Entity;

import android.content.res.Resources;

import com.example.newsaggapp.MainActivity;
import com.example.newsaggapp.R;

import java.util.HashMap;
import java.util.Map;

public class NewsEntity {

    String id;
    String name;
    private Map<String, Integer> colors;
    String category;
    String title;
    String author;
    String desc;
    String time;
    String newsUrl;
    String urlImage;

    @Override
    public String toString() {
        return "NewsEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", colors=" + colors +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", desc='" + desc + '\'' +
                ", time='" + time + '\'' +
                ", newsUrl='" + newsUrl + '\'' +
                ", urlImage='" + urlImage + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getColors() {
        return colors;
    }

    public void setColor(MainActivity mainActivity) {
        colors = new HashMap<>();
        Resources r = mainActivity.getResources();

        colors.put("general", r.getColor(R.color.general));
        colors.put("business", r.getColor(R.color.business));
        colors.put("technology", r.getColor(R.color.technology));
        colors.put("sports", r.getColor(R.color.sports));
        colors.put("entertainment", r.getColor(R.color.entertainment));
        colors.put("health", r.getColor(R.color.health));
        colors.put("science", r.getColor(R.color.science));
    }

    public int getColor(String type){
        return colors.get(type);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public NewsEntity(){

    }

    public NewsEntity(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public NewsEntity(String title, String author, String desc, String time, String urlImage, String newsUrl) {
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.time = time;
        this.newsUrl = newsUrl;
        this.urlImage = urlImage;
    }


}
