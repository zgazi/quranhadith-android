package com.greenledge.common;

/*
 * Copyright (C) 2013 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.Toast;

public class LanguagePreference extends ListPreference {

	private Context context;

	private String oldValue;

	public LanguagePreference(Context context, AttributeSet attributes) {
		super(context, attributes);
		this.context = context;
        setEntries(entries());
        setEntryValues(entryValues());
        setValueIndex(initializeIndex());
        setSummary(MainApplication.LANG);
	}

	public LanguagePreference (Context context) {
        this(context, null);
    }

	@Override
	public void setValue(String value) {
		super.setValue(value);

		if (oldValue != null && !value.equalsIgnoreCase(oldValue) ) {
			Toast.makeText(context, android.R.string.dialog_alert_title + " - " + value, Toast.LENGTH_LONG).show();
			//app.setLanguage(value);
		}
	}


    private CharSequence[] entries() {
        //action to provide entry data in char sequence array for list
        ArrayList<String> listItems = new ArrayList<String>();
		//listItems.add(Locale.getDefault().getDisplayName());
			for (Locale locale : Locale.getAvailableLocales()) {
				final String country = locale.getCountry().toLowerCase();
				if (country != null && country.length() > 0) {
					listItems.add(locale.getDisplayName());
				}
				oldValue = country;
			}

		final CharSequence[] entries = listItems.toArray(new CharSequence[listItems.size()]);


        return entries;
    }

    private CharSequence[] entryValues() {
        //action to provide value data for list

    	//action to provide entry data in char sequence array for list
        ArrayList<String> listItems = new ArrayList<String>();
		//listItems.add(Locale.getDefault().getLanguage());
			for (Locale locale : Locale.getAvailableLocales()) {
				final String country = locale.getCountry().toLowerCase();
				if (country != null && country.length() > 0 ) {
					listItems.add(locale.getLanguage());
				}
			}

		final CharSequence[] entryValues = listItems.toArray(new CharSequence[listItems.size()]);
        return entryValues;
   }

   private int initializeIndex() {
        //here you can provide the value to set (typically retrieved from the SharedPreferences)
        //...

        int i = 2;
        return i;
    }


}
