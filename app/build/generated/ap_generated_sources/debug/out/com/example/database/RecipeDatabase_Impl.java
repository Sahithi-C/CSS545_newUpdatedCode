package com.example.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.dao.RecipeDao;
import com.example.dao.RecipeDao_Impl;
import com.example.data.AllergenDao;
import com.example.data.AllergenDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class RecipeDatabase_Impl extends RecipeDatabase {
  private volatile RecipeDao _recipeDao;

  private volatile AllergenDao _allergenDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `recipes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `image_path` TEXT, `is_favorite` INTEGER NOT NULL, `cooking_time` TEXT, `spice_level` TEXT, `allergens` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ingredients` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipe_id` INTEGER NOT NULL, `ingredient_text` TEXT, `is_mandatory` INTEGER NOT NULL, FOREIGN KEY(`recipe_id`) REFERENCES `recipes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `instructions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipe_id` INTEGER NOT NULL, `step_number` INTEGER NOT NULL, `instruction_text` TEXT, FOREIGN KEY(`recipe_id`) REFERENCES `recipes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `allergens` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `description` TEXT, `is_common_allergen` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recipe_allergen_junction` (`recipeId` INTEGER NOT NULL, `allergenId` INTEGER NOT NULL, PRIMARY KEY(`recipeId`, `allergenId`), FOREIGN KEY(`recipeId`) REFERENCES `recipes`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`allergenId`) REFERENCES `allergens`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_allergen_junction_recipeId` ON `recipe_allergen_junction` (`recipeId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_allergen_junction_allergenId` ON `recipe_allergen_junction` (`allergenId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c73454938f6fd0b7f028139a09a8634c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `recipes`");
        db.execSQL("DROP TABLE IF EXISTS `ingredients`");
        db.execSQL("DROP TABLE IF EXISTS `instructions`");
        db.execSQL("DROP TABLE IF EXISTS `allergens`");
        db.execSQL("DROP TABLE IF EXISTS `recipe_allergen_junction`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRecipes = new HashMap<String, TableInfo.Column>(7);
        _columnsRecipes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipes.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipes.put("image_path", new TableInfo.Column("image_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipes.put("is_favorite", new TableInfo.Column("is_favorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipes.put("cooking_time", new TableInfo.Column("cooking_time", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipes.put("spice_level", new TableInfo.Column("spice_level", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipes.put("allergens", new TableInfo.Column("allergens", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecipes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecipes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecipes = new TableInfo("recipes", _columnsRecipes, _foreignKeysRecipes, _indicesRecipes);
        final TableInfo _existingRecipes = TableInfo.read(db, "recipes");
        if (!_infoRecipes.equals(_existingRecipes)) {
          return new RoomOpenHelper.ValidationResult(false, "recipes(com.example.models.Recipe).\n"
                  + " Expected:\n" + _infoRecipes + "\n"
                  + " Found:\n" + _existingRecipes);
        }
        final HashMap<String, TableInfo.Column> _columnsIngredients = new HashMap<String, TableInfo.Column>(4);
        _columnsIngredients.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("recipe_id", new TableInfo.Column("recipe_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("ingredient_text", new TableInfo.Column("ingredient_text", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredients.put("is_mandatory", new TableInfo.Column("is_mandatory", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIngredients = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysIngredients.add(new TableInfo.ForeignKey("recipes", "CASCADE", "NO ACTION", Arrays.asList("recipe_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIngredients = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIngredients = new TableInfo("ingredients", _columnsIngredients, _foreignKeysIngredients, _indicesIngredients);
        final TableInfo _existingIngredients = TableInfo.read(db, "ingredients");
        if (!_infoIngredients.equals(_existingIngredients)) {
          return new RoomOpenHelper.ValidationResult(false, "ingredients(com.example.models.Ingredient).\n"
                  + " Expected:\n" + _infoIngredients + "\n"
                  + " Found:\n" + _existingIngredients);
        }
        final HashMap<String, TableInfo.Column> _columnsInstructions = new HashMap<String, TableInfo.Column>(4);
        _columnsInstructions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstructions.put("recipe_id", new TableInfo.Column("recipe_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstructions.put("step_number", new TableInfo.Column("step_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstructions.put("instruction_text", new TableInfo.Column("instruction_text", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInstructions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysInstructions.add(new TableInfo.ForeignKey("recipes", "CASCADE", "NO ACTION", Arrays.asList("recipe_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesInstructions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoInstructions = new TableInfo("instructions", _columnsInstructions, _foreignKeysInstructions, _indicesInstructions);
        final TableInfo _existingInstructions = TableInfo.read(db, "instructions");
        if (!_infoInstructions.equals(_existingInstructions)) {
          return new RoomOpenHelper.ValidationResult(false, "instructions(com.example.models.Instruction).\n"
                  + " Expected:\n" + _infoInstructions + "\n"
                  + " Found:\n" + _existingInstructions);
        }
        final HashMap<String, TableInfo.Column> _columnsAllergens = new HashMap<String, TableInfo.Column>(4);
        _columnsAllergens.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllergens.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllergens.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAllergens.put("is_common_allergen", new TableInfo.Column("is_common_allergen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAllergens = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAllergens = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAllergens = new TableInfo("allergens", _columnsAllergens, _foreignKeysAllergens, _indicesAllergens);
        final TableInfo _existingAllergens = TableInfo.read(db, "allergens");
        if (!_infoAllergens.equals(_existingAllergens)) {
          return new RoomOpenHelper.ValidationResult(false, "allergens(com.example.models.Allergen).\n"
                  + " Expected:\n" + _infoAllergens + "\n"
                  + " Found:\n" + _existingAllergens);
        }
        final HashMap<String, TableInfo.Column> _columnsRecipeAllergenJunction = new HashMap<String, TableInfo.Column>(2);
        _columnsRecipeAllergenJunction.put("recipeId", new TableInfo.Column("recipeId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipeAllergenJunction.put("allergenId", new TableInfo.Column("allergenId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecipeAllergenJunction = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysRecipeAllergenJunction.add(new TableInfo.ForeignKey("recipes", "NO ACTION", "NO ACTION", Arrays.asList("recipeId"), Arrays.asList("id")));
        _foreignKeysRecipeAllergenJunction.add(new TableInfo.ForeignKey("allergens", "NO ACTION", "NO ACTION", Arrays.asList("allergenId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesRecipeAllergenJunction = new HashSet<TableInfo.Index>(2);
        _indicesRecipeAllergenJunction.add(new TableInfo.Index("index_recipe_allergen_junction_recipeId", false, Arrays.asList("recipeId"), Arrays.asList("ASC")));
        _indicesRecipeAllergenJunction.add(new TableInfo.Index("index_recipe_allergen_junction_allergenId", false, Arrays.asList("allergenId"), Arrays.asList("ASC")));
        final TableInfo _infoRecipeAllergenJunction = new TableInfo("recipe_allergen_junction", _columnsRecipeAllergenJunction, _foreignKeysRecipeAllergenJunction, _indicesRecipeAllergenJunction);
        final TableInfo _existingRecipeAllergenJunction = TableInfo.read(db, "recipe_allergen_junction");
        if (!_infoRecipeAllergenJunction.equals(_existingRecipeAllergenJunction)) {
          return new RoomOpenHelper.ValidationResult(false, "recipe_allergen_junction(com.example.models.RecipeAllergenCrossRef).\n"
                  + " Expected:\n" + _infoRecipeAllergenJunction + "\n"
                  + " Found:\n" + _existingRecipeAllergenJunction);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c73454938f6fd0b7f028139a09a8634c", "5862eb1efc1ba45f66611df152e2415c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "recipes","ingredients","instructions","allergens","recipe_allergen_junction");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `recipes`");
      _db.execSQL("DELETE FROM `ingredients`");
      _db.execSQL("DELETE FROM `instructions`");
      _db.execSQL("DELETE FROM `recipe_allergen_junction`");
      _db.execSQL("DELETE FROM `allergens`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RecipeDao.class, RecipeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AllergenDao.class, AllergenDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RecipeDao recipeDao() {
    if (_recipeDao != null) {
      return _recipeDao;
    } else {
      synchronized(this) {
        if(_recipeDao == null) {
          _recipeDao = new RecipeDao_Impl(this);
        }
        return _recipeDao;
      }
    }
  }

  @Override
  public AllergenDao allergenDao() {
    if (_allergenDao != null) {
      return _allergenDao;
    } else {
      synchronized(this) {
        if(_allergenDao == null) {
          _allergenDao = new AllergenDao_Impl(this);
        }
        return _allergenDao;
      }
    }
  }
}
