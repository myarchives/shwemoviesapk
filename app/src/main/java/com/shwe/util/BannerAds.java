package com.shwe.util;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ixidev.gdpr.GDPRChecker;

public class BannerAds {
    public static int cc = 0;
    public static void ShowBannerAds(Context context, LinearLayout mAdViewLayout) {

        switch (Constant.statusBannerAds){
            case 1 : ShowGoogleBannerAds(context,mAdViewLayout);break;
            case 2 : ShowFacebookBannerAds(context, mAdViewLayout);break;
        }
    }
    public static void ShowFacebookBannerAds(Context context, LinearLayout mAdViewLayout) {
        if (Constant.isFaceBookBanner) {
            com.facebook.ads.AdView mAdView = new com.facebook.ads.AdView(context, "IMG_16_9_APP_INSTALL#"+Constant.adFBBannerId, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            // Add the ad view to your activity layout
            mAdViewLayout.addView(mAdView);
            mAdView.loadAd();
            mAdViewLayout.setGravity(Gravity.CENTER);
        } else {
            mAdViewLayout.setVisibility(View.GONE);
        }
    }
    public static void ShowGoogleBannerAds(Context context, LinearLayout mAdViewLayout) {
        if (Constant.isBanner) {
            AdView mAdView = new AdView(context);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(Constant.adMobBannerId);
            AdRequest.Builder builder = new AdRequest.Builder();
            GDPRChecker.Request request = GDPRChecker.getRequest();
            if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                // load non Personalized ads
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
            } // else do nothing , it will load PERSONALIZED ads
            mAdView.loadAd(builder.build());
            mAdViewLayout.addView(mAdView);
            mAdViewLayout.setGravity(Gravity.CENTER);
        } else {
            mAdViewLayout.setVisibility(View.GONE);
        }
    }


}
