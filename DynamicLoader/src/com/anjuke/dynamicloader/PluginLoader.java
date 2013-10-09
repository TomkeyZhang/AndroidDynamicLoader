
package com.anjuke.dynamicloader;

import java.io.File;

import android.content.Context;
import dalvik.system.DexClassLoader;

/**
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2013年10月9日
 */
public class PluginLoader {
    private Plugin plugin;
    /**
     * 插件的ClassLoader
     */
    private ClassLoader classLoader;
    /**
     * 插件apk的缓存文件
     */
    private File cachedApkFile;
    /**
     * 应用私有目录下的插件apk文件
     */
    private File apkFile;

    public PluginLoader(Plugin plugin, Context context) {
        this.plugin = plugin;
        File dex = context.getDir("dex", Context.MODE_PRIVATE);
        dex.mkdir();
        apkFile = new File(dex, plugin.getPackageName() + plugin.getVersion() + ".apk");
    }

    /**
     * 是否存在apk文件
     * 
     * @return
     */
    public boolean hasApkFile() {
        return apkFile != null && apkFile.exists();
    }

    public void createClassLoader(String optimizedDirectory, ClassLoader parent) {
        classLoader = new DexClassLoader(apkFile.getAbsolutePath(),
                optimizedDirectory, null,
                parent);
    }

    public File getApkFile() {
        return apkFile;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public File getCachedApkFile() {
        return cachedApkFile;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
