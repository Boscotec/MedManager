package com.boscotec.medmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.boscotec.medmanager.adapter.MedListAdapter;
import com.boscotec.medmanager.custom.RecyclerItemDivider;
import com.boscotec.medmanager.database.DbHelper;
import com.boscotec.medmanager.interfaces.RecyclerItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Created by Johnbosco on 21-Mar-18.
 */
public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
       LoaderManager.LoaderCallbacks<List<RecyclerItem>>, MedListAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MedListAdapter adapter;
    //  private GoogleSignInAccount account = null;
    private static final String SHARE_TAG = "MedManager";

    private FloatingSearchView mSearchView;
    private static final int LOADER_ID = 1;
    //private LoaderManager loaderManager;
    //private Loader<List<RecyclerItem>> databaseLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     //   if(getIntent().hasExtra("account")){
     //     account = getIntent().getParcelableExtra("account");
     //   }

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.appbar);

        mAppBar.addOnOffsetChangedListener(this);
        setupSearchBar();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), MedicationAddActivity.class));
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.med_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerItemDivider(this));
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(1000);
        animator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(animator);
       // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(false);
        adapter = new MedListAdapter(this, this);
        mRecyclerView.setAdapter(adapter);

       // loaderManager = getSupportLoaderManager();
       // databaseLoader = loaderManager.getLoader(LOADER_ID);
       // if(databaseLoader == null){ loaderManager.initLoader(LOADER_ID, null, this);}
       // else { loaderManager.restartLoader(LOADER_ID, null, this);}

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                DbHelper db = new DbHelper(getBaseContext());
                db.delete(id);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return viewHolder instanceof MedListAdapter.ViewHolderMonth ? 0 : super.getSwipeDirs(recyclerView, viewHolder);
            }

        }).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onResume(){
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        super.onResume();
    }

    @Override
    public void onPause() { super.onPause(); }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSearchView.setTranslationY(verticalOffset);
    }

    private void setupSearchBar() {

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                adapter.getFilter().filter(newQuery);
            }
        });

        //handle menu clicks the same way as you would in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit_profile: editProfile(); break;
                    case R.id.action_share: item.setIntent(createShareForecastIntent()); break;
                    case R.id.action_logout: signOut(); break;
                }
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                MainActivity.super.onBackPressed();
            }
        });

    }

    private void editProfile(){
        startActivity(new Intent(getBaseContext(), ProfileActivity.class));
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(SHARE_TAG)
                .getIntent();
        return shareIntent;
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getBaseContext(), "Log out successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void revokeAccess() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getBaseContext(), "Revoking access successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    public void onItemClick(RecyclerItem item) {
    }

    @NonNull
    @Override
    public Loader<List<RecyclerItem>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<List<RecyclerItem>>(this) {
            List<RecyclerItem> mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<RecyclerItem> loadInBackground() {
                try {
                    DbHelper db = new DbHelper(getContext());
                    List<RecyclerItem> items = db.read();
                    return items;
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(List<RecyclerItem> data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<RecyclerItem>> loader, List<RecyclerItem> data) {
        adapter.swapItems(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<RecyclerItem>> loader) {
        adapter.swapItems(null);
    }

}
