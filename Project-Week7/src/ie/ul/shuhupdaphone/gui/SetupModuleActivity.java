package ie.ul.shuhupdaphone.gui;

import ie.ul.shuhupdaphone.R;
import ie.ul.shuhupdaphone.database.ModuleDatabase;
import ie.ul.shuhupdaphone.gui.Slot.CONTACT_TYPE;
import ie.ul.shuhupdaphone.gui.Slot.DAY;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class SetupModuleActivity extends Activity {

	private Button saveModuleButton;
	private Button addSlotButton;
	private TimePicker startTimePicker;
	private TimePicker endTimePicker;
	private Spinner daySpinner;
	private Spinner typeSpinner;
	private EditText moduleNameText;
	private EditText moduleCodeText;
	private Module newModule;
	private TextView scheduleView;
	private ModuleDatabase moduleDB;
	
	private String KEY_START_TIME = "START_TIME";
	private String KEY_END_TIME = "STOP_TIME"; 
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_module);
        
        saveModuleButton = (Button) findViewById(R.id.commitButton);
        addSlotButton = (Button) findViewById(R.id.addSlotButton);
        daySpinner = (Spinner) findViewById(R.id.daySpinner);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        moduleNameText = (EditText) findViewById(R.id.moduleName);
        moduleCodeText = (EditText) findViewById(R.id.moduleCode);
        scheduleView = (TextView) findViewById(R.id.scheduleView);
        moduleDB = new ModuleDatabase(getApplicationContext());
        
        ArrayAdapter<Slot.DAY> dayAdapter = new ArrayAdapter<Slot.DAY>(getApplicationContext(), R.layout.list_item, Slot.DAY.values());
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        daySpinner.setAdapter(dayAdapter);
        
        ArrayAdapter<Slot.CONTACT_TYPE> typeAdapter = new ArrayAdapter<Slot.CONTACT_TYPE>(getApplicationContext(), R.layout.list_item, Slot.CONTACT_TYPE.values());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        typeSpinner.setAdapter(typeAdapter);

        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);
	    
        if (savedInstanceState==null) {
	        startTimePicker.setCurrentHour(15);
	        startTimePicker.setCurrentMinute(0);
	        endTimePicker.setCurrentHour(16);
	        endTimePicker.setCurrentMinute(0);
        }
        
        newModule = new Module();
        
        saveModuleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * Exercise:  A module can be saved when the module name and code have been set and at least one schedule.
				 *  Save the current module if all three conditions are fulfilled
				 */
				if ((isModuleNameCompleted()) && (isModuleCodeCompleted()) && (isScheduleSet())) {
					save();
				}
			
			}
        });

        addSlotButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Slot newSlot = new Slot((DAY) daySpinner.getSelectedItem(), startTimePicker.getCurrentHour(), endTimePicker.getCurrentHour(), (CONTACT_TYPE) typeSpinner.getSelectedItem());
				newModule.addTimeSlot(newSlot);
				scheduleView.append(newModule.getTimeSlotsAsStrings(Slot.CONTACT_TYPE.ALL)[newModule.getTimeSlots(Slot.CONTACT_TYPE.ALL).length-1]+"\n");			
			}
        });
        
        startTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				/*
				 * Exercise: set end hour to 1 hour after start 
				 */
				endTimePicker.setCurrentHour(hourOfDay+1);
				
			}
		});
     }
	 
	 private boolean isModuleNameCompleted() {
		 if (moduleNameText.getText().toString().contentEquals(""))
			 return false;
		 return true;
	 }
	 
	 private boolean isModuleCodeCompleted() {
		 if (moduleCodeText.getText().toString().contentEquals(""))
			 return false;
		 return true;
	 }
	 
	 private boolean isScheduleSet() {
		 if (newModule.getNumberOfTimeSlots() > 0)
			 return true;
		 return false;
	 }
	 
	 /*
	  * For each slot a new row is added to the database with: module name, module code, slot type, slot day, slot start and slot end
	  */
	 private void save() {
		 //MainActivity.moduleList.add(newModule);
		 Slot[] slotList = newModule.getTimeSlots(CONTACT_TYPE.ALL);
		 newModule.setName(moduleNameText.getText().toString());
		 newModule.setCode(moduleCodeText.getText().toString());

		 for (int i=0; i<slotList.length; i++) {
			 moduleDB.addNewSlot(newModule.name(), newModule.code(), slotList[i].type().name(), slotList[i].day().name(), slotList[i].start().toString(), slotList[i].end().toString(), "");
		 }
		 
		 moduleDB.updateTable();
	 }
	 
	 @Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);
			/* Exercise start: add restoration of time picker info */
			startTimePicker.setCurrentHour(savedInstanceState.getInt(KEY_START_TIME, 9));
		 	endTimePicker.setCurrentHour(savedInstanceState.getInt(KEY_END_TIME, 10));
		 	/* Exercise end */
		}


	 	

		@Override
		protected void onSaveInstanceState(Bundle outState) {
			/* Exercise start: add restoration of time picker info */
			outState.putInt(KEY_START_TIME, startTimePicker.getCurrentHour());
			outState.putInt(KEY_END_TIME, endTimePicker.getCurrentHour());
		 	/* Exercise end */
			super.onSaveInstanceState(outState);
		}

	 
}
