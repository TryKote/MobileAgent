package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;

public final class SoundPlayer {

    /* renamed from: a */
    public static final void playSound(int i) {
        stopSound();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new ByteBuffer().writeCompressed(PackedStringKeys.MIDI_HEADER).writeCompressed(i + 430).writeIntLE(3145472).toByteArray());
            Object resource = IOUtils.registerResource((Object) byteArrayInputStream);
            Storage.state().setObject(UIKeys.RANGE_MEDIA_RESOURCES_START, resource);
            if (null != resource) {
                Player playerCreatePlayer = Manager.createPlayer(byteArrayInputStream, Storage.resources().getString(PackedStringKeys.MIME_TYPE_MIDI));
                Storage.state().setObject(UIKeys.OBJ_MEDIA_PLAYER, IOUtils.registerResource(playerCreatePlayer));
                try {
                    playerCreatePlayer.realize();
                } catch (Throwable unused) {
                }
                if (Storage.state().getBool(SettingsKeys.SETTING_SOUND_ENABLED)) {
                    try {
                        ((javax.microedition.media.control.VolumeControl) playerCreatePlayer.getControl(Storage.resources().getString(PackedStringKeys.MIDP_VOLUME_CONTROL))).setLevel(Storage.state().getInt(SettingsKeys.SETTING_VOLUME_LEVEL));
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
                TimerManager.setTimer(TimerManager.SLOT_SOUND, 10000L);
            }
        } catch (Throwable unused5) {
        }
    }

    /* renamed from: m */
    private static final void stopSound() {
        Player player = (Player) Storage.state().getObject(UIKeys.OBJ_MEDIA_PLAYER);
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
        IOUtils.closeInput((InputStream) Storage.state().getObject(UIKeys.RANGE_MEDIA_RESOURCES_START));
        Storage.state().clearRange(UIKeys.RANGE_MEDIA_RESOURCES_START, UIKeys.OBJ_MEDIA_PLAYER);
    }

    /* renamed from: a */
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
