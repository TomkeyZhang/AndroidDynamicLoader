
package com.anjuke.dynamicloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.androidquery.util.AQUtility;

import dalvik.system.DexClassLoader;

public class AnjukeApplication extends Application {
    private ClassLoader ORIGINAL_LOADER;
    private ClassLoader JINPU_LOADER = null;
    // private String JINPU_PACKAGE_NAME = "com.anjuke.plugin.jinpu";
    // private SharedPreferences preferences;
    public static String JINPU_APP = "jinpu";
    private App jinpuApp;
    String apkName;
    String apkPath;
    File f;

    @Override
    public void onCreate() {
        super.onCreate();
        // preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initOriginalLoader();
        // String jinpuStr = preferences.getString(JINPU_APP, "");
        // if (!TextUtils.isEmpty(jinpuStr)) {
        // jinpuApp = JSON.parseObject(jinpuStr, App.class);
        // setJinPuLoader(new File(jinpuApp.getCachedApkPath()));
        // }
    }

    public App getJinpuApp() {
        return jinpuApp;
    }

    public void setJinpuApp(App jinpuApp) {
        this.jinpuApp = jinpuApp;
    }

    private void initOriginalLoader() {
        try {
            Context mBase = new Smith<Context>(this, "mBase").get();

            Object mPackageInfo = new Smith<Object>(mBase, "mPackageInfo")
                    .get();

            Smith<ClassLoader> sClassLoader = new Smith<ClassLoader>(
                    mPackageInfo, "mClassLoader");
            ClassLoader mClassLoader = sClassLoader.get();
            ORIGINAL_LOADER = mClassLoader;

            MyClassLoader cl = new MyClassLoader(mClassLoader);
            sClassLoader.set(cl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setJinPuLoader(File cachedApkFile) {
        File apkFile = jinpuApp.getApkFile(this);
        if (!apkFile.exists()) {
            try {
                AQUtility.copy(new FileInputStream(cachedApkFile), new FileOutputStream(apkFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File fo = getDir("outdex", Context.MODE_PRIVATE);
        fo.mkdir();
        DexClassLoader dcl = new DexClassLoader(apkFile.getAbsolutePath(),
                fo.getAbsolutePath(), null,
                ORIGINAL_LOADER.getParent());
        JINPU_LOADER = dcl;
    }

    class MyClassLoader extends ClassLoader {
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String className)
                throws ClassNotFoundException {
            if (JINPU_LOADER != null && className.startsWith(jinpuApp.getPackageName())) {
                Log.i("zqt", "loadClass( " + className + " )");
                try {
                    Class<?> c = JINPU_LOADER.loadClass(className);
                    if (c != null)
                        return c;
                } catch (ClassNotFoundException e) {
                }
            }
            return super.loadClass(className);
        }
    }
}
