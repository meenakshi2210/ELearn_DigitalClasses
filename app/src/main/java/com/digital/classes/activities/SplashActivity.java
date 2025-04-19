package com.digital.classes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.digital.classes.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AwesomeSplash {

    private static final int RC_SIGN_IN = 123;

    @Override
    public void initSplash(ConfigSplash configSplash) {

        /* you don't have to override every property */

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.colorPrimaryDark); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(1000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_TOP);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_RIGHT); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.logo); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.DropOut); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        configSplash.setTitleSplash(getResources().getString(R.string.app_name));
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.BounceInDown);
       // configSplash.setTitleFont("fonts/streatwear.otf"); //provide string to your font located in assets/fonts/

    }

    @Override
    public void animationsFinished() {
        getCurrentUser();

    }

    public void getCurrentUser() {
        // After login, Parse will cache it on disk, so
        // we don't need to login every time we open this
        // application
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
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
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Hindi", (dialog, which) -> {
                            sharedPreferences.edit().putString("medium", "hi").apply();
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }).show();


            } else {
                Toast.makeText(this, "Sign in Failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
