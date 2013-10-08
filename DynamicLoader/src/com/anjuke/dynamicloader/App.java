
package com.anjuke.dynamicloader;

import java.io.File;

import android.content.Context;

public class App {
    private String mainFragment;
    private int version;
    private String packageName;
    private String apkUrl;

    public String getMainFragment() {
        return mainFragment;
    }

    public void setMainFragment(String mainFragment) {
        this.mainFragment = mainFragment;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public File getApkFile(Context context) {
        File dex = context.getDir("dex", Context.MODE_PRIVATE);
        dex.mkdir();
        return new File(dex, packageName + version);
    }
}
