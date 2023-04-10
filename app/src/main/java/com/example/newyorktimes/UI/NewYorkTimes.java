package com.example.newyorktimes.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.newyorktimes.Data.ArticleDatabase;
import com.example.newyorktimes.Data.ArticleItem;
import com.example.newyorktimes.Data.ArticleItemDAO;
import com.example.newyorktimes.Data.ArticleViewModel;
import com.example.newyorktimes.R;
import com.example.newyorktimes.databinding.ActivityMainBinding;
import com.example.newyorktimes.databinding.ActivityNewYorkTimesBinding;
import com.example.newyorktimes.databinding.ArticleRowBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NewYorkTimes extends AppCompatActivity {
    private ActivityNewYorkTimesBinding binding;


    protected RequestQueue queue = null;
    private static String apiKey = "QE5Ga5xvhjCZqtqnTMlPtp5TbTzpI6WL";
    SharedPreferences prefs;
    private RecyclerView.Adapter myAdapter;

    private ArrayList<ArticleItem> articleItems;
    private ArticleViewModel articleViewModel;
    private Executor thread;

    private ArticleItemDAO mDAO;
    String headLine;
    String article_url;
    String publication_date;
    String search;

    String url = null;

    ArrayList<ArticleItem> list;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_york_times, menu);


        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);


        switch (item.getItemId()) {
            case R.id.Item_1:

                 Intent intent = new Intent(this, FavouriteArticles.class);

                  startActivity(intent);


                break;


            case R.id.Item_2:


                AlertDialog.Builder builder = new AlertDialog.Builder(NewYorkTimes.this);
                builder.setMessage("").
                        setTitle("How to use the New York Times App").
                        setNegativeButton("ok", (dialog, cl) -> {
                        }).create().show();


                break;

        }
        return true;


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewYorkTimesBinding.inflate(getLayoutInflater());

        setTitle("search for articles");
        setSupportActionBar(binding.toolbar);


        setContentView(binding.getRoot());

        list = new ArrayList<ArticleItem>();

        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        articleItems = articleViewModel.articleItems.getValue();
        if (articleItems == null) {


            articleViewModel.articleItems.setValue(articleItems = new ArrayList<ArticleItem>());


            thread = Executors.newSingleThreadExecutor();

            runOnUiThread(() -> {

                binding.searchRecycleView.setAdapter(myAdapter);

                setContentView(binding.getRoot());




            });

        }

        queue = Volley.newRequestQueue(this);


        binding = ActivityNewYorkTimesBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);


        binding.articleEditText.setText(prefs.getString("search", ""));


        thread = Executors.newSingleThreadExecutor();

        thread.execute(() ->
        {

            ArticleDatabase db = Room.databaseBuilder(getApplicationContext(), ArticleDatabase.class, "database-name").build();
            mDAO = db.cmDAO();


        });


        runOnUiThread(() -> {
            setContentView(binding.getRoot());
        });


        binding.weatherSearchButton.setOnClickListener(clk -> {


            search = binding.articleEditText.getText().toString();


            try {
                url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?q=" + URLEncoder.encode(search, "UTF-8") + "&api-key=" + apiKey;


                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {

                            try {

                                prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("search", search);
                                editor.apply();


                                JSONObject responseObj = response.getJSONObject("response");

                                //   Log.i("jason", "onCreate: " + response);

                                JSONArray docArray = responseObj.getJSONArray("docs");

                                runOnUiThread(() -> {

                                    for (int i = 0; i < articleItems.size()-1; i++) {

                                        articleItems.remove(i);
                                        myAdapter.notifyItemRemoved(i);

                                    }

                                });




                                for (int i = 0; i < docArray.length(); i++) {

                                    //  Log.i("13213", "onCreate: "+docArray.getJSONObject(i));


                                    articleItems.add(new ArticleItem(docArray.getJSONObject(i).getJSONObject("headline").getString("main"),
                                            docArray.getJSONObject(i).getString("web_url"), docArray.getJSONObject(i).getString("pub_date")));


                                        myAdapter.notifyItemInserted(articleItems.size()-1);


                                    Log.i("13213", "onCreate: " + docArray.getJSONObject(i).getString("abstract"));


                                }








                            } catch (JSONException e) {


                            }

                        }, error -> {


                });


                queue.add(request);


            } catch (UnsupportedEncodingException e) {
                //  Log.i("tag", "onCreate: "+e);


                throw new RuntimeException(e);
            }


        });


        binding.searchRecycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                ArticleRowBinding articleRowBinding = ArticleRowBinding.inflate(getLayoutInflater(), parent, false);
                View root = articleRowBinding.getRoot();
                return new MyRowHolder(root);


            }


            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {

                holder.row_headline.setText(articleItems.get(position).getHeadline());
                holder.row_publication_date.setText(articleItems.get(position).getPublication_date());
                holder.row_url.setText(articleItems.get(position).getArticle_url());

                holder.row_url.setOnClickListener(c->{
                    Uri uri=Uri.parse(holder.row_url.getText().toString());
                    startActivity(new Intent(Intent.ACTION_VIEW,uri));

                });



                holder.row_imgSave.setOnClickListener(c->{



                    thread.execute(() ->
                    {

                        mDAO.insertMessage(new ArticleItem(holder.row_headline.getText().toString(),holder.row_url.getText().toString(),holder.row_publication_date.getText().toString()));
                        Snackbar.make(holder.itemView,"saved",Snackbar.LENGTH_LONG).show();

                    });



                });








            }

            @Override
            public int getItemCount() {
                return articleItems.size();
            }

            //function to check what kind of ChatMessage object is at row position
            // If the isSend is true, then return 0
            // so that the onCreateViewHolder checks the viewType and inflates a send_message layout.
            // If isSend is false, then getItemViewType returns 1 and onCreateViewHolder checks
            // if the viewType is 1 and inflates a receive_message layout.


            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });

        binding.searchRecycleView.setLayoutManager(new LinearLayoutManager(this));



    }


    class MyRowHolder extends RecyclerView.ViewHolder {

        public TextView row_headline;
        public TextView row_publication_date;
        public TextView row_url;

        public ImageView row_imgSave;

        public MyRowHolder(@NonNull View itemView) {

            super(itemView);




            itemView.setOnClickListener(clk -> {

                int position = getAbsoluteAdapterPosition();
                //  WeatherItem selected = weatherItems.get(position);

                //  weatherModel.selectedWeatherItem.postValue(selected);
            });

            row_headline = itemView.findViewById(R.id.row_head_line);
            row_publication_date=itemView.findViewById(R.id.publication_date);
            row_url=itemView.findViewById(R.id.url);
            row_imgSave=itemView.findViewById(R.id.row_saveButton);


        }


    }


}