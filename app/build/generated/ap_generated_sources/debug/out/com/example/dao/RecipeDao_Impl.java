package com.example.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.data.RecipeWithDetails;
import com.example.models.Allergen;
import com.example.models.Ingredient;
import com.example.models.Instruction;
import com.example.models.Recipe;
import com.example.models.RecipeAllergenCrossRef;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.Unit;

@SuppressWarnings({"unchecked", "deprecation"})
public final class RecipeDao_Impl implements RecipeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Recipe> __insertionAdapterOfRecipe;

  private final EntityInsertionAdapter<Ingredient> __insertionAdapterOfIngredient;

  private final EntityInsertionAdapter<Instruction> __insertionAdapterOfInstruction;

  private final EntityInsertionAdapter<Allergen> __insertionAdapterOfAllergen;

  private final EntityInsertionAdapter<RecipeAllergenCrossRef> __insertionAdapterOfRecipeAllergenCrossRef;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllergensByRecipeId;

  public RecipeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecipe = new EntityInsertionAdapter<Recipe>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `recipes` (`id`,`title`,`image_path`,`is_favorite`,`cooking_time`,`spice_level`,`allergens`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Recipe entity) {
        statement.bindLong(1, entity.id);
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.title);
        }
        if (entity.imagePath == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.imagePath);
        }
        final int _tmp = entity.isFavorite ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.cookingTime == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.cookingTime);
        }
        if (entity.spiceLevel == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.spiceLevel);
        }
        if (entity.allergens == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.allergens);
        }
      }
    };
    this.__insertionAdapterOfIngredient = new EntityInsertionAdapter<Ingredient>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `ingredients` (`id`,`recipe_id`,`ingredient_text`,`is_mandatory`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final Ingredient entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.recipeId);
        if (entity.ingredientText == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.ingredientText);
        }
        final int _tmp = entity.isMandatory ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__insertionAdapterOfInstruction = new EntityInsertionAdapter<Instruction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `instructions` (`id`,`recipe_id`,`step_number`,`instruction_text`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final Instruction entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.recipeId);
        statement.bindLong(3, entity.stepNumber);
        if (entity.instructionText == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.instructionText);
        }
      }
    };
    this.__insertionAdapterOfAllergen = new EntityInsertionAdapter<Allergen>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `allergens` (`id`,`name`,`description`,`is_common_allergen`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Allergen entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        if (entity.description == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.description);
        }
        final int _tmp = entity.isCommonAllergen ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__insertionAdapterOfRecipeAllergenCrossRef = new EntityInsertionAdapter<RecipeAllergenCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `recipe_allergen_junction` (`recipeId`,`allergenId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final RecipeAllergenCrossRef entity) {
        statement.bindLong(1, entity.recipeId);
        statement.bindLong(2, entity.allergenId);
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE recipes SET is_favorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllergensByRecipeId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM recipe_allergen_junction WHERE recipeId = ?";
        return _query;
      }
    };
  }

  @Override
  public long insertRecipe(final Recipe recipe) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfRecipe.insertAndReturnId(recipe);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertIngredients(final List<Ingredient> ingredients) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfIngredient.insert(ingredients);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertInstructions(final List<Instruction> instructions) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfInstruction.insert(instructions);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertAllergen(final Allergen allergen) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfAllergen.insertAndReturnId(allergen);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertRecipeAllergenCrossRef(final RecipeAllergenCrossRef crossRef) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfRecipeAllergenCrossRef.insert(crossRef);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertRecipeWithDetails(final Recipe recipe, final List<Ingredient> ingredients,
      final List<Instruction> instructions) {
    __db.beginTransaction();
    try {
      final long _result;
      _result = RecipeDao.super.insertRecipeWithDetails(recipe, ingredients, instructions);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateFavoriteStatus(final long recipeId, final boolean isFavorite) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFavoriteStatus.acquire();
    int _argIndex = 1;
    final int _tmp = isFavorite ? 1 : 0;
    _stmt.bindLong(_argIndex, _tmp);
    _argIndex = 2;
    _stmt.bindLong(_argIndex, recipeId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateFavoriteStatus.release(_stmt);
    }
  }

  @Override
  public void deleteAllergensByRecipeId(final long recipeId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllergensByRecipeId.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, recipeId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAllergensByRecipeId.release(_stmt);
    }
  }

  @Override
  public List<Recipe> getAllRecipes() {
    final String _sql = "SELECT * FROM recipes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
      final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
      final int _cursorIndexOfCookingTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cooking_time");
      final int _cursorIndexOfSpiceLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "spice_level");
      final int _cursorIndexOfAllergens = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens");
      final List<Recipe> _result = new ArrayList<Recipe>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Recipe _item;
        _item = new Recipe();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfImagePath)) {
          _item.imagePath = null;
        } else {
          _item.imagePath = _cursor.getString(_cursorIndexOfImagePath);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
        _item.isFavorite = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfCookingTime)) {
          _item.cookingTime = null;
        } else {
          _item.cookingTime = _cursor.getString(_cursorIndexOfCookingTime);
        }
        if (_cursor.isNull(_cursorIndexOfSpiceLevel)) {
          _item.spiceLevel = null;
        } else {
          _item.spiceLevel = _cursor.getString(_cursorIndexOfSpiceLevel);
        }
        if (_cursor.isNull(_cursorIndexOfAllergens)) {
          _item.allergens = null;
        } else {
          _item.allergens = _cursor.getString(_cursorIndexOfAllergens);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Recipe getRecipeByTitle(final String title) {
    final String _sql = "SELECT * FROM recipes WHERE title = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (title == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, title);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
      final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
      final int _cursorIndexOfCookingTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cooking_time");
      final int _cursorIndexOfSpiceLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "spice_level");
      final int _cursorIndexOfAllergens = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens");
      final Recipe _result;
      if (_cursor.moveToFirst()) {
        _result = new Recipe();
        _result.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _result.title = null;
        } else {
          _result.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfImagePath)) {
          _result.imagePath = null;
        } else {
          _result.imagePath = _cursor.getString(_cursorIndexOfImagePath);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
        _result.isFavorite = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfCookingTime)) {
          _result.cookingTime = null;
        } else {
          _result.cookingTime = _cursor.getString(_cursorIndexOfCookingTime);
        }
        if (_cursor.isNull(_cursorIndexOfSpiceLevel)) {
          _result.spiceLevel = null;
        } else {
          _result.spiceLevel = _cursor.getString(_cursorIndexOfSpiceLevel);
        }
        if (_cursor.isNull(_cursorIndexOfAllergens)) {
          _result.allergens = null;
        } else {
          _result.allergens = _cursor.getString(_cursorIndexOfAllergens);
        }
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public RecipeWithDetails getRecipeWithDetailsByTitle(final String title) {
    final String _sql = "SELECT * FROM recipes WHERE title = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (title == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, title);
    }
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
      try {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
        final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
        final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
        final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
        final int _cursorIndexOfCookingTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cooking_time");
        final int _cursorIndexOfSpiceLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "spice_level");
        final int _cursorIndexOfAllergens = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens");
        final LongSparseArray<ArrayList<Ingredient>> _collectionIngredients = new LongSparseArray<ArrayList<Ingredient>>();
        final LongSparseArray<ArrayList<Instruction>> _collectionInstructions = new LongSparseArray<ArrayList<Instruction>>();
        while (_cursor.moveToNext()) {
          final Long _tmpKey;
          if (_cursor.isNull(_cursorIndexOfId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _cursor.getLong(_cursorIndexOfId);
          }
          if (_tmpKey != null) {
            if (!_collectionIngredients.containsKey(_tmpKey)) {
              _collectionIngredients.put(_tmpKey, new ArrayList<Ingredient>());
            }
          }
          final Long _tmpKey_1;
          if (_cursor.isNull(_cursorIndexOfId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionInstructions.containsKey(_tmpKey_1)) {
              _collectionInstructions.put(_tmpKey_1, new ArrayList<Instruction>());
            }
          }
        }
        _cursor.moveToPosition(-1);
        __fetchRelationshipingredientsAscomExampleModelsIngredient(_collectionIngredients);
        __fetchRelationshipinstructionsAscomExampleModelsInstruction(_collectionInstructions);
        final RecipeWithDetails _result;
        if (_cursor.moveToFirst()) {
          final Recipe _tmpRecipe;
          if (!(_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfTitle) && _cursor.isNull(_cursorIndexOfImagePath) && _cursor.isNull(_cursorIndexOfIsFavorite) && _cursor.isNull(_cursorIndexOfCookingTime) && _cursor.isNull(_cursorIndexOfSpiceLevel) && _cursor.isNull(_cursorIndexOfAllergens))) {
            _tmpRecipe = new Recipe();
            _tmpRecipe.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpRecipe.title = null;
            } else {
              _tmpRecipe.title = _cursor.getString(_cursorIndexOfTitle);
            }
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpRecipe.imagePath = null;
            } else {
              _tmpRecipe.imagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpRecipe.isFavorite = _tmp != 0;
            if (_cursor.isNull(_cursorIndexOfCookingTime)) {
              _tmpRecipe.cookingTime = null;
            } else {
              _tmpRecipe.cookingTime = _cursor.getString(_cursorIndexOfCookingTime);
            }
            if (_cursor.isNull(_cursorIndexOfSpiceLevel)) {
              _tmpRecipe.spiceLevel = null;
            } else {
              _tmpRecipe.spiceLevel = _cursor.getString(_cursorIndexOfSpiceLevel);
            }
            if (_cursor.isNull(_cursorIndexOfAllergens)) {
              _tmpRecipe.allergens = null;
            } else {
              _tmpRecipe.allergens = _cursor.getString(_cursorIndexOfAllergens);
            }
          } else {
            _tmpRecipe = null;
          }
          final ArrayList<Ingredient> _tmpIngredientsCollection;
          final Long _tmpKey_2;
          if (_cursor.isNull(_cursorIndexOfId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _cursor.getLong(_cursorIndexOfId);
          }
          if (_tmpKey_2 != null) {
            _tmpIngredientsCollection = _collectionIngredients.get(_tmpKey_2);
          } else {
            _tmpIngredientsCollection = new ArrayList<Ingredient>();
          }
          final ArrayList<Instruction> _tmpInstructionsCollection;
          final Long _tmpKey_3;
          if (_cursor.isNull(_cursorIndexOfId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _cursor.getLong(_cursorIndexOfId);
          }
          if (_tmpKey_3 != null) {
            _tmpInstructionsCollection = _collectionInstructions.get(_tmpKey_3);
          } else {
            _tmpInstructionsCollection = new ArrayList<Instruction>();
          }
          _result = new RecipeWithDetails();
          _result.recipe = _tmpRecipe;
          _result.ingredients = _tmpIngredientsCollection;
          _result.instructions = _tmpInstructionsCollection;
        } else {
          _result = null;
        }
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        _cursor.close();
        _statement.release();
      }
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int getFavoritesCount() {
    final String _sql = "SELECT COUNT(*) FROM recipes WHERE is_favorite = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Recipe> getFavoriteRecipes() {
    final String _sql = "SELECT * FROM recipes WHERE is_favorite = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "image_path");
      final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "is_favorite");
      final int _cursorIndexOfCookingTime = CursorUtil.getColumnIndexOrThrow(_cursor, "cooking_time");
      final int _cursorIndexOfSpiceLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "spice_level");
      final int _cursorIndexOfAllergens = CursorUtil.getColumnIndexOrThrow(_cursor, "allergens");
      final List<Recipe> _result = new ArrayList<Recipe>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Recipe _item;
        _item = new Recipe();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfImagePath)) {
          _item.imagePath = null;
        } else {
          _item.imagePath = _cursor.getString(_cursorIndexOfImagePath);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
        _item.isFavorite = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfCookingTime)) {
          _item.cookingTime = null;
        } else {
          _item.cookingTime = _cursor.getString(_cursorIndexOfCookingTime);
        }
        if (_cursor.isNull(_cursorIndexOfSpiceLevel)) {
          _item.spiceLevel = null;
        } else {
          _item.spiceLevel = _cursor.getString(_cursorIndexOfSpiceLevel);
        }
        if (_cursor.isNull(_cursorIndexOfAllergens)) {
          _item.allergens = null;
        } else {
          _item.allergens = _cursor.getString(_cursorIndexOfAllergens);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Allergen> getAllergensByRecipeId(final long recipeId) {
    final String _sql = "SELECT allergens.* FROM allergens INNER JOIN recipe_allergen_junction ON allergens.id = recipe_allergen_junction.allergenId WHERE recipe_allergen_junction.recipeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, recipeId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfIsCommonAllergen = CursorUtil.getColumnIndexOrThrow(_cursor, "is_common_allergen");
      final List<Allergen> _result = new ArrayList<Allergen>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Allergen _item;
        _item = new Allergen();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsCommonAllergen);
        _item.isCommonAllergen = _tmp != 0;
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipingredientsAscomExampleModelsIngredient(
      @NonNull final LongSparseArray<ArrayList<Ingredient>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipingredientsAscomExampleModelsIngredient(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`recipe_id`,`ingredient_text`,`is_mandatory` FROM `ingredients` WHERE `recipe_id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "recipe_id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfRecipeId = 1;
      final int _cursorIndexOfIngredientText = 2;
      final int _cursorIndexOfIsMandatory = 3;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Ingredient> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Ingredient _item_1;
          _item_1 = new Ingredient();
          _item_1.id = _cursor.getLong(_cursorIndexOfId);
          _item_1.recipeId = _cursor.getLong(_cursorIndexOfRecipeId);
          if (_cursor.isNull(_cursorIndexOfIngredientText)) {
            _item_1.ingredientText = null;
          } else {
            _item_1.ingredientText = _cursor.getString(_cursorIndexOfIngredientText);
          }
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfIsMandatory);
          _item_1.isMandatory = _tmp != 0;
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }

  private void __fetchRelationshipinstructionsAscomExampleModelsInstruction(
      @NonNull final LongSparseArray<ArrayList<Instruction>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipinstructionsAscomExampleModelsInstruction(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`recipe_id`,`step_number`,`instruction_text` FROM `instructions` WHERE `recipe_id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "recipe_id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfRecipeId = 1;
      final int _cursorIndexOfStepNumber = 2;
      final int _cursorIndexOfInstructionText = 3;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Instruction> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Instruction _item_1;
          _item_1 = new Instruction();
          _item_1.id = _cursor.getLong(_cursorIndexOfId);
          _item_1.recipeId = _cursor.getLong(_cursorIndexOfRecipeId);
          _item_1.stepNumber = _cursor.getInt(_cursorIndexOfStepNumber);
          if (_cursor.isNull(_cursorIndexOfInstructionText)) {
            _item_1.instructionText = null;
          } else {
            _item_1.instructionText = _cursor.getString(_cursorIndexOfInstructionText);
          }
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
