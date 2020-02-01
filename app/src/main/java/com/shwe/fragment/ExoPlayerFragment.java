package com.shwe.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.shwe.movies.R;
import com.shwe.util.Events;
import com.shwe.util.GlobalBus;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.Subscribe;

public class ExoPlayerFragment extends Fragment {
    private static final String TAG = "StreamPlayerActivity";
    //http://www.viaviweb.in/envato/cc/hdvideo_app_new_demo/uploads/70010_kitkat.mp4 http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8
    String url;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DataSource.Factory mediaDataSourceFactory;
    private ProgressBar progressBar;
    ImageView imgFull;
    public boolean isFullScr = false;
    Button btnTryAgain;

    private static final String streamUrl = "streamUrl";

    public static ExoPlayerFragment newInstance(String SId) {
        ExoPlayerFragment f = new ExoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(streamUrl, SId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exo_player, container, false);

        GlobalBus.getBus().register(this);

        if (getArguments() != null) {
            url = getArguments().getString(streamUrl);
        }

        progressBar = rootView.findViewById(R.id.progressBar);
        imgFull = rootView.findViewById(R.id.img_full_scr);
        btnTryAgain = rootView.findViewById(R.id.btn_try_again);

        mediaDataSourceFactory = buildDataSourceFactory(true);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        RenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(getActivity(), renderersFactory, trackSelector, loadControl);
        playerView = rootView.findViewById(R.id.exoPlayerView);
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();

        Uri uri = Uri.parse(url);

        MediaSource mediaSource = buildMediaSource(uri, null);

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                Log.d(TAG, "onTimelineChanged: ");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(TAG, "onTracksChanged: " + trackGroups.length);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onLoadingChanged: " + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG, "onPlayerStateChanged: " + playWhenReady);
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError: ", error);
                player.stop();
                btnTryAgain.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //    errorDialog();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.d(TAG, "onPositionDiscontinuity: true");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        imgFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScr) {
                    isFullScr = false;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(false);
                    GlobalBus.getBus().post(fullScreen);
                } else {
                    isFullScr = true;
                    Events.FullScreen fullScreen = new Events.FullScreen();
                    fullScreen.setFullScreen(true);
                    GlobalBus.getBus().post(fullScreen);
                }
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTryAgain.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                retryLoad();
            }
        });

        return rootView;
    }

    public void retryLoad() {
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri, null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(getActivity(), bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), "ExoPlayerDemo"), bandwidthMeter);
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScr = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            imgFull.setImageResource(R.drawable.ic_fullscreen_exit);
        } else {
            imgFull.setImageResource(R.drawable.ic_fullscreen);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
    }
}
