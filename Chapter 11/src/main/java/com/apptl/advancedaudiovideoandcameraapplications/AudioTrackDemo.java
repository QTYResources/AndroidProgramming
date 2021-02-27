package com.apptl.advancedaudiovideoandcameraapplications;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * @author Erik Hellman
 */
public class AudioTrackDemo {

    private final AudioTrack mAudioTrack;
    private final int mMinBufferSize;

    public AudioTrackDemo() {
        mMinBufferSize = AudioTrack.getMinBufferSize(16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                mMinBufferSize * 2,
                AudioTrack.MODE_STREAM);
    }

    public void playPcmPacket(byte[] pcmData) {
        if(mAudioTrack != null
                && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            if(mAudioTrack.getPlaybackRate()
                    != AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.play();
            }
            mAudioTrack.write(pcmData, 0, pcmData.length);
        }
    }

    public void stopPlayback() {
        if(mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }
}
