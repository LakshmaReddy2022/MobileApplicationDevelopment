package com.example.androidnotesassignment.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidnotesassignment.R;

public class AdapterViewHolder extends RecyclerView.ViewHolder {
    TextView title, text, date;
    public AdapterViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.note_title);
        text = itemView.findViewById(R.id.note_text);
        date = itemView.findViewById(R.id.note_date);
    }
}
