package com.example.newyorktimes.Data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ArticleItem.class}, version = 1)
public  abstract class ArticleDatabase extends RoomDatabase {

    public abstract ArticleItemDAO cmDAO();

}
