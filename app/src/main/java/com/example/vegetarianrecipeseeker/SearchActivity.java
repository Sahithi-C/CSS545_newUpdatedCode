package com.example.vegetarianrecipeseeker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    String[] dishesList = {"Biryani", "Paneer Butter Masala", "Corn Rice", "Fried Rice", "Okra Curry", "Beans Curry", "Carrot Curry"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishesList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            String selectedRecipe = arrayAdapter.getItem(position);

            Intent intent = new Intent(SearchActivity.this, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_TITLE", selectedRecipe);
            // You can also add more data related to the selected recipe here
            // intent.putExtra("RECIPE_INGREDIENTS", "Your ingredients here");
            // intent.putExtra("RECIPE_INSTRUCTIONS", "Your instructions here");
            // intent.putExtra("RECIPE_IMAGE", R.drawable.your_image_resource); // Assuming you have an image resource
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the Up button click
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SearchActivity.this, homeScreen.class); // Replace HomeActivity with your actual home activity class
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); // Start HomeActivity
            finish(); // Close SearchActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}