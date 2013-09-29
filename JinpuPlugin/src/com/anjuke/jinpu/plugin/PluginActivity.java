
package com.anjuke.jinpu.plugin;

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
    private String apkPath;
    private String fragmentClass;

    public static void start(Context context, String apkPath, String fragmentClass) {
        Intent intent = new Intent(context, PluginActivity.class);
        intent.putExtra(EXTRA_APK_PATH, apkPath);
        intent.putExtra(EXTRA_FRAGMENT_CLASS, fragmentClass);
        context.startActivity(intent);
    }

    private void createResAndTheme(File apkFile) {
        try {
            AssetManager am = (AssetManager) AssetManager.class
                    .newInstance();
            am.getClass().getMethod("addAssetPath", String.class)
                    .invoke(am, apkFile.getAbsolutePath());
            asm = am;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Resources superRes = super.getResources();

        res = new Resources(asm, superRes.getDisplayMetrics(),
                superRes.getConfiguration());

        thmeme = res.newTheme();
        thmeme.setTo(super.getTheme());
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
            apkPath = getIntent().getStringExtra(EXTRA_APK_PATH);
            fragmentClass = getIntent().getStringExtra(EXTRA_FRAGMENT_CLASS);
        } else {
            apkPath = bundle.getString(EXTRA_APK_PATH);
            fragmentClass = bundle.getString(EXTRA_FRAGMENT_CLASS);
        }
        createResAndTheme(new File(apkPath));
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
