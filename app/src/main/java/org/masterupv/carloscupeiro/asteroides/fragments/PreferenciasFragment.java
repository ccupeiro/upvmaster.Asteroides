package org.masterupv.carloscupeiro.asteroides.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.masterupv.carloscupeiro.asteroides.R;

/**
 * Created by carlos.cupeiro on 29/09/2016.
 */

public class PreferenciasFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mis_preferencias);

    }
}
