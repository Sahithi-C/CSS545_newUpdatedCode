package com.example.vegetarianrecipeseeker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;
import java.util.Set;

public class RecipeDetailActivity extends AppCompatActivity {

    private Set<String> favorites;
    private SharedPreferences sharedPreferences;

    private ImageView recipeImage;
    private TextView recipeTitle, ingredientsList, instructionsList;
    private ImageView backButton, homeButton, favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        // Apply window insets for edge-to-edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        backButton = findViewById(R.id.backButton);
        homeButton = findViewById(R.id.homeButton);
        recipeImage = findViewById(R.id.recipeImage);
        recipeTitle = findViewById(R.id.recipeTitle);
        ingredientsList = findViewById(R.id.ingredientsList);
        instructionsList = findViewById(R.id.instructionsList);
        favoriteButton = findViewById(R.id.favoriteButton);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Favorites", MODE_PRIVATE);
        favorites = sharedPreferences.getStringSet("favorites", new HashSet<>());

        // Retrieve data from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("RECIPE_TITLE");
        int imageResource = R.drawable.logo1; // Replace with a default image or specific image logic

        // Set title and image
        recipeTitle.setText(title);
        recipeImage.setImageResource(imageResource);

        // Check if the recipe is Biryani
        if ("Biryani".equalsIgnoreCase(title)) {
            String ingredients = "Compulsory Ingredients Required:\n" +
                    "1 big Onion, 2 big tomatoes (puree), 250 gms Rice, 2 tbsp Curd, 1 tbsp Ginger-Garlic paste.\n\n" +
                    "Optional Ingredients:\n" +
                    "2 cups of vegetables (carrot, bell pepper, cauliflower, peas, beans, cottage cheese (paneer)).\n\n" +
                    "Compulsory Spices Required:\n" +
                    "1 tbsp Red Chili Powder, 1 tbsp Salt, 1/4 tbsp Turmeric Powder, 1 tbsp Coriander Powder, " +
                    "1/4 tbsp Cumin Powder, Bay Leaf, Cinnamon Stick, Green Cardamom, Cloves, Star Anise.\n\n" +
                    "Optional Spices:\n" +
                    "2 pinches of dried Kasuri Methi.";

            String instructions = "Recipe:\n" +
                    "1. Cook rice until al dente (Basmati rice recommended).\n" +
                    "2. Heat 4 tbsp of oil in a pan.\n" +
                    "3. Add whole spices: cumin seeds, bay leaf, cardamom, cloves, cinnamon stick, star anise.\n" +
                    "4. Add julienned onions and cook until golden brown.\n" +
                    "5. Lower heat, add ginger-garlic paste, and ground spices. Cook for 2 mins.\n" +
                    "6. (Optional) Add vegetables and cook for 5-7 mins.\n" +
                    "7. Add curd and let cook for 5 mins.\n" +
                    "8. Add tomato puree, salt, and kasuri methi. Cook until water evaporates.\n" +
                    "9. (Optional) Add paneer cubes, lightly pan-fried.\n" +
                    "10. Add 1/2 glass of water.\n" +
                    "11. Add rice on top, garnish, and steam for 10-15 mins.\n" +
                    "Serve hot!";

            // Set detailed recipe
            ingredientsList.setText(ingredients);
            instructionsList.setText(instructions);
        } else {
            // Show a generic message for other recipes
            ingredientsList.setText("Ingredients: Not Available for " + title);
            instructionsList.setText("Instructions: Not Available for " + title);
        }

        // Check if the recipe is favorited
        if (favorites.contains(title)) {
            favoriteButton.setImageResource(R.drawable.baseline_favorite_24); // Favorited icon
        } else {
            favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24); // Not favorited icon
        }

        // Set click listeners for back and home buttons
        backButton.setOnClickListener(v -> onBackPressed()); // Go back to the previous activity

        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(RecipeDetailActivity.this, homeScreen.class); // Replace with your actual home activity class
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent); // Start HomeActivity
            finish(); // Close RecipeDetailActivity
        });

        // Set click listener for favorite button
        favoriteButton.setOnClickListener(v -> {
            if (favorites.contains(title)) {
                favorites.remove(title);
                favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24); // Change to not favorited icon
            } else {
                favorites.add(title);
                favoriteButton.setImageResource(R.drawable.baseline_favorite_24); // Change to favorited icon
            }
            // Save updated favorites
            sharedPreferences.edit().putStringSet("favorites", favorites).apply();
        });
    }
}