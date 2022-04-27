package org.kashmirworldfoundation.WildlifeGeoSnap.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.kashmirworldfoundation.WildlifeGeoSnap.MapCreateList;
import org.kashmirworldfoundation.WildlifeGeoSnap.R;

public class AboutFragment extends Fragment {
    private View AboutFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AboutFragment=inflater.inflate(R.layout.fragment_about, container, false);

        return AboutFragment;
    }

}
