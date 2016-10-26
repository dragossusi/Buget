package com.rachierudragos.fregmente;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rachierudragos.buget.R;

/**
 * Created by Dragos on 18.01.2016.
 */
public class SetariFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //nu are fab
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();
        View rootView = inflater.inflate(R.layout.fragment_setari,container,false);
        return rootView;
    }
}
