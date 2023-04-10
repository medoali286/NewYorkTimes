package com.example.newyorktimes.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newyorktimes.Data.ArticleDatabase;
import com.example.newyorktimes.Data.ArticleItem;
import com.example.newyorktimes.Data.ArticleItemDAO;
import com.example.newyorktimes.Data.ArticleViewModel;
import com.example.newyorktimes.R;
import com.example.newyorktimes.databinding.ActivityFavouriteArticlesBinding;
import com.example.newyorktimes.databinding.ArticleRowBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class FavouriteArticles extends AppCompatActivity {


    private RecyclerView.Adapter myAdapter;
    private ArrayList<ArticleItem> articleItems;

    private ArticleItemDAO mDAO;
    private Executor thread;
    int position;
    private ArticleViewModel articleModel;
boolean IsSelected;
    View itemView1;
    TextView tv_headline;


ActivityFavouriteArticlesBinding binding;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.favourite_articles, menu);


        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);


        switch (item.getItemId()) {
            case R.id.Item_1:

                Intent intent = new Intent(this, NewYorkTimes.class);

                startActivity(intent);


                break;


            case R.id.Item_2:


                AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteArticles.this);
                builder.setMessage("").
                        setTitle("How to use the New York Times App").
                        setNegativeButton("ok", (dialog, cl) -> {
                        }).create().show();


                break;

            case R.id.Item_3:

                if (articleItems.size() != 0 && IsSelected) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(FavouriteArticles.this);
                    builder1.setMessage("Do you want to Delete this article : " + tv_headline.getText().toString()).
                            setTitle("Question").
                            setNegativeButton("no", (dialog, cl) -> {
                            })
                            .setPositiveButton("yes", (dialog, cl) -> {

                                ArticleItem removedMessage = articleItems.get(position);
                                thread.execute(() ->
                                {

                                    mDAO.deleteArticleItem(removedMessage);

                                });
                                runOnUiThread(() -> {
                                    articleItems.remove(position);
                                    myAdapter.notifyItemRemoved(position);
                                });


                                Snackbar.make(binding.getRoot(), "You deleted article  " + tv_headline.getText(), Snackbar.LENGTH_SHORT)
                                        .setAction("Undo", c -> {
                                            articleItems.add(position, removedMessage);
                                            myAdapter.notifyItemInserted(position);
                                        }).show();


                                onBackPressed();


                                IsSelected = false;


                            }).create().show();


                }


                break;

        }
        return true;


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityFavouriteArticlesBinding.inflate(getLayoutInflater());

        setTitle("Favourite Articles");
        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());

        articleModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        articleItems= articleModel.articleItems.getValue();

        if (articleItems == null) {


            articleModel.articleItems.setValue(articleItems = new ArrayList<ArticleItem>());


            thread = Executors.newSingleThreadExecutor();

            thread.execute(() ->
            {


                ArticleDatabase db = Room.databaseBuilder(getApplicationContext(), ArticleDatabase.class, "database-name").build();
                mDAO = db.cmDAO();


                articleItems.addAll(mDAO.getAllArticleItem()); //Once you get the data from database


                runOnUiThread(() -> {

                    binding.RecycleView.setAdapter(myAdapter);

                    setContentView(binding.getRoot());




                }); //You can then load the RecyclerView
            });

        }

       articleModel.selectedArticleItem.observe(this, (newArrticleItemValue) -> {

            Log.i("tag", "onCreate: " + newArrticleItemValue);




        ArticleDetailsFragment articleDetailsFragment = new ArticleDetailsFragment(newArrticleItemValue);


           getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLocation, articleDetailsFragment).addToBackStack("").commit();



        });



        binding.RecycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
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

                holder.row_imgSave.setVisibility(View.INVISIBLE);



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


        binding.RecycleView.setLayoutManager(new LinearLayoutManager(this));

    }





    class MyRowHolder extends RecyclerView.ViewHolder {

        public TextView row_headline;
        public TextView row_publication_date;
        public TextView row_url;
        public ImageView row_imgSave;

        public MyRowHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(clk -> {

                position = getAbsoluteAdapterPosition();
                ArticleItem selected = articleItems.get(position);
                articleModel.selectedArticleItem.postValue(selected);
                tv_headline=row_headline;

                IsSelected=true;
            });





            row_headline = itemView.findViewById(R.id.row_head_line);
            row_publication_date=itemView.findViewById(R.id.publication_date);
            row_url=itemView.findViewById(R.id.url);
            row_imgSave=itemView.findViewById(R.id.row_saveButton);



        }
    }


}