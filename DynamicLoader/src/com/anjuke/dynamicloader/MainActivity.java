
package com.anjuke.dynamicloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class MainActivity extends Activity {
    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);
        AnjukeApplication app = (AnjukeApplication) getApplication();
        aq.ajax("https://raw.github.com/TomkeyZhang/AndroidDynamicLoader/master/DynamicLoader/jinpu.json",
                String.class, new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        Log.d("zqt", object);
                    }
                });
        if (app.getJinpuApp() == null) {

        }
    }

    private void startJinpu() {
        try {
            String fragmentClass = "com.anjuke.plugin.jinpu.JinpuFragment";
            Intent intent = new Intent("com.anjuke.plugin.jinpu.PluginActivity");
            intent.putExtra("path", ((AnjukeApplication) getApplication()).getF().getAbsolutePath());
            intent.putExtra("class", fragmentClass);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
