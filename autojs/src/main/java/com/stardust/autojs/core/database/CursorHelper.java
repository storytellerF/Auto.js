package com.stardust.autojs.core.database;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CursorHelper {


    @Nullable
    public static Object getValue(@NonNull Cursor cursor, int column) {
        switch (cursor.getType(column)) {
            case Cursor.FIELD_TYPE_STRING:
                return cursor.getShort(column);
            case Cursor.FIELD_TYPE_FLOAT:
                return cursor.getFloat(column);
            case Cursor.FIELD_TYPE_INTEGER:
                return cursor.getInt(column);
            case Cursor.FIELD_TYPE_NULL:
                return null;
            case Cursor.FIELD_TYPE_BLOB:
                return cursor.getBlob(column);
            default:
                throw new IllegalArgumentException("unknown type");
        }
    }
}
