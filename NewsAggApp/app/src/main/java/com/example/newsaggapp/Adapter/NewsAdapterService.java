package com.example.newsaggapp.Adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsaggapp.Entity.NewsEntity;
import com.example.newsaggapp.MainActivity;
import com.example.newsaggapp.R;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapterService extends RecyclerView.Adapter<NewsAdapterService.ViewHolder> {
    MainActivity mainActivity;
    ArrayList<NewsEntity> list;

    @NonNull
    @Override
    public NewsAdapterService.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapterService.ViewHolder holder, int position) {

        NewsEntity news = list.get(position);
        Date d = null;
        try {
            d = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(news.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String s = (new SimpleDateFormat("MMM dd, yyyy hh:mm")).format(d);

        holder.imageView.setOnClickListener(v -> this.mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news.getNewsUrl()))));
        holder.desc.setOnClickListener(v -> this.mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news.getNewsUrl()))));
        holder.title.setOnClickListener(v -> this.mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news.getNewsUrl()))));

        if(news.getTitle().equals(null)){
            holder.title.setText("");
        }else
            holder.title.setText(news.getTitle());
        if(news.getDesc().equals(null))
            holder.desc.setText("");
        else
            holder.desc.setText(news.getDesc());
        if(news.getAuthor().equals(null))
            holder.author.setText("");
        else
            holder.author.setText(news.getAuthor());

        holder.time.setText(s);
        holder.pageCount.setText((position + 1) + " of " + list.size());

        if (!news.getUrlImage().equals("null")) {
            holder.imageView.setImageResource(R.drawable.loading);
            new Image(holder.imageView, mainActivity).execute(news.getUrlImage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<NewsEntity> list){
        this.list = list;
    }

    public NewsAdapterService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private class Image extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        MainActivity mainActivity;

        public Image(ImageView imageView, MainActivity mainActivity) {
            this.imageView = imageView;
            this.mainActivity = mainActivity;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap mIcon11;
            try {
                InputStream in = new java.net.URL(strings[0]).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                mIcon11 = null;
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                imageView.setImageResource(mainActivity.getResources().getIdentifier("brokenimage", "drawable", mainActivity.getPackageName()));
            } else {
                imageView.setImageBitmap(result);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView author;
        TextView time;
        TextView title;
        TextView desc;
        TextView pageCount;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.Author);
            time = itemView.findViewById(R.id.DateInfo);
            title = itemView.findViewById(R.id.Title);
            desc = itemView.findViewById(R.id.Description);
            pageCount = itemView.findViewById(R.id.Count);
            imageView = itemView.findViewById(R.id.Image);

        }
    }
}
