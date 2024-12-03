package com.example.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.models.Allergen;
import com.example.models.RecipeAllergenCrossRef;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AllergenDao_Impl implements AllergenDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Allergen> __insertionAdapterOfAllergen;

  private final EntityInsertionAdapter<RecipeAllergenCrossRef> __insertionAdapterOfRecipeAllergenCrossRef;

  public AllergenDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
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
  public LiveData<List<Allergen>> getAllergensByRecipeId(final long recipeId) {
    final String _sql = "SELECT a.* FROM allergens a JOIN recipe_allergen_junction raj ON a.id = raj.allergenId WHERE raj.recipeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, recipeId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"allergens",
        "recipe_allergen_junction"}, true, new Callable<List<Allergen>>() {
      @Override
      @Nullable
      public List<Allergen> call() throws Exception {
        __db.beginTransaction();
        try {
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
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Allergen>> getAllAllergens() {
    final String _sql = "SELECT * FROM allergens";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"allergens"}, false, new Callable<List<Allergen>>() {
      @Override
      @Nullable
      public List<Allergen> call() throws Exception {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
