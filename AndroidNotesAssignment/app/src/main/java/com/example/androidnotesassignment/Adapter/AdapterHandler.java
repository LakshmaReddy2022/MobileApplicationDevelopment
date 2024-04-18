package com.example.androidnotesassignment.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidnotesassignment.Entity.NotesEntity;
import com.example.androidnotesassignment.MainActivity;
import com.example.androidnotesassignment.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterHandler extends RecyclerView.Adapter<AdapterViewHolder> {
    List<NotesEntity> notesEntityList;
    MainActivity mainActivity;
    public AdapterHandler(ArrayList<NotesEntity> notesEntityList, MainActivity mainActivity) {
        this.notesEntityList = notesEntityList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new AdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        NotesEntity note=notesEntityList.get(position);
        holder.title.setText(note.getTitle());
        String textTemp=note.getText();
        textTemp=textTemp.replaceAll("\n"," ");

        if(textTemp.length()>80){
            holder.text.setText(textTemp.substring(0,80)+"...");
        }else{
            holder.text.setText(textTemp);
        }

        holder.date.setText(note.getDate());

    }

    @Override
    public int getItemCount() {
        return notesEntityList.size();
    }
}
