package com.example.android.homecookinrecipes;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.homecookinrecipes.data.FetchRecipeData;
import com.example.android.homecookinrecipes.data.Recipe;
import com.example.android.homecookinrecipes.data.RecipeRecyclerAdapter;
import com.example.android.homecookinrecipes.utility.Util;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private String mSortOrder;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mPage, mLastPosition;
    private boolean mLoading;
    private Recipe[] mAllRecipes;

    private static final int MAX_REQUEST_ALLOWED = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addOnScrollListener(new EndlessScrollList());
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSortOrder = "r";
        mPage = 1;

        loadRecipeData(mSortOrder, String.valueOf(mPage));
    }

    private void loadRecipeData(String... params) {
        FetchRecipeData recipeTask = new FetchRecipeData(new FetchRecipeData.AsyncResponse() {
            @Override
            public void processResult(Recipe[] result) {
                refreshView(result);
            }
        });
        recipeTask.execute(params);
    }

    private void refreshView(Recipe[] result) {
        mAllRecipes = Util.addRecipeArrays(mAllRecipes, result);
        RecipeRecyclerAdapter adapter = new RecipeRecyclerAdapter(MainActivity.this, mAllRecipes);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.invalidate();
        mRecyclerView.scrollToPosition(mLastPosition);
        mLoading = true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        // getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class EndlessScrollList extends RecyclerView.OnScrollListener {
        int pastVisibleItems, visibleItems, totalItems;
        boolean loading = true;

        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            if (dy > 0) {
                visibleItems = mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                int[] firstVisiblePositions = new int[2];
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisiblePositions)[0];

                Log.d("VisibleItems=", "" + visibleItems + " pastVisible=" + pastVisibleItems + " total=" + totalItems);

                if (mLoading && mPage <= MAX_REQUEST_ALLOWED) {
                    if ((visibleItems + pastVisibleItems) >= totalItems-10) {
                        mLoading = false;
                        mPage++;
                        mLastPosition = pastVisibleItems;
                        loadRecipeData(mSortOrder, String.valueOf(mPage));
                    }
                }

            }
        }
    }
}
