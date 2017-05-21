package com.benhamo.avi.mdb.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by avi on 21/05/2017.
 */

public class MovieTrailerTable {
    /*
    "id": 321612,
  "results": [
    {
      "id": "589219bfc3a368096a009a41",
      "iso_639_1": "en",
      "iso_3166_1": "US",
      "key": "tWapqpCEO7Y",
      "name": "Belle Motion Poster",
      "site": "YouTube",
      "size": 720,
      "type": "Clip"
    },*/

    // Database table
    public static final String TABLE_MOVIES_TRAILER = "movies_trailer";
    public static final String COLUMN__ID = "_id";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_MOVIE_ID = "movie_id";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MOVIES_TRAILER
            + "( "+COLUMN__ID+" integer primary key autoincrement ,"
            + COLUMN_ID + " integer  , "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_KEY + " text not null, "
            + COLUMN_MOVIE_ID + " number "

            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(MovieInfoTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES_TRAILER);
        onCreate(database);
    }
}
