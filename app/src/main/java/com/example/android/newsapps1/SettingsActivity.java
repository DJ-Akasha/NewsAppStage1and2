package com.example.android.newsapps1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    /** Create a {@link NewsItemPreferenceFragment} that allows the user to customize the app */
    public static class NewsItemPreferenceFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the resource layout settings_main.xml
            addPreferencesFromResource(R.xml.settings_main);

            // Updates the number of news items chosen by the user.
            Preference maxAmount = findPreference(getString(R.string.settings_max_amount_key));
            bindPreferenceSummaryToValue(maxAmount);

            // Updates the order of news items chosen by the user.
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            // Updates the news items depending on the query chosen by the user.
            Preference customizeFeed = findPreference(getString(R.string.settings_customize_feed_key));
            bindPreferenceSummaryToValue(customizeFeed);
        }

        /** The code in this method takes care of updating the displayed summary after it has
         * been changed */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        /** This method sets the current preference values into the preference summary */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
