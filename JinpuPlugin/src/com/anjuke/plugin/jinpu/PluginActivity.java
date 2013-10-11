
package com.anjuke.plugin.jinpu;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class PluginActivity extends FragmentActivity {
    private static final String EXTRA_APK_PATH = "path";
    private static final String EXTRA_FRAGMENT_CLASS = "class";
    private AssetManager asm;
    private Resources res;
    private Theme thmeme;
    private static String apkPath;
    private String fragmentClass;

    public static Intent getLaunchIntent(Context context, String fragmentClass) {
        Intent intent = new Intent(context, PluginActivity.class);
        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass);
        intent.putExtra(EXTRA_APK_PATH, apkPath);
        return intent;
    }

    public static void setApkPath(String apkPath) {
        if (apkPath != null)
            PluginActivity.apkPath = apkPath;
    }

    /**
     * 创建插件的AssetManager，Resources和Theme
     * 
     * @param apkFile
     */
    private void createResAndTheme() {
        if (TextUtils.isEmpty(apkPath))
            return;
        try {
            File apkFile = new File(apkPath);
            AssetManager am = (AssetManager) AssetManager.class
                    .newInstance();
            am.getClass().getMethod("addAssetPath", String.class)
                    .invoke(am, apkFile.getAbsolutePath());
            asm = am;
            Resources superRes = super.getResources();

            res = new Resources(asm, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());

            thmeme = res.newTheme();
            thmeme.setTo(super.getTheme());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFragment() {
        try {
            Fragment fragment = (Fragment) getClassLoader().loadClass(
                    fragmentClass).newInstance();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.primary, fragment);
            ft.commit();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private View createRootView() {
        FrameLayout rootView = new FrameLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setId(android.R.id.primary);
        return rootView;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        if (bundle == null) {
            setApkPath(getIntent().getStringExtra(EXTRA_APK_PATH));
            fragmentClass = getIntent().getStringExtra(EXTRA_FRAGMENT_CLASS);
        } else {
            setApkPath(bundle.getString(EXTRA_APK_PATH));
            fragmentClass = bundle.getString(EXTRA_FRAGMENT_CLASS);
        }
        createResAndTheme();
        super.onCreate(bundle);
        setContentView(createRootView());
        loadFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_APK_PATH, apkPath);
        outState.putString(EXTRA_FRAGMENT_CLASS, fragmentClass);
    }

    @Override
    public AssetManager getAssets() {
        return asm == null ? super.getAssets() : asm;
    }

    @Override
    public Resources getResources() {
        return res == null ? super.getResources() : res;
    }

    @Override
    public Theme getTheme() {
        return thmeme == null ? super.getTheme() : thmeme;
    }

}
