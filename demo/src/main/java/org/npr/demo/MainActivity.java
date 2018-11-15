package org.npr.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.npr.rad.Rad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RAD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
                DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
                ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(MainActivity.this, trackSelector, new DefaultLoadControl());
                DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.rad_sample_file_for_demo));
                final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(MainActivity.this);
                try {
                    rawResourceDataSource.open(dataSpec);
                } catch (RawResourceDataSource.RawResourceDataSourceException e) {
                    e.printStackTrace();
                }
                DataSource.Factory factory = new DataSource.Factory() {
                    @Override
                    public DataSource createDataSource() {
                        return rawResourceDataSource;
                    }
                };
                MediaSource audioSource = new ExtractorMediaSource(rawResourceDataSource.getUri(),
                        factory, new DefaultExtractorsFactory(), null, null);
                exoPlayer.prepare(audioSource);
                Rad.start(exoPlayer, trackSelector);
                exoPlayer.setPlayWhenReady(true);

                Rad.getInstance().setDebugListener(new Rad.DebugListener() {
                    @Override
                    public void onEventTriggered(String s) {
                        Log.d(TAG, "onEventTriggered: " + s);
                    }

                    @Override
                    public void onRequestSent(String s) {
                        Log.d(TAG, "onRequestSent: " + s);
                    }

                    @Override
                    public void onMetadataChanged(String data) {
                        Log.d(TAG, "onMetadataChanged: " + data);
                    }
                });
            }
        });

    }
}
