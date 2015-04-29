package ie.ul.shuhupdaphone.scheduler;

/* Student name: Declan Moran
 * Student id:0145076
 * Partner name:Alex Bastor-Alvarez
 * Partner id:14127202
 */

import java.text.ParseException;
import java.util.Date;

import ie.ul.shuhupdaphone.database.ModuleDatabase;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

public class SlotAlarmBroadcastReceiver extends BroadcastReceiver {

	/*
	 * DEBUG and TAG are used in conjunction with the Log class which allows you to print messages to the LogCat window. For examples, 
	 * see how they are used in below code.
	 */
	static boolean DEBUG = true;
	static String TAG = "PvdV SlotalarmBCR";
	
	private static ModuleDatabase moduleDB;
	
	

	
	private final static String RINGER_MODE = "RingerMode";
	private final static String END_TIME_DATE= "Date object end time";
	
	//I. Silence and Revert strings
	public final static String SLOT_ALARM_SILENCE_ACTION = "ie.ul.shuhupdaphone.scheduler.SILENCE_PHONE";
	public final static String SLOT_ALARM_REVERT_ACTION = "ie.ul.shuhupdaphone.scheduler.REVERT_PHONE";
	
	
	private static PendingIntent mPendingAlarmIntent = null;
	    
	@Override
	public void onReceive(final Context context, Intent intent) {
		final AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent newIntent = new Intent();
		newIntent.setAction(SLOT_ALARM_SILENCE_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			// Cover the case where the phone has just rebooted ...
			try {
				am.cancel(pendingIntent);
			} catch (Exception e) {

			} 
		} 
		else if (intent.getAction().equals(SLOT_ALARM_SILENCE_ACTION)) {

			/*
			 * Exercise III: 
			 * 	1: obtain current ringer mode (so that the phone can be reverted to this mode after the slot
			 * 	2: set new ringer mode
			 */
			//1: obtain current ringer mode (so that the phone can be reverted to this mode after the slot
			int ringerMode = this.getRingerMode(context);
			//2: set new ringer mode
			this.setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
			Bundle extras = intent.getExtras();
			if (extras != null) {
				Date endTime = new Date(extras.getLong(END_TIME_DATE));
				scheduleRevert(context, endTime, ringerMode);
			}
			
		}
		else if (intent.getAction().equals(SLOT_ALARM_REVERT_ACTION)) {
			/*
			 * Exercise V, VI: 
			 * 1: Revert to old ringer mode
			 * 2: schedule next alarm
			 */
			//1: Revert to old ringer mode
			Bundle extras = intent.getExtras();
			this.setRingerMode(context,extras.getInt(RINGER_MODE) );
			//2: schedule next alarm
			scheduleNextAlarm(context);
			
		}

	}

	
		
	public static boolean scheduleNextAlarm(Context appContext)
    {
		boolean isSuccessful = false;
		
		moduleDB = new ModuleDatabase(appContext);
		Cursor cursor = moduleDB.getNextSlot();
		if (cursor.moveToFirst()) {
			int rowId = cursor.getInt(cursor.getColumnIndex(ModuleDatabase.KEY_ID));
			
	        Intent intent = new Intent();
	        intent.setAction(SLOT_ALARM_REVERT_ACTION);
	        mPendingAlarmIntent = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	        AlarmManager mAlarmManager = (AlarmManager) appContext
					.getSystemService(Context.ALARM_SERVICE);
	        try 
	        {
	           mAlarmManager.cancel(mPendingAlarmIntent);
	        } 
	        catch (Exception e) 
	        {
	        	//do nothing
	        } 
		
	        intent = new Intent();
	        intent.setAction(SLOT_ALARM_SILENCE_ACTION);
	        intent.putExtra(END_TIME_DATE, getEndTime(cursor).getTime());
	        mPendingAlarmIntent = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	        
	        try 
	        {
	           mAlarmManager.cancel(mPendingAlarmIntent);
	        } 
	        catch (Exception e) 
	        {
	        	//do nothing
	        } 
	        finally 
	        {
	            mAlarmManager.set(AlarmManager.RTC_WAKEUP, getStartTime(cursor).getTime(), mPendingAlarmIntent);
	            if (DEBUG) Log.d(TAG, "Silence phone at: " +ModuleDatabase.defaultFormatter.format(getStartTime(cursor))+":00:00");
	            moduleDB.updateTable(rowId, true);
	            isSuccessful =  true;
	        }
		}
		moduleDB.closeDatabase();
		cursor.close();
		return isSuccessful;
    }
	
	/*
	 * Method should be called after every time the phone has been put into silence mode to revert to old setting at end of slot
	 */
	private static void scheduleRevert(Context appContext, Date endTime, int mode) {
		Intent intent = new Intent();
        intent.setAction(SLOT_ALARM_REVERT_ACTION);
        intent.putExtra(RINGER_MODE, mode);
        mPendingAlarmIntent = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager) appContext
				.getSystemService(Context.ALARM_SERVICE);
        try 
        {
           mAlarmManager.cancel(mPendingAlarmIntent);
        } 
        catch (Exception e) 
        {
        	//do nothing
        } 
        finally 
        {
        	
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, endTime.getTime(), mPendingAlarmIntent);
            if (DEBUG) Log.d(TAG, "Revert phone at " +ModuleDatabase.defaultFormatter.format(endTime)+":00:00");
        }
	}
    
    public static void tearDownAlarmManager(Context appContext)
    {
    	AlarmManager mAlarmManager = (AlarmManager) appContext
				.getSystemService(Context.ALARM_SERVICE);
        try 
        {
           mAlarmManager.cancel(mPendingAlarmIntent);
        } 
        catch (Exception e) 
        {
        } 
     }
    
    public static Date getStartTime(Cursor cursor) {
    	Date now = new Date(0);		
    	if (cursor.moveToFirst()) {
    		String formattedDate = cursor.getString(cursor.getColumnIndex(ModuleDatabase.KEY_SLOT_NEXT_OCCURENCE_COLUMN));
    		try {
				now = ModuleDatabase.defaultFormatter.parse(formattedDate);
			} catch (ParseException e) {
				// do nothing
			}
    	}
    	return now;
    }
    
    public static Date getEndTime(Cursor cursor) {
    	Date startTime = getStartTime(cursor);
    	long durationInMillis = 3600000*(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ModuleDatabase.KEY_SLOT_END_COLUMN)))-
    			Integer.parseInt(cursor.getString(cursor.getColumnIndex(ModuleDatabase.KEY_SLOT_START_COLUMN))));
    	Date endTime = new Date(startTime.getTime()+durationInMillis);
    	return endTime;
    }
   
    /*
     * II. Set the ringerMode to 'mode' 
     */
    private void setRingerMode(Context context, int mode) {
    	AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	audioManager.setRingerMode(mode);

    	
    }
    
    /*
     * II. Get the current ringer mode
     */
    private int getRingerMode(Context context) {
    	AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	return audioManager.getRingerMode();
    }

	
	

}
