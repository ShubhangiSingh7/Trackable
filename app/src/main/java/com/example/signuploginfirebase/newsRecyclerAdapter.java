package com.example.signuploginfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kwabenaberko.newsapilib.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class newsRecyclerAdapter extends RecyclerView.Adapter<newsRecyclerAdapter.Newsviewholder> {

    List<Article> articles;
    newsRecyclerAdapter (List<Article> articles){
        this.articles = articles;
    }

    @NonNull
    @Override
    public Newsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_card, parent, false);
        return new Newsviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Newsviewholder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.getTitle());
        holder.source.setText(article.getSource().getName());
        Picasso.get().load(article.getUrlToImage())
                .error(R.drawable.no_image)
                .into(holder.imageView);
    }

    void updatedata(List<Article> data){
        articles.clear();
        articles.addAll(data);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class Newsviewholder extends RecyclerView.ViewHolder {

        TextView title, source;
        ImageView imageView;
        public Newsviewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            source = itemView.findViewById(R.id.article_source);
            imageView = itemView.findViewById(R.id.article_image_view);
        }
    }
}
