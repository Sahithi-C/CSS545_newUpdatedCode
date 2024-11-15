package com.example.vegetarianrecipeseeker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private RecipeDBHelper dbHelper;
    private SearchView searchView;
    private String savedSearchQuery = "";
    private int savedScrollPosition = 0;
    private static final String PREFS_NAME = "SearchActivityPrefs";
    private static final String KEY_SEARCH_QUERY = "search_query";
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize database
        dbHelper = new RecipeDBHelper(this);

        // Add sample data if the database is empty
        if (dbHelper.getAllRecipeTitles().isEmpty()) {
            addSampleRecipes();
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup ListView with data from database
        listView = findViewById(R.id.listView);
        List<String> recipeList = dbHelper.getAllRecipeTitles();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        listView.setAdapter(arrayAdapter);

        // Handle recipe selection
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRecipe = arrayAdapter.getItem(position);
            Intent intent = new Intent(SearchActivity.this, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_TITLE", selectedRecipe);
            startActivity(intent);
        });

        // Restore state from either savedInstanceState or SharedPreferences
        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore from saved instance state (for configuration changes)
            savedSearchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY, "");
            savedScrollPosition = savedInstanceState.getInt(KEY_SCROLL_POSITION, 0);
        } else {
            // Restore from SharedPreferences (for app suspension/resume)
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            savedSearchQuery = prefs.getString(KEY_SEARCH_QUERY, "");
            savedScrollPosition = prefs.getInt(KEY_SCROLL_POSITION, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Apply saved state if search view is initialized
        if (searchView != null && !savedSearchQuery.isEmpty()) {
            searchView.setQuery(savedSearchQuery, true);
        }
        // Restore scroll position
        if (listView != null) {
            listView.setSelection(savedScrollPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save state to SharedPreferences
        saveCurrentState();
    }

    private void saveCurrentState() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_SEARCH_QUERY, savedSearchQuery);
        editor.putInt(KEY_SCROLL_POSITION, listView.getFirstVisiblePosition());
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        this.optionsMenu = menu;
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (SearchView) menuItem.getActionView();
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
                savedSearchQuery = newText;
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Set up SearchView expansion listener
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                savedSearchQuery = ""; // Clear saved query when search is closed
                saveCurrentState();
                return true;
            }
        });

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SEARCH_QUERY, savedSearchQuery);
        outState.putInt(KEY_SCROLL_POSITION, listView.getFirstVisiblePosition());
    }

    private void addSampleRecipes() {
        // Sample recipe 1: Biryani
        List<String> biryaniMandatory = Arrays.asList(
                "1 big Onion",
                "2 big tomatoes (puree)",
                "250 gms Rice",
                "2 tbsp Curd",
                "1 tbsp Ginger-Garlic paste"
        );

        List<String> biryaniOptional = Arrays.asList(
                "2 cups of vegetables (carrot, bell pepper, cauliflower, peas, beans)",
                "cottage cheese (paneer)"
        );

        List<String> biryaniInstructions = Arrays.asList(
                "Cook rice until al dente (Basmati rice recommended).",
                "Heat 4 tbsp of oil in a pan.",
                "Add whole spices: cumin seeds, bay leaf, cardamom, cloves, cinnamon stick, star anise.",
                "Add julienned onions and cook until golden brown.",
                "Lower heat, add ginger-garlic paste, and ground spices. Cook for 2 mins.",
                "(Optional) Add vegetables and cook for 5-7 mins.",
                "Add curd and let cook for 5 mins.",
                "Add tomato puree, salt, and kasuri methi. Cook until water evaporates.",
                "(Optional) Add paneer cubes, lightly pan-fried.",
                "Add 1/2 glass of water.",
                "Add rice on top, garnish, and steam for 10-15 mins."
        );

        dbHelper.insertRecipe("Biryani", "", 4, biryaniMandatory, biryaniOptional, biryaniInstructions);

        // Sample recipe 2: Paneer Butter Masala
        List<String> paneerMandatory = Arrays.asList(
                "250g Paneer",
                "2 Onions",
                "3 Tomatoes",
                "2 tbsp Butter",
                "1 tbsp Cream"
        );

        List<String> paneerOptional = Arrays.asList(
                "Kasuri Methi",
                "Coriander leaves for garnishing"
        );

        List<String> paneerInstructions = Arrays.asList(
                "Cut paneer into cubes and soak in warm water.",
                "Sauté onions until golden brown.",
                "Add tomatoes and cook until soft.",
                "Blend the mixture into a smooth paste.",
                "Heat butter in a pan and add the paste.",
                "Add spices and cook for 5 minutes.",
                "Add paneer cubes and simmer for 5 minutes.",
                "Add cream and garnish with coriander leaves."
        );

        dbHelper.insertRecipe("Paneer Butter Masala", "", 1, paneerMandatory, paneerOptional, paneerInstructions);
    }
}