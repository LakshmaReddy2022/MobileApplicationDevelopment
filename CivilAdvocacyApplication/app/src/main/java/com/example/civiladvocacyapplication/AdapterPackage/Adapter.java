package com.example.civiladvocacyapplication.AdapterPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.civiladvocacyapplication.MainActivity;
import com.example.civiladvocacyapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    MainActivity mainActivity;
    List<Object> map;

    public Adapter(MainActivity mainActivity, List<Object> map) {
        this.mainActivity = mainActivity;
        this.map = map;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_view, parent, false);
        itemView.setOnClickListener(mainActivity);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {

        Map<String, Object> ob = (Map<String, Object>) map.get(position);
        String pic = (String) ob.get("photoUrl");
        if(!pic.isEmpty()){
            String temp = pic.replace("http:","https:");
            Picasso.get().load(temp).error(R.drawable.brokenimage).into(holder.imageView);
        }else{
            holder.imageView.setImageResource(R.drawable.missing);
        }
        holder.post.setText((CharSequence) ob.get("postName"));
        holder.name.setText(ob.get("name")+" ("+ob.get("party")+")");
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, post;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            post = itemView.findViewById(R.id.post);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
