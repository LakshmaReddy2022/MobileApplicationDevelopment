package com.example.androidnotesassignment.Entity;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class NotesEntity implements Serializable {
    private String title;
    private String text;
    private String date;

    public NotesEntity(String title, String description, String date) {
        this.title = title;
        this.text = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String description) {
        this.text = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        try{
            StringWriter sw=new StringWriter();
            JsonWriter jsonWriter=new JsonWriter(sw);
            jsonWriter.setIndent(" ");
            jsonWriter.beginObject();
            jsonWriter.name("title").value(getTitle());
            jsonWriter.name("text").value(getText());
            jsonWriter.name("date").value(getDate());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }
}
