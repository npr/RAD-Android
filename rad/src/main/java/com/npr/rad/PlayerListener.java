/*
 * Copyright 2018 NPR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.npr.rad;

import android.content.Intent;
import android.text.TextUtils;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.CommentFrame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.npr.rad.model.PlayBackEvent;

import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import static com.npr.rad.Constants.METADATA;
import static com.npr.rad.Constants.REMOTE_AUDIO_DATA;


public class PlayerListener implements Player.EventListener {

    private long playbackStartTimestamp;
    private boolean playing;
    private Rad rad;

    PlayerListener(Rad rad) {
        this.rad = rad;
    }


    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
        //no-op
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Rad.getInstance().setReportingData(null);
        persistMetadata(extractRadPayload(trackSelections));
    }

    private void persistMetadata(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        Intent i = new Intent(METADATA);
        i.putExtra(METADATA, s);
        JobIntentService.enqueueWork(rad.getApplicationContext(), PersistenceService.class, 1, i);
    }

    @Nullable
    private String extractRadPayload(TrackSelectionArray trackSelections) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = rad.getSelector().getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return null;
        }
        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            TrackSelection trackSelection = trackSelections.get(rendererIndex);
            if (rendererTrackGroups.length > 0 && trackSelection != null) {
                for (int selectionIndex = 0; selectionIndex < trackSelection.length(); selectionIndex++) {
                    Metadata metadata = trackSelection.getFormat(selectionIndex).metadata;
                    if (metadata != null) {
                        for (int i = 0; i < metadata.length(); i++) {
                            if (metadata.get(i) instanceof InternalFrame) {
                                InternalFrame frame = (InternalFrame) metadata.get(i);
                                if (frame.text.contains(REMOTE_AUDIO_DATA)) {
                                    return frame.text;
                                }
                            }
                            if (metadata.get(i) instanceof CommentFrame) {
                                CommentFrame frame = (CommentFrame) metadata.get(i);
                                if (frame.text.contains(REMOTE_AUDIO_DATA)) {
                                    return frame.text;
                                }
                            }
                            if (metadata.get(i) instanceof TextInformationFrame) {
                                TextInformationFrame frame = (TextInformationFrame) metadata.get(i);
                                if (frame.value.contains(REMOTE_AUDIO_DATA)) {
                                    return frame.value;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        //no-op
    }

    @Override
    public synchronized void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        long elapsedMillis = System.currentTimeMillis() - playbackStartTimestamp;
        if (Player.STATE_ENDED == playbackState) {
            rad.addPlayBackEvent(new PlayBackEvent(PlayBackEvent.State.STOPPED, elapsedMillis));
            playing = false;
        }
        if (Player.STATE_READY == playbackState) {
            if (playWhenReady) {
                playbackStartTimestamp = System.currentTimeMillis();
                rad.addPlayBackEvent(new PlayBackEvent(PlayBackEvent.State.STARTED, rad.getPlayer().getContentPosition()));
            } else {
                if (playing) {
                    rad.addPlayBackEvent(new PlayBackEvent(PlayBackEvent.State.STOPPED, elapsedMillis));
                }
            }
            playing = playWhenReady;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        //no-op
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        //no-op
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        if (playing) {
            long elapsedMills = System.currentTimeMillis() - playbackStartTimestamp;
            rad.addPlayBackEvent(new PlayBackEvent(PlayBackEvent.State.STOPPED, elapsedMills));
            playing = false;
        }
    }


    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        //no-op
    }

}
