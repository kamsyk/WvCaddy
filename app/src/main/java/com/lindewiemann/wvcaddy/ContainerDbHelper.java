package com.lindewiemann.wvcaddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContainerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LwWvCaddy.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LwWvCaddyDbDict.WvCaddyEntry.TABLE_NAME + " ("
                    + LwWvCaddyDbDict.WvCaddyEntry._ID + " INTEGER PRIMARY KEY,"
                    + LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SHIFT + " INTEGER,"
                    + LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_CODE + " TEXT,"
                    + LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SUBCODE + " TEXT,"
                    + LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_PCS + " INTEGER,"
                    + LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE + " TEXT,"
                    + LwWvCaddyDbDict.WvCaddyEntry.COLUMN_NAME_USER_CODE + " INTEGER"
                    + ")";

    public ContainerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DbUpgrade(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void DbUpgrade(SQLiteDatabase db) {

    }

    private boolean IsExistsColumnInTable(SQLiteDatabase lwVwDb, String lwWvTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = lwVwDb.rawQuery("SELECT * FROM " + lwWvTable + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            //Log.d("... - existsColumnInTable", "When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

}
