package com.digital.classes.fragments;


import static com.digital.classes.activities.MainActivity.InterstialAdId;
import static com.digital.classes.utils.Constant.loadVideoAdsListner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.digital.classes.R;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;

public class OptionFragment extends Fragment implements View.OnClickListener, IUnityAdsInitializationListener {

    private FragmentManager fragmentManager;
    private String t;
    private String q;
    private String chapterName;
    private String className;
    private String bookName;
    private String medium;
    private String chapterId;


    public OptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_option, container, false);

        chapterName = getArguments().getString("chapterName");
        chapterId = getArguments().getString("chapterId");
        className = getArguments().getString("classroom");
        bookName = getArguments().getString("book");
        medium = getArguments().getString("medium");
        ((TextView)view.findViewById(R.id.name)).setText(chapterName);
        view.findViewById(R.id.read_chapter_btn).setOnClickListener(this);
        view.findViewById(R.id.see_qa_btn).setOnClickListener(this);
        fragmentManager = getParentFragmentManager();
        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new ChapterFragment();
        Bundle data = new Bundle();
        data.putString("chapter",chapterId);
        data.putString("classroom",className);
        data.putString("book",bookName);
        switch(v.getId()){
            case R.id.read_chapter_btn:
                if(medium.compareTo("en")==0){
                    data.putString("type","et");
                }else if(medium.compareTo("hi")==0){
                    data.putString("type","ht");
                }
                fragment.setArguments(data);
                fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack("Chapter").commit();
                break;
            case R.id.see_qa_btn:
                if(medium.compareTo("en")==0){
                    data.putString("type","eq");
                }else if(medium.compareTo("hi")==0){
                    data.putString("type","hq");
                }
                fragment.setArguments(data);
                fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack("Chapter").commit();
                break;
        }
    }

    public void DisplayRewardedAd() {
        UnityAds.load(InterstialAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(getActivity(), InterstialAdId));
    }

    @Override
    public void onInitializationComplete() {
        DisplayRewardedAd();
    }
    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {

    }
}
