package com.shwe.util;

import android.content.Context;
import android.os.Bundle;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ixidev.gdpr.GDPRChecker;

public class PopUpAds {
    public static void showInterstitialAds(Context context, int adapterPosition, RvOnClickListener clickListener) {
        if (Constant.isInterstitial) {
            Constant.AD_COUNT += 1;
            if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                final InterstitialAd mInterstitial = new InterstitialAd(context);
                mInterstitial.setAdUnitId(Constant.adMobInterstitialId);
                GDPRChecker.Request request = GDPRChecker.getRequest();
                AdRequest.Builder builder = new AdRequest.Builder();
                if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                    Bundle extras = new Bundle();
                    extras.putString("npa", "1");
                    builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                }
                mInterstitial.loadAd(builder.build());
                Constant.AD_COUNT = 0;
                mInterstitial.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mInterstitial.show();
                    }

                    @Override
                    public void onAdClosed() {
                        clickListener.onItemClick(adapterPosition);
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        clickListener.onItemClick(adapterPosition);
                        super.onAdFailedToLoad(i);
                    }
                });

            } else {
                clickListener.onItemClick(adapterPosition);
            }
        } else {
            clickListener.onItemClick(adapterPosition);
        }
    }
}
