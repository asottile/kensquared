package com.anthonysottile.kenken.ui;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.SettingsProvider;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class KenKenPreferences extends PreferenceActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPreferenceScreen(this.createPreferenceHierarchy());
    }
	
	private final String[] ValuesStrings = new String[] {
		"4", "5", "6", "7", "8", "9"
	};

    private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // Preferences category
        PreferenceCategory preferenceCategory = new PreferenceCategory(this);
        preferenceCategory.setTitle(R.string.preference_category);
        root.addPreference(preferenceCategory);

        // List preference
        ListPreference gameSize = new ListPreference(this);
        gameSize.setEntries(this.ValuesStrings);
        gameSize.setEntryValues(this.ValuesStrings);
        gameSize.setTitle(R.string.gameSize);
        gameSize.setDefaultValue(this.ValuesStrings[SettingsProvider.GetGameSize() - 4]);
        gameSize.setOnPreferenceChangeListener(
    			new Preference.OnPreferenceChangeListener() {
					
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						String value = newValue.toString();
						int valueAsInteger = Integer.parseInt(value, 10);
						SettingsProvider.SetGameSize(valueAsInteger);
						
						// Don't actually care about setting the persisted value here...
						return false;
					}
				}
		);
        
        preferenceCategory.addPreference(gameSize);

        return root;
    }

}
