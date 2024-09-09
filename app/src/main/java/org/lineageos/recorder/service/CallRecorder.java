package org.lineageos.recorder.service;

import android.Manifest;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresPermission;

import java.io.IOException;
import java.nio.file.Path;

public class CallRecorder implements SoundRecording {
    private static final String FILE_NAME_EXTENSION_AAC = "mpeg";
    private static final String FILE_MIME_TYPE_AAC = "audio/mpeg";

    private MediaRecorder mRecorder = null;
    private boolean mIsPaused = false;
    private final Context mContext;

    public CallRecorder(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    @RequiresPermission(Manifest.permission.CAPTURE_AUDIO_OUTPUT)
    public void startRecording(Path path) throws IOException {
        if (Build.VERSION.SDK_INT >= 31) {
            mRecorder = new MediaRecorder(mContext);
        } else {
            mRecorder = new MediaRecorder();
        }

        mRecorder.setOutputFile(path.toFile());
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mRecorder.prepare();
        mRecorder.start();
    }

    @Override
    public boolean stopRecording() {
        if (mRecorder == null) {
            return false;
        }

        if (mIsPaused) {
            mIsPaused = false;
            mRecorder.resume();
        }

        // needed to prevent app crash when starting and stopping too fast
        try {
            mRecorder.stop();
        } catch (RuntimeException rte) {
            return false;
        } finally {
            mRecorder.release();
        }
        return true;
    }

    @Override
    public boolean pauseRecording() {
        if (mIsPaused || mRecorder == null) {
            return false;
        }

        mIsPaused = true;
        mRecorder.pause();
        return true;
    }

    @Override
    public boolean resumeRecording() {
        if (!mIsPaused || mRecorder == null) {
            return false;
        }

        mRecorder.resume();
        mIsPaused = false;
        return true;
    }

    @Override
    public int getCurrentAmplitude() {
        return mRecorder == null ? 0 : mRecorder.getMaxAmplitude();
    }

    @Override
    public String getMimeType() {
        return FILE_MIME_TYPE_AAC;
    }

    @Override
    public String getFileExtension() {
        return FILE_NAME_EXTENSION_AAC;
    }
}
