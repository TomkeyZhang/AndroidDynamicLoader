
package com.anjuke.dynamicloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            String fragmentClass = "com.anjuke.plugin.jinpu.JinpuFragment";
            Intent intent = new Intent("com.anjuke.plugin.jinpu.PluginActivity");
            intent.putExtra("path", ((MyApplication) getApplication()).getF().getAbsolutePath());
            intent.putExtra("class", fragmentClass);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
