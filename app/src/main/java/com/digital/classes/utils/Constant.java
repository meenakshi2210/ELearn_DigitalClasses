package com.digital.classes.utils;

import android.app.Activity;

import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class Constant {
    public static IUnityAdsShowListener showVideoAdsListner(Activity activity) {
        IUnityAdsShowListener showListner = new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
                // Toast.makeText(activity, "Unity ads failed Show listner ="+unityAdsShowError, Toast.LENGTH_SHORT).show();
                if (unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_READY) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NO_CONNECTION) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_INITIALIZED) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.INTERNAL_ERROR)) {
                }
            }

            @Override
            public void onUnityAdsShowStart(String s) {
                //  Toast.makeText(activity, "Watch Complete to Get Reward", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnityAdsShowClick(String s) {
                //  Toast.makeText(activity, "Show click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnityAdsShowComplete(String adUnitId, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

                if (unityAdsShowCompletionState.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                    // Toast.makeText(getApplicationContext(), "Ads Completed", Toast.LENGTH_SHORT).show();

                } else {
                    // Toast.makeText(getApplicationContext(), "skipped by user", Toast.LENGTH_SHORT).show();
                }
            }
        };
        return showListner;
    }

    public static IUnityAdsLoadListener loadVideoAdsListner(Activity activity, String adType) {
        IUnityAdsLoadListener  loadListener = new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String s) {
                //  Toast.makeText(activity, "Ad Load listner", Toast.LENGTH_SHORT).show();
                UnityAds.show(activity, adType, new UnityAdsShowOptions(), Constant.showVideoAdsListner(activity));
            }

            @Override
            public void onUnityAdsFailedToLoad(String addId, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {
                // Toast.makeText(activity, "Unity Ads load failed listner  =="+s1, Toast.LENGTH_SHORT).show();
            }
        };
        return loadListener;
    }


}
