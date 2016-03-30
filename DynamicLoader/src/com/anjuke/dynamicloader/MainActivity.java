package com.anjuke.dynamicloader;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

public class MainActivity extends ListActivity {
	private AQuery aq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aq = new AQuery(this);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] { "金铺插件",
				"好租插件" }));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:
			loadPlugin("https://raw.github.com/TomkeyZhang/AndroidDynamicLoader/master/DynamicLoader/jinpu.json",
					"com.anjuke.plugin.jinpu.PluginActivity");
			break;
		case 1:
			loadPlugin("https://raw.github.com/TomkeyZhang/AndroidDynamicLoader/master/DynamicLoader/haozu.json",
					"com.anjuke.plugin.haozu.PluginActivity");
			break;

		default:
			break;
		}
	}

	/**
	 * 加载并启动插件
	 * 
	 * @param configUrl
	 *            插件配置信息
	 * @param action
	 *            启动插件隐式Intent的Action
	 */
	private void loadPlugin(final String configUrl, final String action) {
		Log.d("zqt", "loadPlugin=" + configUrl + " action=" + action);
		new Thread() {
			public void run() {
				Plugin plugin=new Plugin();
				plugin.setApkUrl("http://7xozsw.dl1.z0.glb.clouddn.com/JinpuPlugin.apk");
				plugin.setMainFragment("com.anjuke.plugin.jinpu.fragment.MainFragment");
				plugin.setPackageName("com.anjuke.plugin.jinpu");
				plugin.setVersion(1);
//				String body = HttpRequest.get(configUrl).body();
				final PluginLoader pluginLoader = new PluginLoader(plugin,
						getApplicationContext());
				if (!pluginLoader.hasApkFile()) {
					Log.d("zqt", "正在下载插件apk，请稍后..");
					HttpRequest.get(pluginLoader.getPlugin().getApkUrl()).receive(pluginLoader.getApkFile());
				}
				startPluginInUIThread(pluginLoader, action);
			};
		}.start();
	}

	private void startPluginInUIThread(final PluginLoader pluginLoader, final String action) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				startPlugin(pluginLoader, action);
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
