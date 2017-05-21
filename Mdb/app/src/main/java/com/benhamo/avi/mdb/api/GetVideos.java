package com.benhamo.avi.mdb.api;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.benhamo.avi.mdb.contentprovider.MdbContract;
import com.benhamo.avi.mdb.db.MovieTrailerTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by avi on 21/05/2017.
 */

public class GetVideos extends GetRequest {


    public boolean getRequest(Context context,long movieId){
        String json = getJSON(Constants.BASE_URL+"movie/"+movieId+"/videos"+Constants.API_KEY_PRAM+"&language=en-US",10*1000);
        if(!TextUtils.isEmpty(json)){
            try {
                JSONObject obj = new JSONObject(json);
                JSONArray results = obj.optJSONArray("results");
                if(results == null || results.length() ==0) return true;

                ContentResolver resolver = context.getContentResolver();
                ContentValues values [] = new ContentValues[results.length()];


                for(int i=0;i<results.length();i++) {
                    JSONObject m = results.optJSONObject(i);
                    if(m != null) {
                        String key = m.getString("key");
                        String title = m.getString("name");
                        String id = m.getString("id");
                        ContentValues v = new ContentValues();
                        v.put(MovieTrailerTable.COLUMN_ID,id);
                        v.put(MovieTrailerTable.COLUMN_TITLE,title);
                        v.put(MovieTrailerTable.COLUMN_KEY,key);
                        v.put(MovieTrailerTable.COLUMN_MOVIE_ID,movieId);
                        values[i]=v;
                    }
                }
                resolver.bulkInsert(MdbContract.MovieTrailer.CONTENT_URI,values);

                /*

                 public static final String TABLE_MOVIES_TRAILER = "movies_trailer";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_MOVIE_ID = "movie_id";

                {
      "id": "58cfcb49c3a36811ce004b82",
      "iso_639_1": "en",
      "iso_3166_1": "US",
      "key": "HHnKnVUySJU",
      "name": "Official Trailer #1",
      "site": "YouTube",
      "size": 1080,
      "type": "Trailer"
    }
                 */
                return true;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
public void getVideos(final Context context, final long movieId){
    AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            getRequest(context,movieId);
            return null;
        }
    } ;
    task.execute();
}
}
