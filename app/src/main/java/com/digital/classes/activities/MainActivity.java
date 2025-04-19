package com.digital.classes.activities;

import static com.digital.classes.utils.Constant.loadVideoAdsListner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digital.classes.FirebaseViewModel;
import com.digital.classes.R;
import com.digital.classes.adapters.ClassAdapter;
import com.digital.classes.listeners.ClassClickListener;
import com.digital.classes.models.Class;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ClassClickListener,
        IUnityAdsInitializationListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private ArrayList<Class> classes = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseViewModel viewModel;
    private ProgressBar progressBar;
   // private long balance;
   public static String InterstialAdId="Rewarded_Android",
           UnityID="5305514",
           VideoAdId="Interstitial_Android";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        progressBar=findViewById(R.id.mainProgressBarrr);
        setSupportActionBar(toolbar);

        new Handler().postDelayed(() -> {
            UnityAds.initialize(getApplicationContext(), UnityID, false, this);
        },3000);

        viewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        setupNavigationDrawer();
        fetchUser();
        setupRecyclerView();

        setupClassList();
      //  findViewById(R.id.add_bal).setOnClickListener(v -> openAddBalanceScreen());

    }

    private void fetchUser() {
        viewModel.getWallet().observe(this, wallet -> {
          //  balanceTv.setText("Balance Available: \u20b9 " + wallet.getBalance());
           // balance = wallet.getBalance();
        });
    }

    private void setupClassList() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("classes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                classes.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Class item = new Class(document.getId());
                    classes.add(item);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                Log.d("FETCHING", "Error getting documents: ", task.getException());
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClassAdapter(classes, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupNavigationDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.getHeaderView(0);
       // headerUserName = navHeaderView.findViewById(R.id.user_name);
        // balanceTv = navHeaderView.findViewById(R.id.balance);
       // headerUserLogo = navHeaderView.findViewById(R.id.imageView);
      //  headerUserName.setText(user.getDisplayName());
        // balanceTv.setText("Balance Available: \u20b9 " + balance);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.change_medium: {
                final SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

                 new MaterialAlertDialogBuilder(this)
                        .setTitle("Select Your Medium")
                        .setPositiveButton("English", (dialog1, which) -> {
                            sharedPreferences.edit().putString("medium", "en").apply();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Hindi", (dialog12, which) -> {
                            sharedPreferences.edit().putString("medium", "hi").apply();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }).show();
                break;
            }
            case R.id.share: {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Refer a Friend");
                alertDialogBuilder
                        .setMessage("Refer the app to your friends...")
                        .setCancelable(false)
                        .setPositiveButton("Refer",
                                (dialog, id) -> {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    String REFER_URL = "https://play.google.com/store/apps/details?id="+getApplication().getPackageName();
                                    intent.putExtra(Intent.EXTRA_TEXT, REFER_URL);
                                    intent.setType("text/plain");
                                    startActivity(intent);
                                    dialog.dismiss();
                                })

                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            }
            case R.id.privacy_policy: {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("name", "Privacy Policy");
                intent.putExtra("file_name", "privacy_policy.txt");
                startActivity(intent);
                break;
            }
            case R.id.terms_cond: {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("name", "Terms & Conditions");
                intent.putExtra("file_name", "terms_cond.txt");
                startActivity(intent);
                break;
            }
//            case R.id.contact: {
//                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
//                intent.putExtra("name", "Contact us");
//                intent.putExtra("file_name", "contact.txt");
//                startActivity(intent);
//                break;
//            }
            case R.id.about: {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("name", "About us");
                intent.putExtra("file_name", "about.txt");
                startActivity(intent);
                break;
            }
            case R.id.services: {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("name", "Our Services");
                intent.putExtra("file_name", "services.txt");
                startActivity(intent);
                break;
            }
            /*case R.id.refund_rep: {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("name", "Refund & Cancellation Policy");
                intent.putExtra("file_name", "refund.txt");
                startActivity(intent);
                break;
            }*/
            case R.id.exit: {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Exit Confirmation");
                alertDialogBuilder
                        .setMessage("Click yes to Exit!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                (dialog, id) -> {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                })

                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            }
          /*  case R.id.checkout: {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra("name", "Checkout Process");
                intent.putExtra("file_name", "checkout.txt");
                startActivity(intent);
                break;
            }*/
            case R.id.menu_my_data_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("Delete Data", (dialog, which) -> {
                    deleteDocument(viewModel.getUserRef());
                }).setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss());
                showAlertdialog(MainActivity.this, builder, "Data Alert!", "Are you sure want to Delete your Data? Remember you never recover your Data in Future.");
                break;
            case R.id.menu_my_account_delete:
                AlertDialog.Builder builderr = new AlertDialog.Builder(this);
                builderr.setPositiveButton("Delete Account", (dialog, which) -> {
                    deleteDocument(viewModel.getUserRef());
                }).setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss());
                showAlertdialog(MainActivity.this, builderr, "Alert!", "Are you sure want to delete your Account? Remember you never recover this Account in Future.");
                break;
        }
        return true;
    }


    private void showAlertdialog(Activity activity, AlertDialog.Builder builder, String title, String msg) {
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(msg);
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(activity.getResources().getColor(R.color.alert_btn_color));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(activity.getResources().getColor(R.color.alert_btn_color));
    }


    private void deleteDocument(DocumentReference documentRef) {
        documentRef.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //   FirebaseAuth.getInstance().signOut();
                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete();
                        finishAffinity();
                    }
                });
    }
    private void openAddBalanceScreen() {
        Intent intent = new Intent(this, AddMoneyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Class item) {
        Intent intent = new Intent(this, BookActivity.class);
        intent.putExtra("classroom", item.getName());
        startActivity(intent);

    }
    public void DisplayRewardedAd() {
        UnityAds.load(InterstialAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, InterstialAdId));
    }

    @Override
    public void onInitializationComplete() {
        //show video ads
       // UnityAds.load(VideoAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, VideoAdId));
        DisplayRewardedAd();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {

    }
}
