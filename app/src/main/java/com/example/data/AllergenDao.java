package com.example.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.models.Allergen;
import com.example.models.RecipeAllergenCrossRef;

import java.util.List;

@Dao
public interface AllergenDao {
    @Insert
    long insertAllergen(Allergen allergen);

    @Insert
    void insertRecipeAllergenCrossRef(RecipeAllergenCrossRef crossRef);

    @Transaction
    @Query("SELECT a.* FROM allergens a " +
            "JOIN recipe_allergen_junction raj ON a.id = raj.allergenId " +
            "WHERE raj.recipeId = :recipeId")
    LiveData<List<Allergen>> getAllergensByRecipeId(long recipeId);

    @Query("SELECT * FROM allergens")
    LiveData<List<Allergen>> getAllAllergens();
}