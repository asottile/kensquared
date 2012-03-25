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
	
	private ListPreference gameSize = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setPreferenceScreen(this.createPreferenceHierarchy());
    }
	
	private static final String[] ValuesStrings = new String[] {
		"4", "5", "6", "7", "8", "9"
	};
	
    private PreferenceScreen createPreferenceHierarchy() {
    	
    	final KenKenPreferences self = this;
    	
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

        // Preferences category
        PreferenceCategory preferenceCategory = new PreferenceCategory(this);
        preferenceCategory.setTitle(R.string.preference_category);
        root.addPreference(preferenceCategory);

        // List preference
        this.gameSize = new ListPreference(this);
        this.gameSize.setEntries(KenKenPreferences.ValuesStrings);
        this.gameSize.setEntryValues(KenKenPreferences.ValuesStrings);
        this.gameSize.setTitle(R.string.gameSize);
        int index = SettingsProvider.GetGameSize() - UIConstants.MinimumGameSize;
        this.gameSize.setDefaultValue(KenKenPreferences.ValuesStrings[index]);
        this.gameSize.setOnPreferenceChangeListener(
			new Preference.OnPreferenceChangeListener() {
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					String value = newValue.toString();
					int valueAsInteger = Integer.parseInt(value, 10);
					SettingsProvider.SetGameSize(valueAsInteger);
					
					// Set the value manually since we don't want to persist the actual
					//  setting.  The reason we avoid persisting the setting is we are
					//  managing the setting ourselves in the SettingsProvider class.
					self.gameSize.setValue((String)newValue);
					
					// Don't actually care about setting the persisted value here...
					return false;
				}
			}
		);
        
        preferenceCategory.addPreference(this.gameSize);

        return root;
    }
}
