package ie.ul.shuhupdaphone.gui;

import java.util.ArrayList;

import ie.ul.shuhupdaphone.R;
import ie.ul.shuhupdaphone.database.ModuleDatabase;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class AddFullScheduleActivity extends Activity {

	private Button addFullScheduleButton;
	private TextView scheduleView;
	private Spinner moduleSpinner;
	private ModuleDatabase moduleDB;
	private String[] moduleCodes;
	private String[] slotList;
	
	private CheckBox lectureCheckBox;
	private CheckBox labCheckBox;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fullschedule);

        addFullScheduleButton = (Button) findViewById(R.id.commitButton);
        scheduleView = (TextView) findViewById(R.id.scheduleView);
        moduleSpinner = (Spinner) findViewById(R.id.moduleSpinner);
        

        moduleDB = new ModuleDatabase(getBaseContext());
        //moduleCodes = moduleDB.getModuleCodes();
        
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, moduleCodes);
        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        moduleSpinner.setAdapter(moduleAdapter);
        
        moduleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//moduleDB.getSlotsForModule(moduleCodes[arg2]);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        ArrayAdapter<String> slotAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item, slotList);
        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        moduleSpinner.setAdapter(slotAdapter);
    }

	
	
}
