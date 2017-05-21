package com.benhamo.avi.mdb.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.benhamo.avi.mdb.db.MovieInfoTable;
import com.benhamo.avi.mdb.db.MovieTrailerTable;

/**
 * Created by avi on 21/05/2017.
 */

public class MdbContract {
    /**
     * The authority of the lentitems provider.
     */
    public static final String AUTHORITY =
            "com.benhamo.avi.mdb.contentprovider";
    /**
     * The content URI for the top-level
     * lentitems authority.
     */
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);


    public static final class MovieInfo implements BaseColumns {

        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        MdbContract.CONTENT_URI,
                        "movies");
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/movieInfo";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/movieInfo";
        /**
         * A projection of all columns
         * in the items table.
         */
        public static final String[] PROJECTION_ALL =
                {MovieInfoTable.COLUMN_ID,
                        MovieInfoTable.COLUMN_TITLE,
                        MovieInfoTable.COLUMN_POSTER,
                        MovieInfoTable.COLUMN_VOTE_AVERAGE,
                        MovieInfoTable.COLUMN_RELEASE_DATE,
                        MovieInfoTable.COLUMN_OVERVIEW
                };

        /**
         * The default sort order for
         * queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT =
                MovieInfoTable.COLUMN_VOTE_AVERAGE + " desc";

    }

    public static final class MovieTrailer implements BaseColumns {
        /**
         * The content URI for this table.
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        MdbContract.CONTENT_URI,
                        "trailer");
        /**
         * The mime type of a directory of items.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/movieTrailer";
        /**
         * The mime type of a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/movieTrailer";
        /**
         * A projection of all columns
         * in the items table.
         */
        public static final String[] PROJECTION_ALL =
                {MovieTrailerTable.COLUMN_ID,
                        MovieTrailerTable.COLUMN_TITLE,
                        MovieTrailerTable.COLUMN_KEY,
                        MovieTrailerTable.COLUMN_MOVIE_ID

                };

        /**
         * The default sort order for
         * queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT =
                MovieTrailerTable.COLUMN_ID + " desc";
    }

}
