package com.lindewiemann.wvcaddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContainerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "LwWvCaddy.db";

    private static final String SQL_CREATE_ENTRIES_CADDY =
            "CREATE TABLE " + LwVwCaddyDbDict.WvCaddyEntry.TABLE_NAME + " ("
                    + LwVwCaddyDbDict.WvCaddyEntry._ID + " INTEGER PRIMARY KEY,"
                    + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SHIFT + " INTEGER,"
                    + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_CODE + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_SUBCODE + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_PCS + " INTEGER,"
                    + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_DATE + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_USER_CODE + " INTEGER,"
                    + LwVwCaddyDbDict.WvCaddyEntry.COLUMN_NAME_LR + " INTEGER"
                    + ")";

    private static final String SQL_CREATE_ENTRIES_CADDY_SUBCODE =
            "CREATE TABLE " + LwVwCaddyDbDict.WvCaddySubcodeEntry.TABLE_NAME + " ("
                    + LwVwCaddyDbDict.WvCaddySubcodeEntry._ID + " INTEGER PRIMARY KEY,"
                    + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_SHIFT + " INTEGER,"
                    + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_SUBCODE + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_PCS + " INTEGER,"
                    + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_DATE + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_USER_CODE + " INTEGER,"
                    + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_LR + " INTEGER"
                    + ")";

    private static final String SQL_CREATE_ENTRIES_CADDY_SETTINGS =
            "CREATE TABLE " + LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME + " ("
                    + LwVwCaddyDbDict.WvCaddySettings._ID + " INTEGER PRIMARY KEY,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_PASSWORD + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_GMAILPASSWORD  + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_HOUR + " INTEGER,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE + " TEXT,"
                    + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_AUTO_MAIL_STATUS + " TEXT"
                    + ")";

    public ContainerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES_CADDY);
        db.execSQL(SQL_CREATE_ENTRIES_CADDY_SUBCODE);
        db.execSQL(SQL_CREATE_ENTRIES_CADDY_SETTINGS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DbUpgrade(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void DbUpgrade(SQLiteDatabase db) {
        /*if(DATABASE_VERSION < 3) {
            String sql =
                    "CREATE TABLE IF NOT EXISTS " + LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME + " ("
                            + LwVwCaddyDbDict.WvCaddySettings._ID + " INTEGER PRIMARY KEY,"
                            + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_SENDER + " TEXT,"
                            + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_RECIPIENTS + " TEXT,"
                            + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_API_KEY + " TEXT,"
                            + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAILJET_SECRET_KEY + " TEXT,"
                            + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_PASSWORD + " TEXT"
                            + ")";
            db.execSQL(sql);
        }

        if(DATABASE_VERSION < 4) {
            String sql = "ALTER TABLE " + LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME +
                    "  ADD " + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_GMAILPASSWORD  + " TEXT";

            db.execSQL(sql);
        }*/

       /*String sql =
                    "CREATE TABLE IF NOT EXISTS " + LwVwCaddyDbDict.WvCaddySubcodeEntry.TABLE_NAME + " ("
                            + LwVwCaddyDbDict.WvCaddySubcodeEntry._ID + " INTEGER PRIMARY KEY,"
                            + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_SHIFT + " INTEGER,"
                            + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_SUBCODE + " TEXT,"
                            + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_PCS + " INTEGER,"
                            + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_DATE + " TEXT,"
                            + LwVwCaddyDbDict.WvCaddySubcodeEntry.COLUMN_NAME_USER_CODE + " INTEGER"
                            + ")";
            db.execSQL(sql);
        */

        /*
        //if(DATABASE_VERSION < 5) {
            if(!IsExistsColumnInTable(db, LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_HOUR)) {
                String sql = "ALTER TABLE " + LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME +
                        "  ADD " + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_HOUR + " INTEGER";

                db.execSQL(sql);
            }
        //}

        //if(DATABASE_VERSION < 7) {
            if(!IsExistsColumnInTable(db, LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE)) {
                String sql = "ALTER TABLE " + LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME +
                        "  ADD " + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_MAIL_DATE + " TEXT";

                db.execSQL(sql);
            }
        if(!IsExistsColumnInTable(db, LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME, LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_AUTO_MAIL_STATUS)) {
            String sql = "ALTER TABLE " + LwVwCaddyDbDict.WvCaddySettings.TABLE_NAME +
                    "  ADD " + LwVwCaddyDbDict.WvCaddySettings.COLUMN_NAME_AUTO_MAIL_STATUS + " TEXT";

            db.execSQL(sql);
        }
        //}*/
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

    private boolean IsExistsTable(SQLiteDatabase lwVwDb, String lwWvTableName) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = lwVwDb.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='{" + lwWvTableName + "}';", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getCount() > 0)
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
