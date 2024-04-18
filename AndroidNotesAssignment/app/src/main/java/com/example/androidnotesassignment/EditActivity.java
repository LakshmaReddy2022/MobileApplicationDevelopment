package com.example.androidnotesassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.androidnotesassignment.Entity.NotesEntity;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    int i =-1;
String noteTitle, noteText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setID();
        Bundle bundle = getIntent().getBundleExtra("List");

        notesEntityArrayList = (ArrayList<NotesEntity>) bundle.getSerializable("List");
        Bundle bundle2 = getIntent().getExtras();

        if( bundle2.getString("title") != null) {
            noteTitle = bundle2.getString("title");
            noteText = bundle2.getString("text");
            i = bundle2.getInt("position");
            title.setText(bundle2.getString("title"));
            text.setText(bundle2.getString("text"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if((item.getItemId()) == R.id.save ){
            Intent intent=new Intent(this,MainActivity.class);
            if(!addNoteFile()){
                alertDialogEmpty();
                return true;
            }
            startActivity(intent);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void setID() {
        title = findViewById(R.id.enter_title);
        text = findViewById(R.id.enter_text);
        i = -1;
    }

    EditText text, title;
    ArrayList<NotesEntity> notesEntityArrayList;

    private boolean addNoteFile(){
        if(!title.getText().toString().isEmpty()){
            @SuppressLint("SimpleDateFormat")
            String noteDate = new SimpleDateFormat("E MMM dd, hh:mm aa ").format(new Date());
            NotesEntity noteObject = new NotesEntity(title.getText().toString(),text.getText().toString(),noteDate);
            if(i>=0){
                if (!noteTitle.equals(title.getText().toString()) || !noteText.equals(text.getText().toString())) {
                    notesEntityArrayList.remove(i);
                }else {
                    return true;
                }
            }
            notesEntityArrayList.add(noteObject);
            addInJSON();
            return true;
        }
        return false;
    }

    private void addInJSON() {
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.print(notesEntityArrayList);
            printWriter.close();
            fileOutputStream.close();
        }catch (Exception e){
            e.getStackTrace();
        }
    }

    public void alertDialogEmpty(){
        Intent intent = new Intent(this, MainActivity.class);
        new AlertDialog.Builder(this)
                .setTitle("Please Write Title!")
                .setMessage("The note will not be saved without a title.\n"+"Do you want to continue?\n")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        String title1 = title.getText().toString();
        if(i>=0 && noteTitle.equals(title.getText().toString()) && noteText.equals(text.getText().toString())){
            addNoteFile();
            startActivity(intent);
        }else if(title1.isEmpty() && text.getText().toString().isEmpty()){
            startActivity(intent);
        }else if(title1.isEmpty()){
            alertDialogEmpty();
        }else{
            handleBack(intent);
        }
    }

    private void handleBack(Intent intent) {
        new AlertDialog.Builder(this)
                .setTitle("Your note is not saved!\n"+"Save Note '"+ title.getText().toString() +"'?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            addNoteFile();
                            startActivity(intent);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(intent);
                    }
                }).show();
    }

}