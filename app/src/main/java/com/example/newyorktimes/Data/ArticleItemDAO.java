package com.example.newyorktimes.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticleItemDAO {


    @Insert
    void insertMessage(ArticleItem A);


    @Query("Select * from ArticleItem")
    List<ArticleItem> getAllArticleItem();

    @Delete
    void deleteArticleItem(ArticleItem A);



}
