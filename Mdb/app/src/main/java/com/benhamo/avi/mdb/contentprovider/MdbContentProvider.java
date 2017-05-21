package com.benhamo.avi.mdb.contentprovider;

/**
 * Created by avi on 21/05/2017.
 */

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.benhamo.avi.mdb.db.MovieDb;
import com.benhamo.avi.mdb.db.MovieInfoTable;
import com.benhamo.avi.mdb.db.MovieTrailerTable;

import java.util.ArrayList;


public class MdbContentProvider extends ContentProvider {

    // database
    private MovieDb database;
    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();


    // helper constants for use with the UriMatcher
    private static final int MOVIE_LIST = 1;
    private static final int MOVIE_ID = 2;
    private static final int TRAILER_LIST = 3;
    private static final int TRAILER_ID = 4;

    private static final UriMatcher URI_MATCHER;

    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(MdbContract.AUTHORITY,
                "movies",
                MOVIE_LIST);
        URI_MATCHER.addURI(MdbContract.AUTHORITY,
                "movies/#",
                MOVIE_ID);
        URI_MATCHER.addURI(MdbContract.AUTHORITY,
                "trailer",
                TRAILER_LIST);
        URI_MATCHER.addURI(MdbContract.AUTHORITY,
                "trailer/#",
                TRAILER_ID);
    }

    @Override
    public boolean onCreate() {
        database = new MovieDb(getContext());
        return true;
    }



    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = database.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                builder.setTables(MovieInfoTable.TABLE_MOVIES);
                break;
            case MOVIE_ID:
                builder.setTables(MovieInfoTable.TABLE_MOVIES);
                // limit query to one row at most:
                builder.appendWhere(MovieInfoTable.COLUMN_ID + " = " +
                        uri.getLastPathSegment());
                break;
            case TRAILER_LIST:
                builder.setTables(MovieTrailerTable.TABLE_MOVIES_TRAILER);
                break;

            case TRAILER_ID:
                builder.setTables(MovieTrailerTable.TABLE_MOVIES_TRAILER);
                // limit query to one row at most:
                builder.appendWhere(MovieTrailerTable.COLUMN_MOVIE_ID + " = " +
                        uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    MdbContract.CONTENT_URI);
        }
        else {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                return MdbContract.MovieInfo.CONTENT_TYPE;
            case MOVIE_ID:
                return MdbContract.MovieInfo.CONTENT_ITEM_TYPE;
            case TRAILER_LIST:
                return MdbContract.MovieTrailer.CONTENT_TYPE;
            case TRAILER_ID:
                return MdbContract.MovieTrailer.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }
    public Uri insert(Uri uri, ContentValues values) {

        String table = null ;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST :
                table = MovieInfoTable.TABLE_MOVIES;
                break;
            case TRAILER_LIST:
                table = MovieTrailerTable.TABLE_MOVIES_TRAILER;
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for insertion: " + uri);
        }

        if(table != null) {
            SQLiteDatabase db = database.getWritableDatabase();
            long id =
                    db.insertWithOnConflict(
                            table,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);
            return getUriForId(id, uri);
        }
        return null;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes:
                getContext().
                        getContentResolver().
                        notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }
    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = database.getWritableDatabase();
        mIsInBatchMode.set(true);
        // the next line works because SQLiteDatabase
        // uses a thread local SQLiteSession object for
        // all manipulations
        db.beginTransaction();
        try {
            final ContentProviderResult[] retResult = super.applyBatch(operations);
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(MdbContract.CONTENT_URI, null);
            return retResult;
        }
        finally {
            mIsInBatchMode.remove();
            db.endTransaction();
        }
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {


        String table = null ;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST :
                table = MovieInfoTable.TABLE_MOVIES;
                break;
            case TRAILER_LIST:
                table = MovieTrailerTable.TABLE_MOVIES_TRAILER;
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for delete: " + uri);
        }
        if(  table  !=  null) {
            SQLiteDatabase db = database.getWritableDatabase();
            int delCount = 0;

                    delCount = db.delete(
                            table,
                            selection,
                            selectionArgs);

            // notify all listeners of changes:
            if (delCount > 0 && !isInBatchMode()) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return delCount;
        }
        return 0;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {


        String table = null ;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST :
                table = MovieInfoTable.TABLE_MOVIES;
                break;
            case TRAILER_LIST:
                table = MovieTrailerTable.TABLE_MOVIES_TRAILER;
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI for update: " + uri);
        }

        if(table != null) {
            SQLiteDatabase db = database.getWritableDatabase();
            int updateCount = 0;
            updateCount = db.update(
                    table,
                    values,
                    selection,
                    selectionArgs);

            // notify all listeners of changes:
            if (updateCount > 0 && !isInBatchMode()) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return updateCount;
        }
        return 0;
    }
}