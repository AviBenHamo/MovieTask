package com.benhamo.avi.mdb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benhamo.avi.mdb.adapters.CursorRecyclerViewAdapter;
import com.benhamo.avi.mdb.api.GetVideos;
import com.benhamo.avi.mdb.contentprovider.MdbContract;
import com.benhamo.avi.mdb.db.MovieInfoTable;
import com.benhamo.avi.mdb.db.MovieTrailerTable;
import com.bumptech.glide.Glide;


public class MovieDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_MOVIE_ID = "movieId";
    RecyclerView recyclerView;

    private long movieId;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE_ID)) {
            movieId = getArguments().getLong(ARG_MOVIE_ID);

            Activity activity = this.getActivity();


        }
    }

    private TextView title;
    private TextView vote;
    private TextView date;
    private TextView overview;
    private ImageView poster;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fregment_movie_detail, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_list);
        title = (TextView) rootView.findViewById(R.id.title);
        vote = (TextView) rootView.findViewById(R.id.vote);
        date = (TextView) rootView.findViewById(R.id.date);
        overview = (TextView) rootView.findViewById(R.id.overview);
        poster = (ImageView) rootView.findViewById(R.id.poster);

        setupRecyclerView(recyclerView);

        return rootView;
    }

    TrailerAdapter adapter;

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TrailerAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);

        getActivity().getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        getActivity().getSupportLoaderManager().restartLoader(TRAILER_LOADER, null, this);
    }

    public class TrailerAdapter extends CursorRecyclerViewAdapter {

        public TrailerAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.movie_detail, parent, false);
            return new TrailerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
            TrailerViewHolder holder = (TrailerViewHolder) viewHolder;
            holder.onBindViewHolder(cursor);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView title;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            title = (TextView) itemView.findViewById(R.id.title);

        }

        public void onBindViewHolder(Cursor cursor) {

            final String key = cursor.getString(CI_KEY);
            final String title = cursor.getString(CI_TRAILER_TITLE);
            this.title.setText(title);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)));
                }
            });
        }

    }//End holder

    private static final int
            CI_ID = 0,
            CI_TITLE = 1,
            CI_POSTER = 2,
            CI_VOTE_AVERAGE = 3,
            CI_RELEASE_DATE = 4,
            CI_OVERVIEW = 5;

    private static final int
            CI_TRAILER_ID = 0,
            CI_TRAILER_TITLE = 1,
            CI_KEY = 2;

    private static final int
            MOVIE_LOADER = 0,
            TRAILER_LOADER = 1;

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == MOVIE_LOADER) {
            String[] projection = {
                    MovieInfoTable.COLUMN_ID,
                    MovieInfoTable.COLUMN_TITLE,
                    MovieInfoTable.COLUMN_POSTER,
                    MovieInfoTable.COLUMN_VOTE_AVERAGE,
                    MovieInfoTable.COLUMN_RELEASE_DATE,
                    MovieInfoTable.COLUMN_OVERVIEW};
            String selection = MovieInfoTable.COLUMN_ID + " = ? ";
            String[] selectionArg = {String.valueOf(movieId)};
            CursorLoader cursorLoader = new CursorLoader(getActivity(),
                    MdbContract.MovieInfo.CONTENT_URI, projection, selection, selectionArg, null);
            return cursorLoader;
        } else {

            String[] projection = {
                    MovieTrailerTable.COLUMN__ID,
                    MovieTrailerTable.COLUMN_TITLE,
                    MovieTrailerTable.COLUMN_KEY};
            String selection = MovieTrailerTable.COLUMN_MOVIE_ID + " = ? ";
            String[] selectionArg = {String.valueOf(movieId)};
            CursorLoader cursorLoader = new CursorLoader(getActivity(),
                    MdbContract.MovieTrailer.CONTENT_URI, projection, selection, selectionArg, null);
            return cursorLoader;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == MOVIE_LOADER) movieLoadFinished(data);
        else trailerLoadFinished(data);
    }

    private void movieLoadFinished(Cursor data) {
        if (data.moveToFirst()) {
            String title = data.getString(CI_TITLE);
            String poster = data.getString(CI_POSTER);
            String vote = data.getString(CI_VOTE_AVERAGE);
            String date = data.getString(CI_RELEASE_DATE);
            String overview = data.getString(CI_OVERVIEW);

            this.title.setText(title);
            this.vote.setText(vote + "/10");
            this.date.setText(date);
            this.overview.setText(overview);
            String path = "http://image.tmdb.org/t/p/w185" + poster;

            Glide.with(this)
                    .load(path)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(this.poster);


        }

    }

    private void trailerLoadFinished(Cursor data) {

        if (data.getCount() == 0)
            new GetVideos().getVideos(getActivity(), movieId);
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == TRAILER_LOADER)
            adapter.swapCursor(null);
    }

}
