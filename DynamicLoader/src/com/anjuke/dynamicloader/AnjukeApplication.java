
package com.anjuke.dynamicloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;

/**
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2013年10月9日
 */
public class AnjukeApplication extends Application {
    private ClassLoader ORIGINAL_LOADER;
    private List<PluginLoader> pluginLoaders = new ArrayList<PluginLoader>();
    private String optimizedDirectory;

    @Override
    public void onCreate() {
        super.onCreate();
        initOriginalLoader();
        File fo = getDir("outdex", Context.MODE_PRIVATE);
        fo.mkdir();
        optimizedDirectory = fo.getAbsolutePath();
    }

    /**
     * 添加一个插件加载器
     * 
     * @param pluginLoader
     */
    public void addPluginLoader(PluginLoader pluginLoader) {
        pluginLoader.createClassLoader(optimizedDirectory, ORIGINAL_LOADER.getParent());
        pluginLoaders.add(pluginLoader);
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

            AnjukeClassLoader cl = new AnjukeClassLoader(mClassLoader);
            sClassLoader.set(cl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public void setPluginLoader(Plugin plugin) {
    // File dex = getDir("dex", Context.MODE_PRIVATE);
    // dex.mkdir();
    // File apkFile = new File(dex, jinpuApp.getPackageName() +
    // jinpuApp.getVersion() + ".apk");
    // if (!apkFile.exists()) {
    // try {
    // AQUtility.copy(new FileInputStream(cachedApkFile), new
    // FileOutputStream(apkFile));
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // JINPU_LOADER = new DexClassLoader(apkFile.getAbsolutePath(),
    // fo.getAbsolutePath(), null,
    // ORIGINAL_LOADER.getParent());
    // }

    private class AnjukeClassLoader extends ClassLoader {
        public AnjukeClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String className)
                throws ClassNotFoundException {
            if (className.startsWith("com.anjuke.plugin")) {
                for (PluginLoader pluginLoader : pluginLoaders) {
                    if (pluginLoader.getClassLoader() != null
                            && className.startsWith(pluginLoader.getPlugin().getPackageName())) {
                        try {
                            Class<?> c = pluginLoader.getClassLoader().loadClass(className);
                            if (c != null)
                                return c;
                        } catch (ClassNotFoundException e) {
                        }
                    }
                }
            }
            return super.loadClass(className);
        }
    }
}
