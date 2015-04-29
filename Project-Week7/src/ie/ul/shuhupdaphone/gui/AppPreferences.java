package ie.ul.shuhupdaphone.gui;

import ie.ul.shuhupdaphone.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AppPreferences extends PreferenceActivity {


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);

	}
	
}
