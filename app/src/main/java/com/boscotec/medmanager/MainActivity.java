package com.boscotec.medmanager;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnbosco on 21-Mar-18.
 */

/*
MD5:  87:25:5B:89:76:D2:26:E3:74:1F:4F:B9:4E:3C:C1:EC
        SHA1: 1A:11:45:01:C4:7D:ED:6A:53:FB:91:CB:6E:4A:27:F0:FC:C3:F8:0F
        SHA256: F1:99:57:9E:A2:53:34:73:C0:A1:D3:90:1D:B4:BC:7D:41:14:E6:43:BD:80:67:F7:64:37:2C:23:B3:7D:E9:52

        client id
        728485456501-31e8a5tb090a50376hn7lsis8s1rt0k1.apps.googleusercontent.com

        Client secret
        CW_0opZd1roqtvP5Z9gubC4Z

        keytool -exportcert -list -v -alias <your-key-name> -keystore <path-to-production-keystore>
        keytool -exportcert -list -v -alias androiddebugkey -keystore %USERPROFILE%\.android\debug.keystore

        gradlew lint
*/
/*
  Intent shareIntent = ShareCompat.IntentBuilder.from(this)
          .setType("text/plain")
          .setText(mForecast + FORECAST_SHARE_HASHTAG)
          .getIntent();
          return shareIntent;
          */

// You can also get the user's email address with getEmail,
// the user's Google ID(for client-side use) with getId,
// and an ID token for the user with with getIdToken.
// If you need to pass the currently signed-in user to a backend server,
// send the ID token to your backend server and validate the token on the server.

public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
       LoaderManager.LoaderCallbacks<List<RecyclerItem>>, MedListAdapter.ListItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MedListAdapter adapter;
    private GoogleSignInAccount account = null;

    private FloatingSearchView mSearchView;
    private static final int LOADER_ID = 1;
    private DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().hasExtra("account")){
          account = getIntent().getParcelableExtra("account");
        }

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.appbar);

        mAppBar.addOnOffsetChangedListener(this);
        setupSearchBar();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AddMedicationActivity.class));
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                db = new DbHelper(getBaseContext());
                db.delete(id);
                db.close();
                //getSupportLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return viewHolder instanceof MedListAdapter.ViewHolderMonth ? 0 : super.getSwipeDirs(recyclerView, viewHolder);
            }

        }).attachToRecyclerView(mRecyclerView);


        db = new DbHelper(getBaseContext());
        List<RecyclerItem> info = db.read();
        if(info != null){adapter.swapItems(info);}
        db.close();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        //db.close();
    }

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
                    case R.id.action_edit_profile: break;
                    //case R.id.action_share: item.setIntent(createShareForecastIntent()); break;
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

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getBaseContext(), "Log out successful", Toast.LENGTH_SHORT).show();
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
                    }
                });
    }

    @Override
    public void onItemClick(RecyclerItem item) {
    }

    @NonNull
    @Override
    public Loader<List<RecyclerItem>> onCreateLoader(int id, @Nullable Bundle args) {
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
                    //if(items != null){adapter.swapItems(items);}
                    db.close();
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
