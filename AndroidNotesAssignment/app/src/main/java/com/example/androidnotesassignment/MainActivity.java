package com.example.androidnotesassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.androidnotesassignment.Adapter.AdapterHandler;
import com.example.androidnotesassignment.Entity.NotesEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    List<NotesEntity> notesEntityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setId();
        handleLayout();
    }

    private void handleLayout() {
        recyclerView.setAdapter(adapterHandler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setId() {
        recyclerView=findViewById(R.id.recyclerView);
        adapterHandler = new AdapterHandler((ArrayList<NotesEntity>) notesEntityList, this);
    }

    RecyclerView recyclerView;
    AdapterHandler adapterHandler;


    @Override
    public void onClick(View v) {
        Intent intent=new Intent(this,EditActivity.class);
        Bundle bundle=new Bundle();
        int position=recyclerView.getChildAdapterPosition(v);
        NotesEntity note=notesEntityList.get(position);
        intent.putExtra("title",note.getTitle());
        intent.putExtra("text",note.getText());
        bundle.putSerializable("List",(Serializable) notesEntityList);
        intent.putExtra("List",bundle);
        intent.putExtra("position",position);
        startActivity(intent);

    }

    @Override
    public boolean onLongClick(View v) {
        int notePosition = recyclerView.getChildAdapterPosition(v);
        new AlertDialog.Builder(this)
                .setTitle("Delete Note "+notesEntityList.get(notePosition).getTitle()+"?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    notesEntityList.remove(notePosition);
                    handleLongClick();
                    adapterHandler.notifyDataSetChanged();
                    setTitle("Android Notes ("+notesEntityList.size()+")");
                    return;
                })
                .setNegativeButton("No",null).show();
        return true;
    }

    private void handleLongClick() {
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.print(notesEntityList);
            printWriter.close();
            fileOutputStream.close();
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    @Override
    protected void onResume() {
        notesEntityList.clear();
        loadData();
        setTitle("Android Notes ("+notesEntityList.size()+")");
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_meue, menu);
        return true;
    }

    private void loadData() {
        try{
            @SuppressLint("ResourceType")
            InputStream inputStream=getApplicationContext().openFileInput(getString(R.string.file_name));
            StringBuilder stringBuilder=new StringBuilder();
            String line;
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }

            JSONArray jsonArray=new JSONArray(stringBuilder.toString());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                NotesEntity note=new NotesEntity(jsonObject.getString("title"),jsonObject.getString("text"),jsonObject.getString("date"));
                notesEntityList.add(note);
            }

        }catch(IOException | JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, EditActivity.class);
        Intent info = new Intent(this, AboutActivity.class);
        Bundle bundle = new Bundle();
        if(item.getItemId()==R.id.add){
            bundle.putSerializable("List",(Serializable) notesEntityList);
            intent.putExtra("List",bundle);
            startActivity(intent);
            return true;
        }else if(item.getItemId()==R.id.info){
            startActivity(info);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}