package com.benhamo.avi.mdb.db;

/**
 * Created by avi on 21/05/2017.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class MovieDb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todotable.db";
    private static final int DATABASE_VERSION = 1;
    private static final boolean EXTERNAL_DB = true;

    public MovieDb(Context context) {

        super(context, getDbDir(context), null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        MovieInfoTable.onCreate(database);
        MovieTrailerTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        MovieInfoTable.onUpgrade(database, oldVersion, newVersion);
        MovieTrailerTable.onUpgrade(database, oldVersion, newVersion);
    }

    private static String getDbDir(Context context) {
        File storageDir = null;
        if (EXTERNAL_DB) {
            storageDir = context.getExternalFilesDir("AviMdb");
            if (storageDir == null || (!storageDir.exists() && !storageDir.mkdirs())) {
                storageDir = context.getFilesDir();
            }
        } else {
            storageDir = context.getFilesDir();
        }
        return new File(storageDir,DATABASE_NAME).getAbsolutePath();
    }
}