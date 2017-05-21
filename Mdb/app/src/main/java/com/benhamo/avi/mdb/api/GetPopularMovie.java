package com.benhamo.avi.mdb.api;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.benhamo.avi.mdb.contentprovider.MdbContract;
import com.benhamo.avi.mdb.db.MovieInfoTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by avi on 21/05/2017.
 */

public class GetPopularMovie extends GetRequest {
    public static final String
            API = "movie/popular";

    public boolean getRequest(Context context) {
        int page = 0;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        long now = System.currentTimeMillis();
        long lastRefresh = pref.getLong("lastRefresh",now);
        if(now - lastRefresh < 1000*60*60*24){
            page = pref.getInt("page",page);
        }
        int total_pages = pref.getInt("total_pages",1);
        page++;
        if(page>total_pages) return false;

        String json = getJSON(Constants.BASE_URL + API + Constants.API_KEY_PRAM+"&language=en-US&page="+page, 10 * 1000);
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject obj = new JSONObject(json);
                 page = obj.optInt("page");
                 total_pages = obj.optInt("total_pages");
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("page",page);
                editor.putInt("total_pages",total_pages);
                editor.commit();
                JSONArray results = obj.optJSONArray("results");
                if (results == null || results.length() == 0) return true;

                ContentResolver resolver = context.getContentResolver();
                ContentValues values[] = new ContentValues[results.length()];


                for (int i = 0; i < results.length(); i++) {
                    JSONObject m = results.optJSONObject(i);
                    if (m != null) {
                        String poster_path = m.getString("poster_path");
                        String overview = m.getString("overview");
                        String title = m.getString("title");
                        long id = m.getLong("id");
                        double vote_average = m.optDouble("vote_average");
                        String release_date = m.getString("release_date");
                        try {
                            release_date = release_date.split("-")[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ContentValues v = new ContentValues();
                        v.put(MovieInfoTable.COLUMN_ID, id);
                        v.put(MovieInfoTable.COLUMN_TITLE, title);
                        v.put(MovieInfoTable.COLUMN_POSTER, poster_path);
                        v.put(MovieInfoTable.COLUMN_VOTE_AVERAGE, vote_average);
                        v.put(MovieInfoTable.COLUMN_RELEASE_DATE, release_date);
                        v.put(MovieInfoTable.COLUMN_OVERVIEW, overview);
                        values[i] = v;
                    }
                }
                resolver.bulkInsert(MdbContract.MovieInfo.CONTENT_URI, values);

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void getMoies(final Context context) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getRequest(context);
                return null;
            }
        };
        task.execute();
    }
}
