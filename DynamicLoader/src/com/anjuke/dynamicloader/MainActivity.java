
package com.anjuke.dynamicloader;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class MainActivity extends Activity {
    private AQuery aq;
    private AnjukeApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);
        application = (AnjukeApplication) getApplication();
        aq.ajax("https://raw.github.com/TomkeyZhang/AndroidDynamicLoader/master/DynamicLoader/jinpu.json",
                String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        final App jinpuApp = JSON.parseObject(object, App.class);
                        application.setJinpuApp(jinpuApp);
                        File cachedFile = aq.getCachedFile(jinpuApp.getApkUrl());
                        Log.d("zqt", "cachedFile:" + cachedFile);
                        if (cachedFile == null) {
                            Toast.makeText(getApplicationContext(), "正在下载金铺插件apk，请稍后..", Toast.LENGTH_LONG).show();
                            aq.ajax(jinpuApp.getApkUrl(), File.class, 0, new AjaxCallback<File>() {
                                @Override
                                public void callback(String url, File file, AjaxStatus status) {
                                    Toast.makeText(getApplicationContext(), "金铺插件apk下载完成", Toast.LENGTH_SHORT).show();
                                    application.setJinPuLoader(file);
                                    startJinpuApp(jinpuApp);
                                }
                            });
                        } else {
                            application.setJinPuLoader(cachedFile);
                            startJinpuApp(jinpuApp);
                        }
                    }
                });
    }

    private void startJinpuApp(App app) {
        try {
            Intent intent = new Intent("com.anjuke.plugin.jinpu.PluginActivity");
            intent.putExtra("path", app.getMainFragment());
            intent.putExtra("class", app.getApkFile(application).getAbsolutePath());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
