package com.digital.classes.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.digital.classes.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Arrays;
import java.util.List;

public class NewSpleshActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient signInClient;
    private ActivityResultLauncher<Intent> loginResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_splesh);


        //sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("729036409326-h0samvd4h6vog5msap0ip70rl06ppevs.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //launchMainScreen();
            getCurrentUser();
        }, 3000);

      /*  loginResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                        if (result.getResultCode() == RESULT_OK) {
                            IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            final SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

                            new MaterialAlertDialogBuilder(this)
                                    .setTitle("Select Your Medium")
                                    .setMessage("Select Medium in which language you want to learn!")
                                    .setCancelable(false)
                                    .setPositiveButton("English", (dialog, which) -> {
                                        sharedPreferences.edit().putString("medium", "en").apply();
                                        Intent intent = new Intent(NewSpleshActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .setNegativeButton("Hindi", (dialog, which) -> {
                                        sharedPreferences.edit().putString("medium", "hi").apply();
                                        Intent intent = new Intent(NewSpleshActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }).show();


                        } else {
                            Toast.makeText(this, "Sign in Failed.", Toast.LENGTH_SHORT).show();
                        }

                });*/

    }

    public void getCurrentUser() {
        // After login, Parse will cache it on disk, so
        // we don't need to login every time we open this
        // application
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
                    Log.d("tttttttttt", "getCurrentUser: ddddddddd");
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else {
                    Log.d("tttttttttt", "getCurrentUser: eeeeeeeeee");
//            Intent intent = signInClient.getSignInIntent();
//            loginResultLauncher.launch(intent);
//
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-itn intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                final SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

                new MaterialAlertDialogBuilder(this)
                        .setTitle("Select Your Medium")
                        .setCancelable(false)
                        .setPositiveButton("English", (dialog, which) -> {
                            sharedPreferences.edit().putString("medium", "en").apply();
                            Intent intent = new Intent(NewSpleshActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Hindi", (dialog, which) -> {
                            sharedPreferences.edit().putString("medium", "hi").apply();
                            Intent intent = new Intent(NewSpleshActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }).show();


            } else {
                Toast.makeText(this, "Sign in Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}