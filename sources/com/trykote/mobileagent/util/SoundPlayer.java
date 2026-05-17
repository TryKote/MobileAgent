package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.PackedStringKeys;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public final class SoundPlayer {

    private static final int MIDI_NOTE_BASE_KEY = 430;
    private static final int MIDI_END_OF_TRACK = 0x300000;
    private static final long SOUND_TIMEOUT_MS = 10000L;

    public static final void playSound(int i) {
        stopSound();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new ByteBuffer().writeCharBytes("MThd\0\0\0\0").writeBytes(AppState.getBytes(i + MIDI_NOTE_BASE_KEY)).writeIntLE(MIDI_END_OF_TRACK).toByteArray());
            Object resource = IOUtils.registerResource((Object) byteArrayInputStream);
            UIState.setMediaResource(resource);
            if (resource != null) {
                Player playerCreatePlayer = Manager.createPlayer(byteArrayInputStream, StringPool.get(PackedStringKeys.MIME_TYPE_MIDI));
                UIState.setMediaPlayer(IOUtils.registerResource(playerCreatePlayer));
                try {
                    playerCreatePlayer.realize();
                } catch (Throwable unused) {
                }
                if (SettingsState.isSoundEnabled()) {
                    try {
                        ((javax.microedition.media.control.VolumeControl) playerCreatePlayer.getControl(StringPool.get(PackedStringKeys.MIDP_VOLUME_CONTROL))).setLevel(SettingsState.getVolumeLevel());
                    } catch (Throwable unused2) {
                    }
                }
                try {
                    playerCreatePlayer.prefetch();
                } catch (Throwable unused3) {
                }
                try {
                    playerCreatePlayer.start();
                } catch (Throwable unused4) {
                }
                TimerManager.setTimer(TimerManager.SLOT_SOUND, SOUND_TIMEOUT_MS);
            }
        } catch (Throwable unused5) {
        }
    }

    private static final void stopSound() {
        Player player = (Player) UIState.getMediaPlayer();
        if (player != null) {
            IOUtils.unregisterResource(player);
            try {
                player.stop();
            } catch (Throwable unused) {
            }
            try {
                player.close();
            } catch (Throwable unused2) {
            }
        }
        IOUtils.closeInput((InputStream) UIState.getMediaResource());
        UIState.clearMediaRange();
    }

    public static final void checkSoundTimer() {
        boolean z;
        long[] jArr = TimerManager.timers;
        long j = jArr[6];
        if (j == 0 || j >= System.currentTimeMillis()) {
            z = false;
        } else {
            jArr[6] = 0;
            z = true;
        }
        if (z) {
            stopSound();
        }
    }
}
