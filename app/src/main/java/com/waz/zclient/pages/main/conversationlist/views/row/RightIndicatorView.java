/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.pages.main.conversationlist.views.row;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.waz.api.IConversation;
import com.waz.zclient.R;
import com.waz.zclient.controllers.mediaplayer.MediaPlayerState;
import com.waz.zclient.controllers.streammediaplayer.IStreamMediaPlayerController;
import com.waz.zclient.core.stores.network.INetworkStore;
import com.waz.zclient.ui.text.CircleIconButton;
import com.waz.zclient.utils.ViewUtils;

public class RightIndicatorView extends LinearLayout {
    private final int initialPadding;
    // Media player control indicator
    private CircleIconButton mediaControlView;
    // muteButton and microphone indicator
    public CircleIconButton muteButton;
    private IConversation conversation;
    private IStreamMediaPlayerController streamMediaPlayerController;
    private INetworkStore networkStore;

    private boolean isMediaControlVisible;
    private boolean isMuteVisible;

    public void setConversation(final IConversation conversation) {
        this.conversation = conversation;
        updated();
    }

    /**
     * Creates the view.
     */
    public RightIndicatorView(final Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                                                                             LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.RIGHT;
        setLayoutParams(layoutParams);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        setId(R.id.row_conversation_behind_layout);
        LayoutInflater.from(getContext()).inflate(R.layout.conv_list_item_indicator, this, true);

        initialPadding = getResources().getDimensionPixelSize(R.dimen.framework__general__right_padding);

        muteButton = ViewUtils.getView(this, R.id.tv_conv_list_voice_muted);
        muteButton.setText(R.string.glyph__silence);
        muteButton.setSelectedTextColor(getResources().getColor(R.color.calling__ongoing__background__color));
        muteButton.setShowCircleBorder(false);

        mediaControlView = ViewUtils.getView(this, R.id.tv_conv_list_media_player);
        mediaControlView.setText(R.string.glyph__pause);
        mediaControlView.setSelectedTextColor(getResources().getColor(R.color.calling__ongoing__background__color));
        mediaControlView.setShowCircleBorder(false);
        mediaControlView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (streamMediaPlayerController == null ||
                    networkStore == null) {
                    return;
                }
                if (!streamMediaPlayerController.isSelectedConversation(conversation.getId())) {
                    return;
                }
                if (!networkStore.hasInternetConnection()) {
                    networkStore.notifyNetworkAccessFailed();
                    return;
                }

                final MediaPlayerState mediaPlayerState = streamMediaPlayerController.getMediaPlayerState(conversation.getId());
                if (mediaPlayerState.isPauseControl()) {
                    streamMediaPlayerController.pause(conversation.getId());
                } else if (mediaPlayerState.isPlayControl()) {
                    streamMediaPlayerController.play(conversation.getId());
                }
            }
        });
    }

    public void updated() {
        isMuteVisible = updateMuteIndicator();
        isMediaControlVisible = updateMediaPlayerIndicator();
    }

    private boolean updateMediaPlayerIndicator() {
        if (streamMediaPlayerController == null) {
            mediaControlView.setVisibility(GONE);
            return false;
        }
        MediaPlayerState mediaPlayerState = streamMediaPlayerController.getMediaPlayerState(conversation.getId());
        if (!streamMediaPlayerController.isSelectedConversation(conversation.getId()) ||
            mediaPlayerState == MediaPlayerState.PlaybackCompleted ||
            mediaPlayerState == MediaPlayerState.Stopped) {
            mediaControlView.setVisibility(GONE);
            return false;
        }
        if (mediaPlayerState.isPauseControl()) {
            mediaControlView.setText(R.string.glyph__pause);
            mediaControlView.setVisibility(VISIBLE);
            return true;
        } else if (mediaPlayerState.isPlayControl()) {
            mediaControlView.setText(R.string.glyph__play);
            mediaControlView.setVisibility(VISIBLE);
            return true;
        } else {
            mediaControlView.setVisibility(GONE);
            return false;
        }
    }

    private boolean updateMuteIndicator() {
        if (conversation.hasVoiceChannel()) {
            if (conversation.getVoiceChannel().isSilenced() || conversation.hasUnjoinedCall()) {
                muteButton.setVisibility(View.GONE);
                return false;
            } else {
                muteButton.setVisibility(View.VISIBLE);
                muteButton.setText(R.string.glyph__microphone_off);
                muteButton.setSelected(conversation.isVoiceChannelMuted());
                return true;
            }
        }

        muteButton.setSelected(false);
        if (conversation.isMuted()) {
            muteButton.setText(R.string.glyph__silence);
            muteButton.setVisibility(View.VISIBLE);
            return true;
        } else {
            muteButton.setVisibility(View.GONE);
            return false;
        }
    }

    public void setStreamMediaPlayerController(IStreamMediaPlayerController streamMediaPlayer) {
        this.streamMediaPlayerController = streamMediaPlayer;
    }

    public void setNetworkStore(INetworkStore networkStore) {
        this.networkStore = networkStore;
    }

    public int getTotalWidth() {
        int totalPadding = initialPadding;

        if (isMediaControlVisible) {
            totalPadding += getResources().getDimensionPixelSize(R.dimen.conversation_list__right_icon_width);
        }

        if (isMuteVisible) {
            totalPadding += getResources().getDimensionPixelSize(R.dimen.conversation_list__right_icon_width);
        }

        return totalPadding;
    }
}
