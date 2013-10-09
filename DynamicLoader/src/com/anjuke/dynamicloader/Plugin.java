
package com.anjuke.dynamicloader;

/**
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2013年10月9日
 */
public class Plugin {
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

}
