/* Student name: 
 * Student id:
 * Partner name:
 * Partner id:
 */

package ie.ul.shuhupdaphone.gui;

import ie.ul.shuhupdaphone.R;
import ie.ul.shuhupdaphone.database.ModuleDatabase;
import ie.ul.shuhupdaphone.scheduler.SlotAlarmBroadcastReceiver;

import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
	
	Button addModuleButton;
	Button addLectureButton;
	Button addLabButton;
	Button addFullScheduleButton;
	Button viewScheduleButton;

	
	/*
	 * For now modules are stored in this moduleList. ArrayLists are ideal for this: the size does not need to be set apriori, 
	 * elements can be added (at the end or in the desired location) and removed as needed. For now this ArrayList will be 
	 * accessed from the other activities. As the MainActivities existence cannot be guaranteed it should be a static field. 
	 * Hence the keywords 'public static'.
	 */
	public static ArrayList<Module> moduleList = new ArrayList<Module>();


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        addModuleButton = (Button) findViewById(R.id.newModuleButton);
        addLectureButton = (Button) findViewById(R.id.addLectureButton);
        viewScheduleButton = (Button) findViewById(R.id.viewScheduleButton);
        addFullScheduleButton = (Button) findViewById(R.id.addFullSchedule);
        addLabButton = (Button) findViewById(R.id.addLabButton);
        
        addModuleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent addModuleActivityIntent = new Intent(getBaseContext(), SetupModuleActivity.class);
				startActivity(addModuleActivityIntent);
			}
        });
        	
/*
        addLectureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent addLectureActivityIntent = new Intent(getBaseContext(), AddLectureActivity.class);
				startActivity(addLectureActivityIntent);
			}
        });
*/
        viewScheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent viewScheduleActivityIntent = new Intent(getBaseContext(), ViewScheduleActivity.class);
				startActivity(viewScheduleActivityIntent);
			}
        });

        addFullScheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent viewScheduleActivityIntent = new Intent(getBaseContext(), AddFullScheduleActivity.class);
				startActivity(viewScheduleActivityIntent);
			}
        });
        
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myPrefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
        		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        			updateUIFromPreferences(prefs);
        		}
        });
        
        
        /* Exercise start - Add functionality to populate button with name on creation of the activity */
        updateUIFromPreferences(myPrefs);
        /* Exercise end */
        
        
    }

 
	

	 @Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
		}




		@Override
		protected void onSaveInstanceState(Bundle outState) {
		}




	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.preferences: {
				Intent prefIntent = new Intent(getBaseContext(), AppPreferences.class);
				startActivity(prefIntent);
				break;
			}
			case R.id.info: {
				Intent infoIntent = new Intent(getBaseContext(), InformationActivity.class);
				startActivity(infoIntent);
				break;
			}
			case R.id.start: {
				ModuleDatabase moduleDB = new ModuleDatabase(getApplicationContext());
				moduleDB.updateTable();
				SlotAlarmBroadcastReceiver.scheduleNextAlarm(getApplicationContext());
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void updateUIFromPreferences(SharedPreferences prefs) {
		String name = prefs.getString("PREF_USERNAME","");
		if (!name.contentEquals("")) {
			viewScheduleButton.setText("View " + name+"'s " + "Schedule");
		}
	}

    
}
