
package com.anjuke.dynamicloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.androidquery.AQuery;

import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {
    private AQuery aq;
    private AnjukeApplication application;
    String apkName;
    String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);
        // application = (AnjukeApplication) getApplication();
        // aq.ajax("https://raw.github.com/TomkeyZhang/AndroidDynamicLoader/master/DynamicLoader/jinpu.json",
        // String.class, new AjaxCallback<String>() {
        // @Override
        // public void callback(String url, String object, AjaxStatus status) {
        // final App jinpuApp = JSON.parseObject(object, App.class);
        // application.setJinpuApp(jinpuApp);
        // File cachedFile = aq.getCachedFile(jinpuApp.getApkUrl());
        // Log.d("zqt", "cachedFile:" + cachedFile);
        // if (cachedFile == null) {
        // Toast.makeText(getApplicationContext(), "正在下载金铺插件apk，请稍后..",
        // Toast.LENGTH_LONG).show();
        // aq.ajax(jinpuApp.getApkUrl(), File.class, 0, new AjaxCallback<File>()
        // {
        // @Override
        // public void callback(String url, File file, AjaxStatus status) {
        // Toast.makeText(getApplicationContext(), "金铺插件apk下载完成",
        // Toast.LENGTH_SHORT).show();
        // Log.d("zqt", "file:" + file.getTotalSpace());
        // application.setJinPuLoader(file);
        // startJinpuApp(jinpuApp);
        // }
        // });
        // } else {
        // application.setJinPuLoader(cachedFile);
        // startJinpuApp(jinpuApp);
        // }
        // }
        // });

        try {
            apkName = getAssets().list("apks")[0];
            apkPath = "apks/" + apkName;
            File dex = getDir("dex", Context.MODE_PRIVATE);
            dex.mkdir();
            File f = new File(dex, apkName);
            InputStream fis = getAssets().open(apkPath);
            FileOutputStream fos = new FileOutputStream(f);
            byte[] buffer = new byte[0xFF];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fis.close();
            fos.close();

            File fo = getDir("outdex", Context.MODE_PRIVATE);
            fo.mkdir();
            DexClassLoader dcl = new DexClassLoader(f.getAbsolutePath(),
                    fo.getAbsolutePath(), null,
                    MyApplication.ORIGINAL_LOADER.getParent());
            MyApplication.CUSTOM_LOADER = dcl;
            String fragmentClass = "com.anjuke.jinpu.plugin.JinpuFragment";
            Intent intent = new Intent("com.anjuke.jinpu.plugin.PluginActivity");
            intent.putExtra("path", f.getAbsolutePath());
            intent.putExtra("class", fragmentClass);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.CUSTOM_LOADER = null;
        }
    }

    private void startJinpuApp(App app) {
        try {
            Intent intent = new Intent("com.anjuke.plugin.jinpu.PluginActivity");
            intent.putExtra("path", app.getMainFragment());
            intent.putExtra("class", app.getApkPath());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
