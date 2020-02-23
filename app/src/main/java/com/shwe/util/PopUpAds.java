package com.shwe.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ixidev.gdpr.GDPRChecker;


public class PopUpAds {
    public static int a = 0;
    public static void showInterstitialAds(Context context, int adapterPosition, RvOnClickListener clickListener) {
        a++;
        if (a % 2 == 0) {
            Log.i("TEST ADS ROUND", "GOOGLE");
            showGoogleInterstitialAds(context, adapterPosition, clickListener);

        } else {
            Log.i("TEST ADS ROUND", "FACEBOOK");
            showFBInterstitialAds(context, adapterPosition, clickListener);

        }
    }

    public static void showGoogleInterstitialAds(Context context, int adapterPosition, RvOnClickListener clickListener) {
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

    public static void showFBInterstitialAds(Context context, int adapterPosition, RvOnClickListener clickListener) {
        if (Constant.isInterstitial) {
            Constant.AD_COUNT += 1;
            if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                final String TAG = PopUpAds.class.getSimpleName();
                final com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(context, "2482238635323765_2568780046669623");

                // Set listeners for the Interstitial Ad
                interstitialAd.setAdListener(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        // Interstitial ad displayed callback
                        Log.e(TAG, "Interstitial ad displayed.");
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        // Interstitial dismissed callback
                        Log.e(TAG, "Interstitial ad dismissed.");
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Ad error callback
                        Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        // Interstitial ad is loaded and ready to be displayed
                        Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                        // Show the ad
                        interstitialAd.show();
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Ad clicked callback
                        Log.d(TAG, "Interstitial ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Ad impression logged callback
                        Log.d(TAG, "Interstitial ad impression logged!");
                    }
                });

                // For auto play video ads, it's recommended to load the ad
                // at least 30 seconds before it is shown
                interstitialAd.loadAd();
                Constant.AD_COUNT = 0;

            } else {
                clickListener.onItemClick(adapterPosition);
            }
        } else {
            clickListener.onItemClick(adapterPosition);
        }
    }
}
