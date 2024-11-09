package com.example.vegetarianrecipeseeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {
    private RecipeDBHelper dbHelper;
    private RecipeDBHelper.Recipe currentRecipe;
    private ImageView favoriteButton;

    // Key for saving state
    private static final String KEY_RECIPE_TITLE = "recipe_title";
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private static final String KEY_IS_FAVORITE = "is_favorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize database helper
        dbHelper = new RecipeDBHelper(this);

        // Setup back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Setup home button
        ImageView homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeDetailActivity.this, homeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Check if we're recreating from a saved state
        String recipeTitle;
        if (savedInstanceState != null) {
            recipeTitle = savedInstanceState.getString(KEY_RECIPE_TITLE);
            // Restore scroll position after the layout is complete
            final int scrollPosition = savedInstanceState.getInt(KEY_SCROLL_POSITION);
            findViewById(R.id.main).post(() -> {
                findViewById(R.id.main).scrollTo(0, scrollPosition);
            });
        } else {
            // Get recipe title from intent
            recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        }

        if (recipeTitle != null) {
            // Load recipe details from database
            currentRecipe = dbHelper.getRecipeByTitle(recipeTitle);
            displayRecipe();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh recipe data in case it was modified elsewhere
        if (currentRecipe != null) {
            currentRecipe = dbHelper.getRecipeByTitle(currentRecipe.title);
            displayRecipe();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current recipe title
        if (currentRecipe != null) {
            outState.putString(KEY_RECIPE_TITLE, currentRecipe.title);
            outState.putBoolean(KEY_IS_FAVORITE, currentRecipe.isFavorite);
        }
        // Save scroll position
        outState.putInt(KEY_SCROLL_POSITION, findViewById(R.id.main).getScrollY());
    }

    private void displayRecipe() {
        // Find all views
        TextView titleView = findViewById(R.id.recipeTitle);
        TextView ingredientsList = findViewById(R.id.ingredientsList);
        TextView instructionsList = findViewById(R.id.instructionsList);
        ImageView recipeImage = findViewById(R.id.recipeImage);
        favoriteButton = findViewById(R.id.favoriteButton);

        // Set recipe title
        titleView.setText(currentRecipe.title);

        // Build ingredients text
        StringBuilder ingredients = new StringBuilder("Mandatory Ingredients:\n");
        for (String ingredient : currentRecipe.mandatoryIngredients) {
            ingredients.append(ingredient).append("\n");
        }
        ingredients.append("\nOptional Ingredients:\n");
        for (String ingredient : currentRecipe.optionalIngredients) {
            ingredients.append(ingredient).append("\n");
        }
        ingredientsList.setText(ingredients.toString());

        // Build instructions text
        StringBuilder instructions = new StringBuilder("Recipe:\n");
        for (int i = 0; i < currentRecipe.instructions.size(); i++) {
            instructions.append(i + 1)
                    .append(". ")
                    .append(currentRecipe.instructions.get(i))
                    .append("\n");
        }
        instructionsList.setText(instructions.toString());

        // Set favorite button state and click listener
        updateFavoriteButton();
        favoriteButton.setOnClickListener(v -> toggleFavorite());
    }

    private void updateFavoriteButton() {
        favoriteButton.setImageResource(currentRecipe.isFavorite ?
                R.drawable.baseline_favorite_24 :
                R.drawable.baseline_favorite_border_24);
    }

    private void toggleFavorite() {
        currentRecipe.isFavorite = !currentRecipe.isFavorite;
        dbHelper.updateFavoriteStatus(currentRecipe.id, currentRecipe.isFavorite);
        updateFavoriteButton();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}