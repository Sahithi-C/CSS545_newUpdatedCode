package com.example.vegetarianrecipeseeker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipeDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RecipeDB";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_INGREDIENTS = "ingredients";
    private static final String TABLE_INSTRUCTIONS = "instructions";

    // Common column names
    private static final String KEY_ID = "id";

    // Recipes table columns
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_IS_FAVORITE = "is_favorite";

    // Ingredients table columns
    private static final String KEY_RECIPE_ID = "recipe_id";
    private static final String KEY_INGREDIENT_TEXT = "ingredient_text";
    private static final String KEY_IS_MANDATORY = "is_mandatory";

    // Instructions table columns
    private static final String KEY_STEP_NUMBER = "step_number";
    private static final String KEY_INSTRUCTION_TEXT = "instruction_text";

    // Create table statements
    private static final String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TITLE + " TEXT,"
            + KEY_IMAGE_PATH + " TEXT,"
            + KEY_IS_FAVORITE + " INTEGER DEFAULT 0"
            + ")";

    private static final String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_RECIPE_ID + " INTEGER,"
            + KEY_INGREDIENT_TEXT + " TEXT,"
            + KEY_IS_MANDATORY + " INTEGER DEFAULT 1,"
            + "FOREIGN KEY(" + KEY_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + KEY_ID + ")"
            + ")";

    private static final String CREATE_INSTRUCTIONS_TABLE = "CREATE TABLE " + TABLE_INSTRUCTIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_RECIPE_ID + " INTEGER,"
            + KEY_STEP_NUMBER + " INTEGER,"
            + KEY_INSTRUCTION_TEXT + " TEXT,"
            + "FOREIGN KEY(" + KEY_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + KEY_ID + ")"
            + ")";

    public RecipeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_INSTRUCTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // Insert a new recipe
    public long insertRecipe(String title, String imagePath, List<String> mandatoryIngredients,
                             List<String> optionalIngredients, List<String> instructions) {
        SQLiteDatabase db = this.getWritableDatabase();
        long recipeId = -1;

        db.beginTransaction();
        try {
            // Insert recipe
            ContentValues recipeValues = new ContentValues();
            recipeValues.put(KEY_TITLE, title);
            recipeValues.put(KEY_IMAGE_PATH, imagePath);
            recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

            // Insert mandatory ingredients
            for (String ingredient : mandatoryIngredients) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put(KEY_RECIPE_ID, recipeId);
                ingredientValues.put(KEY_INGREDIENT_TEXT, ingredient);
                ingredientValues.put(KEY_IS_MANDATORY, 1);
                db.insert(TABLE_INGREDIENTS, null, ingredientValues);
            }

            // Insert optional ingredients
            for (String ingredient : optionalIngredients) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put(KEY_RECIPE_ID, recipeId);
                ingredientValues.put(KEY_INGREDIENT_TEXT, ingredient);
                ingredientValues.put(KEY_IS_MANDATORY, 0);
                db.insert(TABLE_INGREDIENTS, null, ingredientValues);
            }

            // Insert instructions
            for (int i = 0; i < instructions.size(); i++) {
                ContentValues instructionValues = new ContentValues();
                instructionValues.put(KEY_RECIPE_ID, recipeId);
                instructionValues.put(KEY_STEP_NUMBER, i + 1);
                instructionValues.put(KEY_INSTRUCTION_TEXT, instructions.get(i));
                db.insert(TABLE_INSTRUCTIONS, null, instructionValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return recipeId;
    }

    // Get all recipe titles for the ListView
    public List<String> getAllRecipeTitles() {
        List<String> titles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPES,
                new String[]{KEY_TITLE},
                null, null, null, null,
                KEY_TITLE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                titles.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return titles;
    }

    // Get recipe details by title
    public Recipe getRecipeByTitle(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Recipe recipe = new Recipe();

        // Get recipe basic info
        Cursor recipeCursor = db.query(TABLE_RECIPES,
                new String[]{KEY_ID, KEY_TITLE, KEY_IMAGE_PATH, KEY_IS_FAVORITE},
                KEY_TITLE + "=?", new String[]{title},
                null, null, null);

        if (recipeCursor.moveToFirst()) {
            recipe.id = recipeCursor.getLong(0);
            recipe.title = recipeCursor.getString(1);
            recipe.imagePath = recipeCursor.getString(2);
            recipe.isFavorite = recipeCursor.getInt(3) == 1;

            // Get mandatory ingredients
            Cursor mandatoryIngredientsCursor = db.query(TABLE_INGREDIENTS,
                    new String[]{KEY_INGREDIENT_TEXT},
                    KEY_RECIPE_ID + "=? AND " + KEY_IS_MANDATORY + "=1",
                    new String[]{String.valueOf(recipe.id)},
                    null, null, null);

            while (mandatoryIngredientsCursor.moveToNext()) {
                recipe.mandatoryIngredients.add(mandatoryIngredientsCursor.getString(0));
            }
            mandatoryIngredientsCursor.close();

            // Get optional ingredients
            Cursor optionalIngredientsCursor = db.query(TABLE_INGREDIENTS,
                    new String[]{KEY_INGREDIENT_TEXT},
                    KEY_RECIPE_ID + "=? AND " + KEY_IS_MANDATORY + "=0",
                    new String[]{String.valueOf(recipe.id)},
                    null, null, null);

            while (optionalIngredientsCursor.moveToNext()) {
                recipe.optionalIngredients.add(optionalIngredientsCursor.getString(0));
            }
            optionalIngredientsCursor.close();

            // Get instructions
            Cursor instructionsCursor = db.query(TABLE_INSTRUCTIONS,
                    new String[]{KEY_INSTRUCTION_TEXT},
                    KEY_RECIPE_ID + "=?",
                    new String[]{String.valueOf(recipe.id)},
                    null, null, KEY_STEP_NUMBER + " ASC");

            while (instructionsCursor.moveToNext()) {
                recipe.instructions.add(instructionsCursor.getString(0));
            }
            instructionsCursor.close();
        }
        recipeCursor.close();
        return recipe;
    }

    // Update favorite status
    public void updateFavoriteStatus(long recipeId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_RECIPES, values, KEY_ID + "=?", new String[]{String.valueOf(recipeId)});
    }

    // Recipe class to hold all recipe data
    public static class Recipe {
        public long id;
        public String title;
        public String imagePath;
        public boolean isFavorite;
        public List<String> mandatoryIngredients = new ArrayList<>();
        public List<String> optionalIngredients = new ArrayList<>();
        public List<String> instructions = new ArrayList<>();
    }
}
