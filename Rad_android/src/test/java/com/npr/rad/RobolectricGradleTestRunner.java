package com.npr.rad;

import android.os.Build;
import android.util.Log;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

import java.io.File;
import java.io.IOException;

public class RobolectricGradleTestRunner extends RobolectricTestRunner {

    public static final String TAG = RobolectricTestRunner.class.getSimpleName();
    private static final String PROJECT_DIR =
            ".";


    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC =
            Build.VERSION_CODES.JELLY_BEAN_MR2;

    public RobolectricGradleTestRunner(final Class<?> testClass) throws Exception {
        super(testClass);
    }

    private static AndroidManifest getAndroidManifest() {

        String manifestPath = PROJECT_DIR + "/src/main/AndroidManifest.xml";
        String resPath = PROJECT_DIR + "/src/main/res";
        String assetPath = PROJECT_DIR + "/src/main/assets";

        System.out.println("manifest path: " + manifestPath);
        System.out.println("resPath path: " + resPath);
        System.out.println("assetPath path: " + assetPath);

        return new AndroidManifest(
                Fs.fileFromPath(manifestPath), Fs.fileFromPath(resPath), Fs.fileFromPath(assetPath)) {
            @Override
            public int getTargetSdkVersion() {
                return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
            }
        };
    }

    private static String getProjectDirectory() {
        String path = "";
        try {
            File file = new File("..");
            path = file.getCanonicalPath();
            path = path + "/app/";
        } catch (IOException ex) {
            Log.e(TAG, "getProjectDirectory: ", ex);
        }
        return path;
    }


    public AndroidManifest getAppManifest(Config config) {
        return getAndroidManifest();
    }
}