package ie.ul.shuhupdaphone.gui;

import ie.ul.shuhupdaphone.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class InformationActivity extends Activity {
	
	private TextView userName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		
		userName = (TextView) findViewById(R.id.userNameField);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		userName.setText(prefs.getString("PREF_USERNAME","No name available in SharedPreferences"));
	}
	
	

}
