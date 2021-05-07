package com.test.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import tech.kicky.simplifyopencv.R;

/**
 * 单独将这个类提取出来,方便以后不用opencv时减小库的大小
 */
public class EncodeFaceUtils {
    private static final String TAG = "EncodeFaceUtils";

    public static CascadeClassifier getDetector(Context context,int[] pixels,int width,int height) {
        try {
            // load cascade file from application resources
            InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            Log.d(TAG, "getDetector: " + mCascadeFile.exists());
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            Log.d(TAG, "getDetector path: " + mCascadeFile.getAbsolutePath());
            List<Rect> list = detect(pixels,width,height,mCascadeFile.getAbsolutePath());
            Log.d(TAG, "getDetector list: " + list.toString());
            return null;
//            CascadeClassifier mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//            Log.d(TAG, "getDetector: " + mCascadeFile.getAbsolutePath());
//            if (mJavaDetector.empty()) {
//                Log.e(TAG, "Failed to load cascade classifier");
//                mJavaDetector = null;
//            } else
//                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
//            cascadeDir.delete();
//            return mJavaDetector;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
            return null;
        }
    }

    public static native List<Rect> detect(int[] pixels, int width, int height, String path);

    static {
        System.loadLibrary("native-lib");
    }

}
