package edu.milton.miltonmobileandroid.saa;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.milton.miltonmobileandroid.R;

/**
 * Created by ravi_000 on 11/8/2014.
 */
public class SaaFragment extends Fragment {

    public SaaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saa, container, false);
        return rootView;
    }
}
