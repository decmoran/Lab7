package ie.ul.shuhupdaphone.gui;


import ie.ul.shuhupdaphone.R;
import ie.ul.shuhupdaphone.database.ModuleDatabase;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ViewScheduleActivity extends Activity {

	private TextView scheduleView;
	private Spinner moduleSpinner;
	private ModuleDatabase moduleDB;
	private String[] moduleCodes = {};
	private String[] emptyStringArray = {};
	private CheckBox lectureCheckBox;
	private CheckBox labCheckBox;
	private ArrayAdapter<String> moduleAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        scheduleView = (TextView) findViewById(R.id.scheduleView);
        moduleSpinner = (Spinner) findViewById(R.id.moduleSpinner);
        lectureCheckBox = (CheckBox) findViewById(R.id.lectureCheckBox);
        labCheckBox = (CheckBox) findViewById(R.id.labCheckBox);

        moduleDB = new ModuleDatabase(getBaseContext());
 
        
        moduleAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, moduleCodes);
        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        moduleSpinner.setAdapter(moduleAdapter);
        
        lectureCheckBox.setOnCheckedChangeListener(checkBoxListener);
        labCheckBox.setOnCheckedChangeListener(checkBoxListener);
        
        moduleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (moduleCodes.length>position) {
					Cursor slotCursor = moduleDB.getSlotsForModule(moduleAdapter.getItem(position));
					populateView(slotCursor);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
     }


	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		moduleDB.closeDatabase();
	}




	/*
	 * Call this function to populate the TextView with the data in this cursor 
	 */
	private void populateView(Cursor dataCursor) {
		String code;
		String name;
		String type;
		String day;
		String start;
		String end;
		boolean result = dataCursor.moveToFirst();
		if (result==true) {
			code = dataCursor.getString(dataCursor.getColumnIndex(ModuleDatabase.KEY_MODULE_CODE_COLUMN));
			name = dataCursor.getString(dataCursor.getColumnIndex(ModuleDatabase.KEY_MODULE_NAME_COLUMN));
			scheduleView.append("Module info for " +code +" - " +name.toUpperCase()+":\n");
			while (result) {
				type = dataCursor.getString(dataCursor.getColumnIndex(ModuleDatabase.KEY_SLOT_TYPE_COLUMN));
				day = dataCursor.getString(dataCursor.getColumnIndex(ModuleDatabase.KEY_SLOT_DAY_COLUMN));
				start = dataCursor.getString(dataCursor.getColumnIndex(ModuleDatabase.KEY_SLOT_START_COLUMN));
				end = dataCursor.getString(dataCursor.getColumnIndex(ModuleDatabase.KEY_SLOT_END_COLUMN));
				scheduleView.append(""+ type + " on " +day+ " from " +start + " to " + end +"\n");
				result=dataCursor.moveToNext(); 
			}
		}
		
		
	}
	
	OnCheckedChangeListener checkBoxListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if (lectureCheckBox.isChecked()&labCheckBox.isChecked()) {
				// Exercise 10 start
				 moduleCodes = moduleDB.getModuleCodes();
			}
			else if (lectureCheckBox.isChecked()) {
				moduleCodes = moduleDB.getModulesWithLectures();
			}
			else if (labCheckBox.isChecked()) {
				moduleCodes = moduleDB.getModulesWithLabs();
				 //Exercise 10 end
			}
			else moduleCodes = emptyStringArray;
		
			moduleAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, moduleCodes);
	        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
	        moduleSpinner.setAdapter(moduleAdapter);

		}
    	
    };


}
