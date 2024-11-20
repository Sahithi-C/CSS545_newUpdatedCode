package com.example.vegetarianrecipeseeker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.data.RecipeWithDetails;
import com.example.models.Ingredient;
import com.example.models.Instruction;
import com.example.viewmodel.RecipeViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    private RecipeViewModel recipeViewModel;
    private RecipeWithDetails currentRecipe;
    private ImageView favoriteButton;
    private View mainScrollView;
    private ScrollView recipeScrollView;
    private ImageButton scrollUpButton;
    private ImageButton scrollDownButton;

    private static final String KEY_RECIPE_TITLE = "recipe_title";
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private static final int MAX_FAVORITES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize ViewModel
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        initializeViews();
        setupNavigationButtons();
        setupScrollButtons();
        loadRecipeData(savedInstanceState);
    }

    private void initializeViews() {
        mainScrollView = findViewById(R.id.main);
        favoriteButton = findViewById(R.id.favoriteButton);
        recipeScrollView = findViewById(R.id.recipeScrollView);
        scrollUpButton = findViewById(R.id.scrollUpButton);
        scrollDownButton = findViewById(R.id.scrollDownButton);
    }

    private void setupNavigationButtons() {
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        ImageView homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> navigateToHome());
    }

    private void setupScrollButtons() {
        // Initialize scroll buttons
        scrollUpButton.setVisibility(View.GONE);
        scrollDownButton.setVisibility(View.GONE);

        final int scrollAmount = 800;

        scrollUpButton.setOnClickListener(v -> {
            recipeScrollView.smoothScrollBy(0, -scrollAmount);
            updateScrollButtonsVisibility();
        });

        scrollDownButton.setOnClickListener(v -> {
            recipeScrollView.smoothScrollBy(0, scrollAmount);
            updateScrollButtonsVisibility();
        });

        // Add scroll listener to track scroll changes
        recipeScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            recipeScrollView.post(this::updateScrollButtonsVisibility);
        });

        // Initial visibility check after layout
        recipeScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateScrollButtonsVisibility();
                // Remove the listener after first layout
                recipeScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void updateScrollButtonsVisibility() {
        if (recipeScrollView == null || recipeScrollView.getChildAt(0) == null) return;

        int totalHeight = recipeScrollView.getChildAt(0).getHeight();
        int scrollViewHeight = recipeScrollView.getHeight();
        int currentScroll = recipeScrollView.getScrollY();
        int maxScroll = totalHeight - scrollViewHeight;

        // Show down button if there's content below
        if (maxScroll > 0) {
            scrollDownButton.setVisibility(currentScroll >= maxScroll ? View.GONE : View.VISIBLE);
            scrollUpButton.setVisibility(currentScroll > 0 ? View.VISIBLE : View.GONE);
        } else {
            // If content fits without scrolling, hide both buttons
            scrollDownButton.setVisibility(View.GONE);
            scrollUpButton.setVisibility(View.GONE);
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(RecipeDetailActivity.this, homeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadRecipeData(Bundle savedInstanceState) {
        String recipeTitle;
        if (savedInstanceState != null) {
            recipeTitle = savedInstanceState.getString(KEY_RECIPE_TITLE);
            restoreScrollPosition(savedInstanceState.getInt(KEY_SCROLL_POSITION));
        } else {
            recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        }

        if (recipeTitle != null) {
            recipeViewModel.getRecipeByTitle(recipeTitle).observe(this, recipe -> {
                if (recipe != null) {
                    currentRecipe = recipe;
                    displayRecipe();
                } else {
                    showErrorAndFinish();
                }
            });
        } else {
            showErrorAndFinish();
        }
    }

    private void restoreScrollPosition(final int scrollPosition) {
        recipeScrollView.post(() -> {
            recipeScrollView.scrollTo(0, scrollPosition);
            updateScrollButtonsVisibility();
        });
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, "Error loading recipe", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentRecipe != null && currentRecipe.recipe != null) {
            outState.putString(KEY_RECIPE_TITLE, currentRecipe.recipe.title);
        }
        outState.putInt(KEY_SCROLL_POSITION, recipeScrollView.getScrollY());
    }

    @Override
    protected void onResume() {
        super.onResume();
        recipeScrollView.post(this::updateScrollButtonsVisibility);
    }

    private void displayRecipe() {
        TextView titleView = findViewById(R.id.recipeTitle);
        TextView cookingTimeText = findViewById(R.id.cookingTimeText);
        TextView spiceLevelText = findViewById(R.id.spiceLevelText);
        TextView ingredientsList = findViewById(R.id.ingredientsList);
        TextView instructionsList = findViewById(R.id.instructionsList);
        ImageView recipeImage = findViewById(R.id.recipeImage);

        String imageName = currentRecipe.recipe.imagePath;
        int resourceId = getResources().getIdentifier(
                imageName,
                "drawable",
                getPackageName()
        );

        if (resourceId != 0) {
            recipeImage.setImageResource(resourceId);
        } else {
            recipeImage.setImageResource(R.drawable.cooking);
        }

        titleView.setText(currentRecipe.recipe.title);
        cookingTimeText.setText(currentRecipe.recipe.cookingTime);
        spiceLevelText.setText(currentRecipe.recipe.spiceLevel);

        displayIngredients(ingredientsList);
        displayInstructions(instructionsList);
        setupFavoriteButton();

        // Reset scroll position and update button visibility
        recipeScrollView.post(() -> {
            recipeScrollView.scrollTo(0, 0);
            updateScrollButtonsVisibility();
        });
    }

    private void displayIngredients(TextView ingredientsList) {
        StringBuilder mandatory = new StringBuilder("Mandatory Ingredients:\n");
        StringBuilder optional = new StringBuilder("\nOptional Ingredients:\n");

        for (Ingredient ingredient : currentRecipe.ingredients) {
            if (ingredient.isMandatory) {
                mandatory.append("• ").append(ingredient.ingredientText).append("\n");
            } else {
                optional.append("• ").append(ingredient.ingredientText).append("\n");
            }
        }

        ingredientsList.setText(mandatory.toString() + optional.toString());
    }

    private void displayInstructions(TextView instructionsList) {
        List<Instruction> sortedInstructions = new ArrayList<>(currentRecipe.instructions);
        Collections.sort(sortedInstructions, (a, b) -> a.stepNumber - b.stepNumber);

        StringBuilder instructions = new StringBuilder("Instructions:\n");
        for (Instruction instruction : sortedInstructions) {
            instructions.append(instruction.stepNumber)
                    .append(". ")
                    .append(instruction.instructionText)
                    .append("\n\n");
        }
        instructionsList.setText(instructions.toString());
    }

    private void setupFavoriteButton() {
        updateFavoriteButton();
        favoriteButton.setOnClickListener(v -> handleFavoriteClick());
    }

    private void handleFavoriteClick() {
        if (!currentRecipe.recipe.isFavorite) {
            recipeViewModel.getFavoritesCount().observe(this, count -> {
                if (count < MAX_FAVORITES) {
                    toggleFavorite();
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    showFavoritesFullDialog();
                }
            });
        } else {
            toggleFavorite();
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFavorite() {
        currentRecipe.recipe.isFavorite = !currentRecipe.recipe.isFavorite;
        recipeViewModel.updateFavoriteStatus(currentRecipe.recipe.id, currentRecipe.recipe.isFavorite);
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        favoriteButton.setImageResource(currentRecipe.recipe.isFavorite ?
                R.drawable.baseline_favorite_24 :
                R.drawable.baseline_favorite_border_24);
    }

    private void showFavoritesFullDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Favorites List Full")
                .setMessage("You can only have " + MAX_FAVORITES + " favorite recipes. Would you like to view your favorites to remove some?")
                .setPositiveButton("View Favorites", (dialog, which) -> {
                    Intent intent = new Intent(RecipeDetailActivity.this, Favourites.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}