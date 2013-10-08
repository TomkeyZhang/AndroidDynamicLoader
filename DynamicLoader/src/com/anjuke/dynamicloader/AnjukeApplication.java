
package com.anjuke.dynamicloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.androidquery.util.AQUtility;

import dalvik.system.DexClassLoader;

public class AnjukeApplication extends Application {
    private ClassLoader ORIGINAL_LOADER;
    private ClassLoader JINPU_LOADER = null;
    private String JINPU_PACKAGE_NAME = "com.anjuke.plugin.jinpu";
    private SharedPreferences preferences;
    public static String JINPU_APP = "jinpu";
    private App jinpuApp;
    String apkName;
    String apkPath;
    File f;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initOriginalLoader();
        String jinpuStr = preferences.getString(JINPU_APP, "");
        if (!TextUtils.isEmpty(jinpuStr)) {
            jinpuApp = JSON.parseObject(jinpuStr, App.class);
            setJinPuLoader(new File(jinpuApp.getCachedApkUrl()));
        }
    }

    public App getJinpuApp() {
        return jinpuApp;
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
        File dex = getDir("dex", Context.MODE_PRIVATE);
        dex.mkdir();
        File apkFile = new File(dex, JINPU_PACKAGE_NAME);
        if (!apkFile.exists()) {
            try {
                AQUtility.copy(new FileInputStream(cachedApkFile), new FileOutputStream(apkFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("zqt", apkFile.exists() + "");
        f = apkFile;
        File fo = getDir("outdex", Context.MODE_PRIVATE);
        fo.mkdir();
        DexClassLoader dcl = new DexClassLoader(apkFile.getAbsolutePath(),
                fo.getAbsolutePath(), null,
                ORIGINAL_LOADER.getParent());
        JINPU_LOADER = dcl;
    }

    public File getF() {
        return f;
    }

    class MyClassLoader extends ClassLoader {
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String className)
                throws ClassNotFoundException {
            if (className.startsWith(JINPU_PACKAGE_NAME) && JINPU_LOADER != null) {
                Log.i("classloader", "loadClass( " + className + " )");
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
