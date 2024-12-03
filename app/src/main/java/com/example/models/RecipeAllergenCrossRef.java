package com.example.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "recipe_allergen_junction",
        primaryKeys = {"recipeId", "allergenId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Recipe.class,
                        parentColumns = "id",
                        childColumns = "recipeId"
                ),
                @ForeignKey(
                        entity = Allergen.class,
                        parentColumns = "id",
                        childColumns = "allergenId"
                )
        },
        indices = {
                @Index(value = "recipeId"),
                @Index(value = "allergenId")
        }
)
public class RecipeAllergenCrossRef {
    public long recipeId;
    public long allergenId;

    // Constructor
    public RecipeAllergenCrossRef(long recipeId, long allergenId) {
        this.recipeId = recipeId;
        this.allergenId = allergenId;
    }
}