package com.example.newyorktimes.Data;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ArticleItem {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate=true)
    public int id;




    @ColumnInfo(name="headline")
    protected String headline;


    @ColumnInfo(name="article_url")
    protected String article_url;


    @ColumnInfo(name="publication date")
    protected String publication_date;

    public ArticleItem(String headline, String article_url, String publication_date) {
        this.headline = headline;
        this.article_url = article_url;
        this.publication_date = publication_date;
    }

    public int getId() {
        return id;
    }

    public String getHeadline() {
        return headline;
    }

    public String getArticle_url() {
        return article_url;
    }

    public String getPublication_date() {
        return publication_date;
    }
}
