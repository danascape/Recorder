/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.recorder.service;

import android.Manifest;
import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.IOException;
import java.nio.file.Path;

public class GoodQualityRecorder implements SoundRecording {
    private static final String FILE_NAME_EXTENSION_AAC = "mpeg";
    private static final String FILE_MIME_TYPE_AAC = "audio/mpeg";

    private MediaRecorder mRecorder = null;
    private boolean mIsPaused = false;
    private final Context mContext;
    private AudioManager mAudioManager;

    public GoodQualityRecorder(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
//    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    @RequiresPermission(Manifest.permission.CAPTURE_AUDIO_OUTPUT)
    public void startRecording(Path path) throws IOException {
        if (Build.VERSION.SDK_INT >= 31) {
            mRecorder = new MediaRecorder(mContext);
        } else {
            mRecorder = new MediaRecorder();
        }

        AudioDeviceInfo[] deviceList = mAudioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
//        AudioDeviceInfo routedDevice = mRecorder.getRoutedDevice();
        for (int i = 0; i < deviceList.length; i++) {
            Log.e("saalim", deviceList[i].toString());
//            Log.e("saalim", routedDevice.toString());
        }


//        AudioDeviceInfo routedDevice = mRecorder.getRoutedDevice();

        mRecorder.setOutputFile(path.toFile());
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
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
