package com.benhamo.avi.mdb.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by avi on 21/05/2017.
 */

public class MovieInfoTable {

/*
    {
        "poster_path": "/tWqifoYuwLETmmasnGHO7xBjEtt.jpg",
            "adult": false,
            "overview": "A live-action adaptation of Disney's version of the classic 'Beauty and the Beast' tale of a cursed prince and a beautiful young woman who helps him break the spell.",
            "release_date": "2017-03-16",
            "genre_ids": [
        10402,
                10751,
                14,
                10749
        ],
        "id": 321612,
            "original_title": "Beauty and the Beast",
            "original_language": "en",
            "title": "Beauty and the Beast",
            "backdrop_path": "/7QshG75xKCmClghQDU1ta2BTaja.jpg",
            "popularity": 163.359125,
            "vote_count": 2547,
            "video": false,
            "vote_average": 6.8
    }
*/
        // Database table
        public static final String TABLE_MOVIES = "movies";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";

        // Database creation SQL statement
        private static final String DATABASE_CREATE = "create table "
                + TABLE_MOVIES
                + "("
                + COLUMN_ID + " integer primary key , "
                + COLUMN_TITLE + " text not null, "
                + COLUMN_POSTER + " text not null, "
                + COLUMN_VOTE_AVERAGE + " number, "
                + COLUMN_RELEASE_DATE  + " text , "
                + COLUMN_OVERVIEW + " text "
                + ");";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            Log.w(MovieInfoTable.class.getName(), "Upgrading database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
            onCreate(database);
        }

}
