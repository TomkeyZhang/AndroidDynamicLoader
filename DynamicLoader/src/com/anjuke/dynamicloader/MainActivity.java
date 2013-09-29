
package com.anjuke.dynamicloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {

    String apkName;
    String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

}
