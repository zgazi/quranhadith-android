package com.greenledge.quran;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.ListPreference;
import android.os.Bundle;
import android.view.Menu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.graphics.Color;

import com.greenledge.common.LanguagePreference;


public class SettingsFragment extends PreferenceFragment {
	
	private ListPreference mListPreference;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setBackgroundColor(Color.GRAY);
        getView().setClickable(true);
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
/*
        mListPreference = (ListPreference)  getPreferenceManager().findPreference("prefViewMode");
        mListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // insert custom code
            	return true;
            }
        });
 */
        // TODO Auto-generated method stub
        LinearLayout v = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);

        Button close = new Button(getActivity().getBaseContext());
        close.setText(android.R.string.ok);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        params.setMargins(100, 0, 0, 500);
        close.setLayoutParams(params);

        v.addView(close);

        close.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // do your code 
            	getActivity().recreate();

            }
        });

        return v;
    }
}
