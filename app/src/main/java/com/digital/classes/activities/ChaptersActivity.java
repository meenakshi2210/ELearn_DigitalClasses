package com.digital.classes.activities;

import static com.digital.classes.activities.MainActivity.InterstialAdId;
import static com.digital.classes.utils.Constant.loadVideoAdsListner;

import android.content.Context;
import android.os.Bundle;

import com.digital.classes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.digital.classes.fragments.OptionFragment;
import com.digital.classes.models.Chapter;
import com.digital.classes.utils.NaturalOrderComparator;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ChaptersActivity extends AppCompatActivity implements IUnityAdsInitializationListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ListView navLV;
    private ArrayList<Chapter> chapters = new ArrayList<Chapter>();
    private ArrayList<String> chapterNames = new ArrayList<>();
    private ArrayList<String> chapterIds = new ArrayList<>();
    private Toolbar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String className;
    private String bookName;
    private String medium;
    private ArrayAdapter<String> adapter;
    private FragmentManager fragmentManager;
    private DrawerLayout drawer;
    private TextView message;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Chapter");
        setSupportActionBar(toolbar);
        message = findViewById(R.id.message);
        progressBar=findViewById(R.id.chapterProgressBarrr);
        fragmentManager = getSupportFragmentManager();

        className = getIntent().getStringExtra("classroom");
        bookName = getIntent().getStringExtra("book");
        medium = getSharedPreferences("pref", Context.MODE_PRIVATE).getString("medium","en");

        setupNavigationDrawer();

        setupListView();

        setupChapterList();

        drawer.openDrawer(GravityCompat.START);

    }

    private void openChapter(int position){
        String chapterName = chapterNames.get(position);
        String chapterId = chapterIds.get(position);
        Fragment optionFragment = new OptionFragment();//Get Fragment Instance
        Bundle data = new Bundle();//Use bundle to pass data

        data.putString("chapterId",chapterId);
        data.putString("chapterName",chapterName);
        data.putString("classroom",className);
        data.putString("book",bookName);
        data.putString("medium",medium);
        toolbar.setTitle(chapterName);
        optionFragment.setArguments(data);//Finally set argument bundle to fragment
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, optionFragment).commit();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        message.setVisibility(View.GONE);
    }

    private void setupListView() {
        navLV = findViewById(R.id.navLV);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, chapterNames);
        navLV.setAdapter(adapter);
        navLV.setOnItemClickListener((parent, view, position, id) -> openChapter(position));
    }

    private void setupChapterList() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("classes").document(className).collection("Books").document(bookName).collection("Chapters").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    chapterNames.clear();
                    chapterIds.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        String name = (String) document.get("name");
                        chapterIds.add(id);
                        if(name.compareTo("")==0){
                            chapterNames.add(id);
                        }
                        else{
                            chapterNames.add(id+": "+name);
                        }

                    }
                    Collections.sort(chapterIds,new NaturalOrderComparator());
                    Collections.sort(chapterNames,new NaturalOrderComparator());
                    adapter.notifyDataSetChanged();
                    openChapter(0);
                    drawer.openDrawer(GravityCompat.START);
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.d("FETCHING", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void setupNavigationDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_open);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            super.onBackPressed();
//        }
    }
    public void DisplayRewardedAd() {
        UnityAds.load(InterstialAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(ChaptersActivity.this, InterstialAdId));
    }

    @Override
    public void onInitializationComplete() {
        DisplayRewardedAd();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {

    }
}
