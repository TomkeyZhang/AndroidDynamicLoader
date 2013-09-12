
package com.sample.anjuke;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dianping.loader.MyResources;

public class AnjukeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // MyResources res = MyResources.getResource(AnjukeFragment.class);
        // return inflater.inflate(R.layout.fragment_anjuke, null);
        return MyResources.getResource(AnjukeFragment.class).inflate(getActivity(), R.layout.fragment_anjuke,
                container, false);
    }
}
