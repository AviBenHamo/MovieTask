package com.benhamo.avi.mdb;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.benhamo.avi.mdb.adapters.CursorRecyclerViewAdapter;
import com.benhamo.avi.mdb.api.GetPopularMovie;
import com.benhamo.avi.mdb.contentprovider.MdbContract;
import com.benhamo.avi.mdb.db.MovieInfoTable;
import com.bumptech.glide.Glide;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());



        recyclerView = (RecyclerView) findViewById(R.id.movie_list);
        setupRecyclerView(recyclerView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    MoviesAdapter adapter;
    boolean loadingMore;

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
    int grid = getResources().getInteger(R.integer.grid);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, grid);

        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MoviesAdapter(this, null);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int maxPositions = layoutManager.getItemCount();

                if (lastVisibleItemPosition == maxPositions - 1) {
                    if (loadingMore)
                        return;

                    loadingMore = true;
                    new GetPopularMovie().getMoies(MovieListActivity.this);
                }
            }
        });

        getSupportLoaderManager().restartLoader(0, null, this);


    }

    public class MoviesAdapter extends CursorRecyclerViewAdapter {

        public MoviesAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MovieListActivity.this).inflate(R.layout.movie_image, parent, false);
            return new MovieViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
            MovieViewHolder holder = (MovieViewHolder) viewHolder;
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

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivImagePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ivImagePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);/*
            ivImagePoster.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ivImagePoster.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    final DisplayMetrics display = MyApp.getInstance().getResources().getDisplayMetrics();

                    int width = (int) (display.widthPixels);
                    int height = (int) (display.heightPixels);
                    try {
                        final TypedArray styledAttributes = MyApp.getInstance().getTheme().obtainStyledAttributes(
                                new int[]{android.R.attr.actionBarSize});
                        height -= (int) styledAttributes.getDimension(0, 0);
                        int resource = MyApp.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
                        if (resource > 0) {
                            height -= MyApp.getInstance().getResources().getDimensionPixelSize(resource);
                        }
                    } catch (Exception ex) {

                    }

                    ivImagePoster.getLayoutParams().width = width / 2;
                    ivImagePoster.getLayoutParams().height = height / 2;
                }
            });*/
        }

        public void onBindViewHolder(Cursor cursor) {
            String path = "http://image.tmdb.org/t/p/w185" + cursor.getString(CI_POSTER);
            final long movieId = cursor.getLong(CI_MOVIE_ID);
            try {
                if (path != null) {
                    Glide.with(MovieListActivity.this)
                            .load(path)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(ivImagePoster);
                } else {
                    ivImagePoster.setImageResource(R.mipmap.ic_launcher);
                }
            } catch (Exception ignored) {
            }

            ivImagePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putLong(MovieDetailFragment.ARG_MOVIE_ID, movieId);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_MOVIE_ID, movieId);

                        context.startActivity(intent);
                    }
                }
            });
        }

    }//End holder

    private static final int
            CI_MOVIE_ID = 0,
            CI_POSTER = 1;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MovieInfoTable.COLUMN_ID, MovieInfoTable.COLUMN_POSTER};
        CursorLoader cursorLoader = new CursorLoader(this,
                MdbContract.MovieInfo.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0)
            new GetPopularMovie().getMoies(this);
        loadingMore = false;
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
