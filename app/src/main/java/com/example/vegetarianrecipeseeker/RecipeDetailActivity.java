package com.example.vegetarianrecipeseeker;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.data.RecipeWithDetails;
import com.example.models.Allergen;
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
    private ImageView allergenWarningButton;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String PREFS_KEY = "hasSeenAllergenMessage";

    // Constants for saving and restoring state
    private static final String KEY_RECIPE_TITLE = "recipe_title";
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private static final String KEY_IS_SCROLL_RESTORED = "is_scroll_restored";
    private static final int MAX_FAVORITES = 5;

    // Flag to track if scroll position has been restored
    private boolean isScrollRestored = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        allergenWarningButton = findViewById(R.id.allergenWarningButton);

        // Check if the user has already seen the allergen instruction message
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean hasSeenAllergenMessage = prefs.getBoolean(PREFS_KEY, false);

        if (!hasSeenAllergenMessage) {
            // Show toast message explaining the allergen icon functionality
            Toast.makeText(this, "Click on the allergen icon for warnings", Toast.LENGTH_LONG).show();

            // Save that the user has seen the message to prevent showing it again
            prefs.edit().putBoolean(PREFS_KEY, true).apply();
        }

        // 1. Tooltip for the allergen icon
        ViewCompat.setTooltipText(allergenWarningButton, "Click for allergen warnings");

        allergenWarningButton.setOnLongClickListener(v -> {
            // Show tooltip on long press
            return true; // Return true to consume the event
        });

        // 2. Pulse animation to draw attention to the allergen icon
        ObjectAnimator pulseAnimator = ObjectAnimator.ofFloat(allergenWarningButton, "alpha", 1f, 0.5f, 1f);
        pulseAnimator.setDuration(1000); // Duration for one pulse cycle
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat infinitely
        pulseAnimator.start();

        // 3. Highlight the icon by changing its background color temporarily
        allergenWarningButton.setBackgroundColor(getResources().getColor(R.color.colorAccent)); // Temporary highlight color

        // After 2 seconds, reset background color to transparent
        new Handler().postDelayed(() -> allergenWarningButton.setBackgroundColor(getResources().getColor(R.color.transparent)), 2000);

        // Initialize ViewModel
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        initializeViews();
        setupNavigationButtons();
        setupScrollButtons();

        // Restore saved state if available
        if (savedInstanceState != null) {
            isScrollRestored = savedInstanceState.getBoolean(KEY_IS_SCROLL_RESTORED, false);
        }

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
        final int[] savedScrollPosition = {0};

        if (savedInstanceState != null) {
            recipeTitle = savedInstanceState.getString(KEY_RECIPE_TITLE);
            savedScrollPosition[0] = savedInstanceState.getInt(KEY_SCROLL_POSITION, 0);
        } else {
            recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        }

        if (recipeTitle != null) {
            recipeViewModel.getRecipeByTitle(recipeTitle).observe(this, recipe -> {
                if (recipe != null) {
                    currentRecipe = recipe;
                    displayRecipe();

                    // Restore scroll position after layout is complete
                    if (!isScrollRestored) {
                        recipeScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                recipeScrollView.scrollTo(0, savedScrollPosition[0]);
                                updateScrollButtonsVisibility();

                                // Remove the listener to prevent multiple calls
                                recipeScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                // Mark scroll as restored
                                isScrollRestored = true;
                            }
                        });
                    }
                } else {
                    showErrorAndFinish();
                }
            });
        } else {
            showErrorAndFinish();
        }
    }

    private void restoreScrollPosition(final int scrollPosition) {
        if (!isScrollRestored) {
            recipeScrollView.post(() -> {
                recipeScrollView.scrollTo(0, scrollPosition);
                updateScrollButtonsVisibility();
                isScrollRestored = true;
            });
        }
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, "Error loading recipe", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save recipe title
        if (currentRecipe != null && currentRecipe.recipe != null) {
            outState.putString(KEY_RECIPE_TITLE, currentRecipe.recipe.title);
        }

        // Save scroll position
        if (recipeScrollView != null) {
            int scrollPosition = recipeScrollView.getScrollY();
            outState.putInt(KEY_SCROLL_POSITION, scrollPosition);

            // Save flag to track if scroll has been restored
            outState.putBoolean(KEY_IS_SCROLL_RESTORED, isScrollRestored);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore scroll position flag
        isScrollRestored = savedInstanceState.getBoolean(KEY_IS_SCROLL_RESTORED, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset scroll restoration flag when returning to the activity
        isScrollRestored = false;

        // Update scroll buttons visibility
        recipeScrollView.post(this::updateScrollButtonsVisibility);
    }

    private void showAllergenWarningDialog() {
        // Ensure currentRecipe is loaded
        if (currentRecipe == null || currentRecipe.recipe == null) {
            Toast.makeText(this, "Recipe information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a dialog to show allergen information
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Allergen Warnings");

        // Fetch allergens for this recipe
        recipeViewModel.getAllergensByRecipeId(currentRecipe.recipe.id).observe(this, allergens -> {
            // Prepare the dialog message
            String allergenMessage;

            // Predefined allergen warnings for specific recipes
            switch (currentRecipe.recipe.title) {
                case "Biryani":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Dairy Allergens: Contains yogurt (curd) and potentially ghee/clarified butter.\n" +
                            "• Milk Protein: High risk for those with milk protein sensitivities.\n" +
                            "• Gluten Potential: May contain gluten from biryani masala and spice blends.\n" +
                            "• Spice Sensitivities: Contains multiple whole spices (cardamom, cloves, cinnamon, bay leaves) which can trigger sensitivities.\n" +
                            "• Nut Cross-Contamination: Potential risk of trace nut contamination during preparation.";
                    break;

                case "Paneer Butter Masala":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Dairy Allergens: Contains extensive dairy products (butter, cream, paneer).\n" +
                            "• Milk Protein: Extremely high dairy content, severe risk for milk-allergic individuals.\n" +
                            "• Nut Allergens: Contains whole cashews in the recipe.\n" +
                            "• Gluten Potential: May contain gluten from spice blends and potential cross-contamination.\n" +
                            "• Spice Sensitivities: Contains multiple spices that might trigger reactions.\n" +
                            "• Soy Risk: Potential soy contamination in spice processing.\n" +
                            "• Other Allergens: Garlic and ginger may cause allergic reactions in some individuals.";
                    break;

                case "Peanut Chutney":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Peanut Allergy: PRIMARY INGREDIENT IS PEANUTS - EXTREME CAUTION!\n" +
                            "• Nut Allergens: Contains whole peanuts, risk of other nut cross-contamination.\n" +
                            "• Seed Allergens: Potential mustard seed presence.\n" +
                            "• Spice Sensitivities: Contains green chilies, garlic that might trigger reactions.\n" +
                            "• Gluten Potential: Risk if served with bread or gluten-containing accompaniments.\n" +
                            "• Other Allergens: Contains tamarind which can cause allergic reactions in some people.";
                    break;

                case "Okra Fry Curry":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Nut Allergens: Contains roasted peanuts.\n" +
                            "• Legume Allergens: Contains chana dal and urad dal.\n" +
                            "• Spice Sensitivities: Multiple spices including turmeric and chili powder.\n" +
                            "• Coconut Allergen: Contains coconut powder.\n" +
                            "• Gluten Potential: Risk if served with bread or roti.\n" +
                            "• Other Allergens: Curry leaves might trigger sensitivities in some individuals.";
                    break;

                case "Veg Hakka Noodles":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Gluten Allergens: Contains flour-based noodles.\n" +
                            "• Soy Allergens: Contains soy sauce.\n" +
                            "• Wheat Sensitivity: High gluten content from noodles.\n" +
                            "• Spice Sensitivities: Contains garlic, ginger, and pepper.\n" +
                            "• Potential Seed Allergens: Possible sesame in soy sauce.";
                    break;

                case "Upma":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Gluten Allergens: Contains semolina (wheat derivative).\n" +
                            "• Nut Allergens: Contains peanuts.\n" +
                            "• Legume Allergens: Contains chana dal and urad dal.\n" +
                            "• Spice Sensitivities: Multiple spices including mustard seeds.\n" +
                            "• Vegetable Allergens: Contains multiple vegetables that might trigger reactions.\n" +
                            "• Other Allergens: Curry leaves and green chilies can cause sensitivities.";
                    break;

                case "Veg Momos":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Gluten Allergens: Contains maida/plain flour.\n" +
                            "• Wheat Sensitivity: High gluten content.\n" +
                            "• Soy Potential: Possible soy sauce in preparation.\n" +
                            "• Spice Sensitivities: Contains garlic, ginger, and chili.\n" +
                            "• Vegetable Allergens: Contains multiple vegetables.\n" +
                            "• Other Allergens: Potential nut oil or seed oil used in preparation.";
                    break;

                case "Momos Chutney":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Nut Allergens: Contains blanched almonds.\n" +
                            "• Soy Allergens: Contains soy sauce.\n" +
                            "• Seed Allergens: Potential mustard or sesame in soy sauce.\n" +
                            "• Spice Sensitivities: Contains dried red chilies and garlic.\n" +
                            "• Vinegar Sensitivity: Contains vinegar which can trigger reactions.\n" +
                            "• Other Allergens: Potential cross-contamination during preparation.";
                    break;

                case "Spinach Soup":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Dairy Allergens: Contains milk, butter, and cream.\n" +
                            "• Milk Protein: High dairy content, severe risk for milk-allergic individuals.\n" +
                            "• Gluten Potential: Contains maida (all-purpose flour).\n" +
                            "• Spice Sensitivities: Contains bay leaf and pepper.\n" +
                            "• Other Allergens: Potential cross-contamination in kitchen preparation.";
                    break;

                case "Corn Rice":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Spice Sensitivities: Contains multiple whole spices (cardamom, cinnamon, cloves).\n" +
                            "• Gluten Potential: Risk of cross-contamination.\n" +
                            "• Vegetable Allergens: Contains bell peppers and corn.\n" +
                            "• Other Allergens: Contains ginger and garlic that might trigger reactions.\n" +
                            "• Seed Allergens: Potential cumin and other seed-based spices.";
                    break;

                case "Fried Rice":
                    allergenMessage = "Allergen Warning:\n" +
                            "• Soy Allergens: Contains soy sauce.\n" +
                            "• Gluten Potential: Soy sauce and potential cross-contamination.\n" +
                            "• Vinegar Sensitivity: Contains vinegar.\n" +
                            "• Vegetable Allergens: Multiple vegetables present.\n" +
                            "• Spice Sensitivities: Contains garlic and pepper.\n" +
                            "• Other Allergens: Potential nut oil used in preparation.";
                    break;

                default:
                    if (allergens == null || allergens.isEmpty()) {
                        allergenMessage = "No specific allergen warnings for this recipe.\n" +
                                "As always, check ingredient labels and be aware of potential cross-contamination.";
                    } else {
                        // Existing code to generate allergen message from database
                        StringBuilder messageBuilder = new StringBuilder();
                        int commonAllergenCount = 0;
                        int recipeSpecificAllergenCount = 0;

                        for (Allergen allergen : allergens) {
                            if (allergen.isCommonAllergen) {
                                commonAllergenCount++;
                            } else {
                                recipeSpecificAllergenCount++;
                            }

                            messageBuilder.append("- ")
                                    .append(allergen.name)
                                    .append(": ")
                                    .append(allergen.description)
                                    .append("\n");
                        }

                        messageBuilder.insert(0, String.format(
                                "This recipe contains:\n" +
                                        "• %d common food allergen%s\n" +
                                        "• %d recipe-specific allergen%s\n\n",
                                commonAllergenCount,
                                commonAllergenCount != 1 ? "s" : "",
                                recipeSpecificAllergenCount,
                                recipeSpecificAllergenCount != 1 ? "s" : ""
                        ));

                        allergenMessage = messageBuilder.toString().trim();
                    }
                    break;
            }

            // Add the disclaimer message
            allergenMessage += "\n\nDisclaimer: The allergen information provided is based on our current knowledge. We recommend that you check the ingredients before consumption. We do not take responsibility for any allergic reactions.";

            // Create SpannableString to style the dialog text
            SpannableString spannableMessage = new SpannableString(allergenMessage);

            // First, set the entire text to black
            spannableMessage.setSpan(new ForegroundColorSpan(Color.BLACK), 0, allergenMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Then apply yellow to the "Allergen Warning:" heading
            int headingStart = allergenMessage.indexOf("Allergen Warning:");
            int headingEnd = headingStart + "Allergen Warning:".length();
            // Using a predefined dark yellow color
            spannableMessage.setSpan(new ForegroundColorSpan(Color.rgb(204, 153, 0)), headingStart, headingEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Then apply red to the disclaimer
            int disclaimerStart = allergenMessage.indexOf("Disclaimer:");
            int disclaimerEnd = allergenMessage.length();
            spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), disclaimerStart, disclaimerEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the message and show the dialog
            builder.setMessage(spannableMessage)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setIcon(R.drawable.baseline_warning_24);

            // Change icon color to yellow
            Drawable icon = getResources().getDrawable(R.drawable.baseline_warning_24);
            icon.setColorFilter(new PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN));

            // Set the modified icon
            builder.setIcon(icon);

            // Create and show the dialog on the main thread
            runOnUiThread(() -> {
                try {
                    if (builder == null) {
                        Log.e("AllergenDialog", "AlertDialog.Builder is null");
                        Toast.makeText(this, "Dialog builder is null", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog dialog = builder.create();

                    if (dialog == null) {
                        Log.e("AllergenDialog", "Created dialog is null");
                        Toast.makeText(this, "Dialog creation failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    dialog.show();

                    // Null checks for SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    if (prefs == null) {
                        Log.e("AllergenDialog", "SharedPreferences is null");
                    }

                    // Null check for allergenWarningButton
                    if (allergenWarningButton == null) {
                        Log.e("AllergenDialog", "allergenWarningButton is null");
                    } else {
                        // Existing button-related code with null checks
                        ViewCompat.setTooltipText(allergenWarningButton, "Click for allergen warnings");

                        allergenWarningButton.setOnLongClickListener(v -> {
                            return true;
                        });

                        // Pulse animation
                        ObjectAnimator pulseAnimator = ObjectAnimator.ofFloat(allergenWarningButton, "alpha", 1f, 0.5f, 1f);
                        pulseAnimator.setDuration(1000);
                        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                        pulseAnimator.start();

                        // Background color changes
                        try {
                            allergenWarningButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            new Handler().postDelayed(() -> {
                                if (allergenWarningButton != null) {
                                    allergenWarningButton.setBackgroundColor(getResources().getColor(R.color.transparent));
                                }
                            }, 2000);
                        } catch (Exception e) {
                            Log.e("AllergenDialog", "Error setting button background", e);
                        }
                    }

                    // Toast and animation
                    Toast toast = Toast.makeText(this, "Allergen Information", Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    if (toastView != null) {
                        toastView.setBackgroundColor(Color.BLACK);
                        toast.show();

                        // Color change animator
                        ObjectAnimator colorChangeAnimator = ObjectAnimator.ofArgb(
                                toastView, "backgroundColor", Color.YELLOW, Color.RED);
                        colorChangeAnimator.setDuration(500);
                        colorChangeAnimator.start();
                    }

                } catch (Exception e) {
                    // Comprehensive error logging
                    Log.e("AllergenDialog", "Comprehensive error details:", e);
                    e.printStackTrace();

                    // More detailed error toast
                    Toast.makeText(this, "Dialog error: " + e.getClass().getName() + " - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

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

        // Add a new button for allergen warnings
        ImageView allergenWarningButton = findViewById(R.id.allergenWarningButton);
        allergenWarningButton.setOnClickListener(v -> showAllergenWarningDialog());
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

            // Optional: If you want to immediately remove from Favorites screen if open
            if (getCallingActivity() != null &&
                    getCallingActivity().getClassName().equals(Favourites.class.getName())) {
                setResult(RESULT_OK);
                finish();
            }
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
