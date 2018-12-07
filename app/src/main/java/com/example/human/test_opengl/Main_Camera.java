package com.example.human.test_opengl;

//import android.graphics.Camera;
import android.app.Application;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.view.View;
import android.hardware.Camera.Size;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.view.Menu;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.example.human.test_opengl.MainActivity.getAppContext;
import static com.example.human.test_opengl.MainActivity.getMainActivity;

public class Main_Camera implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback, Camera.AutoFocusCallback {
    private Camera         cameraDevice        = null;
    private SurfaceHolder  cameraSurfaceHolder = null;
    private int id_camera_front[];
    private int id_camera_back[];
    private int number_facing_front_cameras;
    private int number_facing_back_cameras;
    private int index_facing_back_cameras;
    private int index_facing_front_cameras;
    private int index_support_resolution;
    private boolean back_or_front;
    private List<Size> allSupportSizes;
    private SurfaceView preview;
    private Button shot_button;
    private Switch switch_camera;
    private SeekBar resolution_camera;

    Main_Camera(){
            int number_cameras = Camera.getNumberOfCameras();
            number_facing_front_cameras = 0;
            number_facing_back_cameras = 0;
            index_facing_back_cameras = 0;
            index_facing_front_cameras = 0;
            index_support_resolution = 0;
            int id_camera[] = new int[number_cameras + number_cameras];

            for (int i = 0; i < number_cameras; i++) {
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    id_camera[number_facing_front_cameras] = i;
                    number_facing_front_cameras++;
                }
                if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                    id_camera[number_cameras + number_facing_back_cameras] = i;
                    number_facing_back_cameras++;
                }
            }
            id_camera_front = new int[number_facing_front_cameras];
            id_camera_back = new int[number_facing_back_cameras];
            for (int i = 0; i < number_facing_front_cameras; i++)
                id_camera_front[i] = id_camera[i];
            for (int i = 0; i < number_facing_back_cameras; i++)
                id_camera_back[i] = id_camera[number_cameras + i];
        back_or_front = true;

    }
    public void InitSurface(SurfaceView view, SurfaceHolder surfaceHolder, Button shotButton, Switch switch_button, SeekBar resolution){
        preview = view;
        resolution_camera = resolution;
        switch_camera = switch_button;
        cameraSurfaceHolder = surfaceHolder;
        cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraSurfaceHolder.addCallback(this);



        switch_camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int width = preview.getWidth(), height = preview.getHeight();
                if (isChecked) {
                    back_or_front = false;
                    releaseCamera();
                    cameraDevice = getCameraInstance();
                    setSizeCamera(cameraSurfaceHolder, width, height);
                } else {
                    back_or_front = true;
                    releaseCamera();
                    cameraDevice = getCameraInstance();
                    setSizeCamera(cameraSurfaceHolder, width, height);
                }
            }
        });
        resolution_camera.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                index_support_resolution = resolution_camera.getProgress();
                Toast.makeText(getAppContext(), "set resolution = " + String.valueOf(allSupportSizes.get(index_support_resolution).width) + " x " + String.valueOf(allSupportSizes.get(index_support_resolution).height), Toast.LENGTH_LONG).show();
                Camera.Parameters cameraParams = cameraDevice.getParameters();
                cameraParams.setPictureSize(allSupportSizes.get(index_support_resolution).width, allSupportSizes.get(index_support_resolution).height);
                cameraDevice.setParameters(cameraParams);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                Toast.makeText(getAppContext(), "resolution = " + String.valueOf(allSupportSizes.get(progress).width) + " x " + String.valueOf(allSupportSizes.get(progress).height), Toast.LENGTH_SHORT).show();

            }

        });
        shot_button = shotButton;
        shot_button.setOnClickListener(this);
        preview.setOnClickListener(this);
        shot_button.setText("Make photo");

    }
    public Camera getCameraInstance(){
        Camera camera = null;
            if (back_or_front){
                if (index_facing_back_cameras<number_facing_back_cameras){
                    camera = Camera.open(id_camera_back[index_facing_back_cameras]);
                    index_facing_back_cameras++;
                    if (index_facing_back_cameras == number_facing_back_cameras)
                        index_facing_back_cameras = 0;
                }
            }else{
                if (index_facing_front_cameras<number_facing_front_cameras){
                camera = Camera.open(id_camera_front[index_facing_front_cameras]);
                index_facing_front_cameras++;
                if (index_facing_front_cameras == number_facing_front_cameras)
                    index_facing_front_cameras = 0;
                }
            }
        allSupportSizes = camera.getParameters().getSupportedPictureSizes();
        int max_index_support_resolution = allSupportSizes.size() - 1;
        resolution_camera.setMax(max_index_support_resolution);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(parameters);
        index_support_resolution = 0;
        resolution_camera.setProgress(0);
        return camera;
    }

    void OpenCamera(){
        releaseCamera();
        cameraDevice = getCameraInstance();

        //    setSizeCamera(cameraSurfaceHolder, preview.getWidth(), preview.getHeight());
    }


    void CloseCamera(){
        if (cameraDevice != null)
        {
            cameraDevice.setPreviewCallback(null);
            cameraDevice.stopPreview();
            cameraDevice.release();
            cameraDevice = null;
        }
    }

    private void releaseCamera() {
        if (cameraDevice != null) {
            cameraDevice.release();
            cameraDevice = null;
        }
    }

    public Camera.Size getBestPreviewSize(Camera.Parameters parameters, int w, int h)
    {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes())
        {
            if (size.width <= w && size.height <= h)
            {
                if (null == result)
                    result = size;
                else
                {
                    int resultDelta = w - result.width + h - result.height;
                    int newDelta    = w - size.width   + h - size.height;

                    if (newDelta < resultDelta)
                        result = size;
                }
            }
        }
        return result;
    }
    private void setSizeCamera(SurfaceHolder holder, int width, int height){
        int angle;
        Camera.Parameters cameraParams = cameraDevice.getParameters();
        Display display;
        display = getMainActivity().getWindowManager().getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                angle = 90;
                break;
            case Surface.ROTATION_90:
                angle = 0;
                break;
            case Surface.ROTATION_180:
                angle = 270;
                break;
            case Surface.ROTATION_270:
                angle = 180;
                break;
            default:
                angle = 90;
                break;
        }
        cameraDevice.setDisplayOrientation(angle);

        Camera.Size size = getBestPreviewSize(cameraParams,width,height);

        cameraParams.setPreviewSize(size.width, size.height);
        cameraParams.setPictureSize(allSupportSizes.get(index_support_resolution).width, allSupportSizes.get(index_support_resolution).height);

        try {
            cameraDevice.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cameraDevice.startPreview();

    }
    static class SaveInBackground extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... arrayOfByte) {
            try
            {
                File path = new File (Environment.getExternalStorageDirectory(), "CameraExample");
                if (! path.exists()){
                    if (! path.mkdirs()){
                        return(null);
                    }
                }

                FileOutputStream os = new FileOutputStream(String.format(Environment.getExternalStorageDirectory() + "/CameraExample/%d.jpg", System.currentTimeMillis()));
                os.write(arrayOfByte[0]);
                os.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return(null);
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        setSizeCamera(holder, width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        releaseCamera();
        cameraDevice = getCameraInstance();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (cameraDevice == null)
            return;
        cameraDevice.stopPreview();
        cameraDevice.release();
        cameraDevice = null;
    }

    @Override
    public void onClick(View v)
    {
        if (v == shot_button)
        {
            cameraDevice.takePicture(null, null, null, this);
        }else if(v == preview){
            cameraDevice.autoFocus(this);
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
    {
        new SaveInBackground().execute(paramArrayOfByte);
        paramCamera.startPreview();
    }

    @Override
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
        if (paramBoolean)
        {

        }
    }

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
    {
        // здесь можно обрабатывать изображение, показываемое в preview
    }
}
