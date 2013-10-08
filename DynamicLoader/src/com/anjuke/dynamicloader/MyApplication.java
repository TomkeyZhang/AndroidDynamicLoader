
package com.anjuke.dynamicloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class MyApplication extends Application {
    public static ClassLoader ORIGINAL_LOADER;
    public static ClassLoader CUSTOM_LOADER = null;
    String apkName;
    String apkPath;
    File f;

    @Override
    public void onCreate() {
        super.onCreate();
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

            apkName = getAssets().list("apks")[0];
            apkPath = "apks/" + apkName;
            File dex = getDir("dex", Context.MODE_PRIVATE);
            dex.mkdir();
            f = new File(dex, apkName);
            if (!f.exists()) {
                InputStream fis = getAssets().open(apkPath);
                FileOutputStream fos = new FileOutputStream(f);
                byte[] buffer = new byte[0xFF];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fis.close();
                fos.close();
            }
            File fo = getDir("outdex", Context.MODE_PRIVATE);
            fo.mkdir();
            DexClassLoader dcl = new DexClassLoader(f.getAbsolutePath(),
                    fo.getAbsolutePath(), null,
                    MyApplication.ORIGINAL_LOADER.getParent());
            MyApplication.CUSTOM_LOADER = dcl;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (CUSTOM_LOADER != null) {
                if (className.startsWith("com.anjuke.")) {
                    Log.i("classloader", "loadClass( " + className + " )");
                }
                try {
                    Class<?> c = CUSTOM_LOADER.loadClass(className);
                    if (c != null)
                        return c;
                } catch (ClassNotFoundException e) {
                }
            }
            return super.loadClass(className);
        }
    }
}
