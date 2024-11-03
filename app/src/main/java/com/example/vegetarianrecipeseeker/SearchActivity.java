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
    private String savedSearchQuery = "";
    private int savedScrollPosition = 0;

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
            startActivity(intent);
        });

        // Restore saved instance state if available
        if (savedInstanceState != null) {
            savedSearchQuery = savedInstanceState.getString("SEARCH_QUERY", "");
            savedScrollPosition = savedInstanceState.getInt("LIST_SCROLL_POSITION", 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search here");

        // Restore search query if available
        if (!savedSearchQuery.isEmpty()) {
            menuItem.expandActionView();
            searchView.setQuery(savedSearchQuery, false);
            searchView.setIconified(false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                savedSearchQuery = newText;
                return false;
            }
        });

        listView.setSelection(savedScrollPosition);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SearchActivity.this, homeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("SEARCH_QUERY", savedSearchQuery);
        outState.putInt("LIST_SCROLL_POSITION", listView.getFirstVisiblePosition());
    }
}