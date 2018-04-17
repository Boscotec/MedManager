package com.boscotec.medmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        //signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart(){
        super.onStart();
        /* check if we are already signed in, if so logIn immediately */
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        LogIn(account);
    }

    /**
     *Called when the sign in button is clicked
     *@param v the view that made the call
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    /**
     *Called when googleSignInClient returns a result
     *@param requestCode code used in making the request
     *@param resultCode code for result
     *@param data data returned
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                LogIn(account);
            } catch (ApiException e) {
                Log.w(TAG, "logInResult: failed " + e.getStatusCode());
            }
        }
    }

    /**
     * This method is for Logging in
     *@param account A valid account of user from Google social authentication
     *
     */
    private void LogIn(GoogleSignInAccount account){
        /* check the state of account and don't proceed if null */
        if(account == null) return;

        /* account is valid, move to the next page @MainActivity*/
        Intent login = new Intent (this, MainActivity.class);
        //login.putExtra("account", account);
        startActivity(login);
        finish();
    }
}