
package com.anjuke.dynamicloader;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class MainActivity extends ListActivity {
    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] {
                "金铺插件"
        }));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
            case 0:
                loadPlugin("https://raw.github.com/TomkeyZhang/AndroidDynamicLoader/master/DynamicLoader/jinpu.json");
                break;

            default:
                break;
        }
    }

    private void startJinpuPlugin(PluginLoader pluginLoader) {
        startPlugin(pluginLoader, "com.anjuke.plugin.jinpu.PluginActivity");
    }

    private void loadPlugin(String configUrl) {
        aq.ajax(configUrl,
                String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        final PluginLoader pluginLoader = new PluginLoader(JSON.parseObject(object, Plugin.class),
                                getApplicationContext());
                        if (!pluginLoader.hasApkFile()) {
                            Toast.makeText(getApplicationContext(), "正在下载插件apk，请稍后..",
                                    Toast.LENGTH_LONG).show();
                            aq.download(pluginLoader.getPlugin().getApkUrl(), pluginLoader.getApkFile(),
                                    new AjaxCallback<File>() {
                                        @Override
                                        public void callback(String url, File object, AjaxStatus status) {
                                            startJinpuPlugin(pluginLoader);
                                        }
                                    });
                        } else {
                            startJinpuPlugin(pluginLoader);
                        }
                    }
                });
    }

    private void startPlugin(PluginLoader pluginLoader, String action) {
        try {
            ((AnjukeApplication) getApplication()).addPluginLoader(pluginLoader);
            Intent intent = new Intent(action);
            intent.putExtra("path", pluginLoader.getApkFile().getAbsolutePath());
            intent.putExtra("class", pluginLoader.getPlugin().getMainFragment());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
