package ie.ul.shuhupdaphone.database;

import ie.ul.shuhupdaphone.gui.Slot;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.util.Log;

public class ModuleDatabase {
 
	boolean DEBUG = true;
	String TAG = "PvdV ModuleDB";

	
  //The index (key) column name for use in where clauses.
  public static final String KEY_ID = "_id";
  
  //The name and column index of each column in your database.
  //These should be descriptive.
  public static final String KEY_MODULE_NAME_COLUMN =  
		  "MODULE_NAME_COLUMN";
  public static final String KEY_MODULE_CODE_COLUMN =
		  "MODULE_CODE_COLUMN";
  public static final String KEY_SLOT_TYPE_COLUMN =
		  "SLOT_TYPE_COLUMN";
  public static final String KEY_SLOT_DAY_COLUMN =
		  "SLOT_DAY_COLUMN";
  public static final String KEY_SLOT_START_COLUMN =
		  "SLOT_START_COLUMN";
  public static final String KEY_SLOT_END_COLUMN =
		  "SLOT_END_COLUMN";
  public static final String KEY_SLOT_NEXT_OCCURENCE_COLUMN = 
		  "SLOT_NEXT_OCCURENCE";
  
  public static DateFormat defaultFormatter = new SimpleDateFormat("yyyy-MM-dd HH");


  // Database open/upgrade helper
  private ModuleDBOpenHelper moduleDBOpenHelper;

  public ModuleDatabase(Context context) {
    moduleDBOpenHelper = new ModuleDBOpenHelper(context, ModuleDBOpenHelper.DATABASE_NAME, null, 
                                              ModuleDBOpenHelper.DATABASE_VERSION);
    
    
    if (getModuleCodes().length==0) {
    	addNewSlot("Test name", "Test Code", "LAB", "MON", "10", "11", "");
    	addNewSlot("Test name", "Test Code", "LAB", "WED", "09", "11", "");
    	addNewSlot("Test name", "Test Code", "LAB", "WED", "11", "12", "");
    	addNewSlot("Test name", "Test Code", "LAB", "FRI", "10", "11", "");
    }
    
  }
  
  // Called when you no longer need access to the database.
  public void closeDatabase() {
    moduleDBOpenHelper.close();
  }
  
  
  /*
   * Return all module codes found in the database. Each unique module code is returned only once
   * 
   * Note the following:
   * 1. We are interested in the module codes -> only return the column KEY_MODULE_CODE_COLUMN in the result_columns
   * 2. We want all the rows -> where = null indicates we just want all rows in the database returned
   * 3. with where == null, whereArgs[] == null
   * 4. We don't need them grouped -> groupBy == null
   * 5.  
   */
  public String[] getModuleCodes() {

	  ArrayList<String> moduleArray = new ArrayList<String>();
	  String currentCode;

	  String[] result_columns = new String[] { 
	      KEY_MODULE_CODE_COLUMN }; 
	    
	  String where = null;
	    
	  String whereArgs[] = null;
	  String groupBy = null;
	  String having = null;
	  String order = null;
	    
	  SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
	  Cursor moduleList = db.query(ModuleDBOpenHelper.DATABASE_TABLE, 
	                             result_columns, where,
	                             whereArgs, groupBy, having, order);
	  
	  //Cursor uniqueList = db.rawQuery("SELECT DISTINCT " + KEY_MODULE_CODE_COLUMN +  " FROM "+ModuleDBOpenHelper.DATABASE_TABLE, null);
	  
	    
	  boolean result = moduleList.moveToFirst();
	  while (result) {
		  currentCode = moduleList.getString(moduleList.getColumnIndex(ModuleDatabase.KEY_MODULE_CODE_COLUMN));
		  if (!moduleArray.contains(currentCode)) {
			  moduleArray.add(currentCode);
			  }
		  result=moduleList.moveToNext();
		  }
	  return moduleArray.toArray(new String[moduleArray.size()]);
	  }
  
  
  /*
   * Return all the module codes that contain one or more lab sessions in an array of strings
   */
  public String[] getModulesWithLabs() {
	  ArrayList<String> moduleArray = new ArrayList<String>();
	  String currentCode;

	  String[] result_columns = new String[] { 
	      KEY_ID, KEY_MODULE_CODE_COLUMN }; 
	    
	  String where = KEY_SLOT_TYPE_COLUMN  + "= ?"  ;
	    
	  String whereArgs[] = {Slot.CONTACT_TYPE.LAB.name()};
	  String groupBy = null;
	  String having = null;
	  String order = null;
	    
	  SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
	  Cursor moduleList = db.query(ModuleDBOpenHelper.DATABASE_TABLE, 
	                             result_columns, where,
	                             whereArgs, groupBy, having, order);
	    
	  // run through resulting cursor and store all unique module codes
	  boolean result = moduleList.moveToFirst();
	  while (result) {
		  currentCode = moduleList.getString(moduleList.getColumnIndex(ModuleDatabase.KEY_MODULE_CODE_COLUMN));
		  if (!moduleArray.contains(currentCode)) {
			  moduleArray.add(currentCode);
			  }
		  result=moduleList.moveToNext();
		  }
	  return moduleArray.toArray(new String[moduleArray.size()]);
	  
  }
  
  /*
   * Return all the module codes that contain one or more lecture sessions in an array of strings
   */
  public String[] getModulesWithLectures() {
	  ArrayList<String> moduleArray = new ArrayList<String>();
	  String currentCode;

	  String[] result_columns = new String[] { 
	      KEY_ID, KEY_MODULE_CODE_COLUMN }; 
	    
	  String where = KEY_SLOT_TYPE_COLUMN  + "= ?"  ;
	    
	  String whereArgs[] = {Slot.CONTACT_TYPE.LECTURE.name()};
	  String groupBy = null;
	  String having = null;
	  String order = null;
	    
	  SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
	  Cursor moduleList = db.query(ModuleDBOpenHelper.DATABASE_TABLE, 
	                             result_columns, where,
	                             whereArgs, groupBy, having, order);
	    
	  // run through resulting cursor and store all unique module codes
	  boolean result = moduleList.moveToFirst();
	  while (result) {
		  currentCode = moduleList.getString(moduleList.getColumnIndex(ModuleDatabase.KEY_MODULE_CODE_COLUMN));
		  if (!moduleArray.contains(currentCode)) {
			  moduleArray.add(currentCode);
			  }
		  result=moduleList.moveToNext();
		  }
	  return moduleArray.toArray(new String[moduleArray.size()]);
	  
  }
  
  /*
   * get next start time for a given row. If no valid start time is found, a time in the past will be returned
   */
  public Calendar getStartTime(Integer rowId) {
	  Calendar nextStart = Calendar.getInstance();
	  nextStart.setTimeInMillis(0);
	  
	  String[] result_columns = new String[] { 
		      KEY_ID, KEY_SLOT_NEXT_OCCURENCE_COLUMN }; 
		    
		  String where = KEY_ID  + "= ?"  ;
		    
		  String whereArgs[] = {rowId.toString()};
		  String groupBy = null;
		  String having = null;
		  String order = null;
		    
		  SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
		  Cursor moduleList = db.query(ModuleDBOpenHelper.DATABASE_TABLE, 
		                             result_columns, where,
		                             whereArgs, groupBy, having, order);
		    
		  // run through resulting cursor and store all unique module codes
		  boolean result = moduleList.moveToFirst();
		  if (result)
		  {
			  String nextOccurenceString = moduleList.getString(moduleList.getColumnIndex(KEY_SLOT_NEXT_OCCURENCE_COLUMN));
			  try {
				nextStart.setTime(defaultFormatter.parse(nextOccurenceString));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
			}
		  }
		  return nextStart;
  }
  
  public Cursor getSlotsForModule(String moduleCode) {

	String[] result_columns = new String[] { 
      KEY_ID,KEY_MODULE_CODE_COLUMN, KEY_MODULE_NAME_COLUMN, KEY_SLOT_TYPE_COLUMN, KEY_SLOT_DAY_COLUMN, KEY_SLOT_START_COLUMN, KEY_SLOT_END_COLUMN }; 
    
    String where = KEY_MODULE_CODE_COLUMN + "= ?"  ;
    String whereArgs[] = {moduleCode};
    String groupBy = null;
    String having = null;
    String order = null;
    
    SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
    Cursor cursor = db.query(ModuleDBOpenHelper.DATABASE_TABLE, 
                             result_columns, where,
                             whereArgs, groupBy, having, order);
    //
    return cursor;
  }
  
  /*
   * Return all information on the slot at row rowId
   */
  public Cursor getSlotInfo(Integer rowId) {
	  String[] result_columns = new String[] { 
		      KEY_MODULE_CODE_COLUMN, KEY_SLOT_TYPE_COLUMN, KEY_SLOT_DAY_COLUMN, KEY_SLOT_START_COLUMN, KEY_SLOT_END_COLUMN, KEY_SLOT_NEXT_OCCURENCE_COLUMN }; 
		    
		    String where = KEY_ID + "= ?"  ;
		    String whereArgs[] = {rowId.toString()};
		    String groupBy = null;
		    String having = null;
		    String order = null;
		    
		    SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
		    Cursor cursor = db.query(ModuleDBOpenHelper.DATABASE_TABLE, 
		                             result_columns, where,
		                             whereArgs, groupBy, having, order);
		    //
		    return cursor;
  }
  
  /*
   * Return first slot starting after 'now'. Note that slots starting at the same time will be ignored.
   */
  public Cursor getNextSlot() {
	  Calendar now = Calendar.getInstance();
	  String nowString = defaultFormatter.format(now.getTime());
	  
	  String[] result_columns = new String[] { 
		      KEY_ID, KEY_MODULE_CODE_COLUMN, KEY_MODULE_NAME_COLUMN,  KEY_SLOT_TYPE_COLUMN, KEY_SLOT_DAY_COLUMN, KEY_SLOT_START_COLUMN, KEY_SLOT_END_COLUMN, KEY_SLOT_NEXT_OCCURENCE_COLUMN }; 
		    
		    String where = KEY_SLOT_NEXT_OCCURENCE_COLUMN + "> ?"  ;
		    String whereArgs[] = {nowString};
		    String groupBy = null;
		    String having = null;
		    String order = KEY_SLOT_NEXT_OCCURENCE_COLUMN;
		    String limit = "1";
		    boolean distinct = true;
		    
		    SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
		    Cursor cursor = db.query(distinct, ModuleDBOpenHelper.DATABASE_TABLE, 
		                             result_columns, where,
		                             whereArgs, groupBy, having, order, limit);
		    //
		    return cursor;
  }
  
  
  /*
   * Return a Cursor with all used row Ids
   */
  public Cursor getIds () {
	  String[] result_columns = new String[] { 
		      KEY_ID }; 
		    
		  String where = null;
		    
		  String whereArgs[] = null;
		  String groupBy = null;
		  String having = null;
		  String order = null;
		    
		  SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
		  return db.query(ModuleDBOpenHelper.DATABASE_TABLE, 
		                             result_columns, where,
		                             whereArgs, groupBy, having, order);
  }
  
  public void addNewSlot(String moduleName, String moduleCode, String slotType, String day, String startTime, String endTime, String nextTime) {
    // Create a new row of values to insert.
    ContentValues newValues = new ContentValues();
  
    // Assign values for each row.
    newValues.put(KEY_MODULE_NAME_COLUMN, moduleName);
    newValues.put(KEY_MODULE_CODE_COLUMN, moduleCode);
    newValues.put(KEY_SLOT_TYPE_COLUMN, slotType);
    newValues.put(KEY_SLOT_DAY_COLUMN, day);
    newValues.put(KEY_SLOT_START_COLUMN, startTime);
    newValues.put(KEY_SLOT_END_COLUMN, endTime);
    newValues.put(KEY_SLOT_NEXT_OCCURENCE_COLUMN, nextTime);
    
    // Insert the row into your table
    SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
    db.insert(ModuleDBOpenHelper.DATABASE_TABLE, null, newValues); 
  }
  
  /*
   * updateTable looks after updating the KEY_SLOT_NEXT_OCCURENCE time for row rowId.
   * This method should be run any time a slot time was retrieved from the database for scheduling
   * slot occurences are periodic with a week period so this method simply adds a full week to the scheduled time
   * if the time stored in the database for this particular slot is in the past. If forceUpdate = true, the time will be updated 
   * to the next occurence regardless.
   */
  public boolean updateTable(Integer rowId, boolean forceUpdate) {
	  
	  if (DEBUG) {
		  Cursor slotInfo = getSlotInfo(rowId);
		  if (slotInfo.moveToFirst()) {
			  Log.e(TAG, "Slot content: " + slotInfo.getString(slotInfo.getColumnIndex(KEY_MODULE_CODE_COLUMN)) +", " +
			  slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_DAY_COLUMN)) +", " +
			  slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_NEXT_OCCURENCE_COLUMN))) ;
		  }
	  }
	  
	  Calendar nextStart = getStartTime(rowId);

	  if (nextStart.getTimeInMillis()==0) {
		  Cursor slotInfo = getSlotInfo(rowId);
		  if (slotInfo.moveToFirst()) {
			  String weekDay = slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_DAY_COLUMN));
			  String startHour = slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_START_COLUMN));
			  nextStart = Calendar.getInstance();
			  nextStart.set(Calendar.DAY_OF_WEEK, (Slot.DAY.valueOf(weekDay).getValue()));
			  nextStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startHour));
		  }
		  else
			  return false;
	  }
	  
	  
	  while (nextStart.before(Calendar.getInstance()) || forceUpdate) {
		  nextStart.add(Calendar.DAY_OF_MONTH, 7);
		  forceUpdate = false;
	  }

	  
	  ContentValues values = new ContentValues();
	  String where = KEY_ID + " = ?";
	  String whereArgs[] = {rowId.toString()};
	  values.put(KEY_SLOT_NEXT_OCCURENCE_COLUMN, defaultFormatter.format(nextStart.getTime())+":00:00");
	  SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
	  if (db.update(ModuleDBOpenHelper.DATABASE_TABLE, values, where, whereArgs) == 1) {
		  if (DEBUG) {
			  Cursor slotInfo = getSlotInfo(rowId);
			  if (slotInfo.moveToFirst()) {
				  Log.e(TAG, "Slot updated: " + slotInfo.getString(slotInfo.getColumnIndex(KEY_MODULE_CODE_COLUMN)) +", " +
				  slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_DAY_COLUMN)) +", " +
				  slotInfo.getString(slotInfo.getColumnIndex(KEY_SLOT_NEXT_OCCURENCE_COLUMN))) ;
			  }
		  }
		  return true;
	  }
	  if (DEBUG) Log.e(TAG, "Database update failed");
	  return false;

  }
  
  /*
   * updateTable looks after updating the KEY_SLOT_NEXT_OCCURENCE times in the whole table.
   * This method should be run any time the full database needs scheduling
   */
  public void updateTable() {
	  Cursor idCursor = getIds();
	  boolean result = idCursor.moveToFirst();
	  while (result) {
		  updateTable(idCursor.getInt(idCursor.getColumnIndex(KEY_ID)), false);
		  result = idCursor.moveToNext();
	  }
  }
  
  public void deleteRow(int idNr) {
    // Specify a where clause that determines which row(s) to delete.
    // Specify where arguments as necessary.
    String where = KEY_ID + "=" + idNr;
    String whereArgs[] = null;
  
    // Delete the rows that match the where clause.
    SQLiteDatabase db = moduleDBOpenHelper.getWritableDatabase();
    db.delete(ModuleDBOpenHelper.DATABASE_TABLE, where, whereArgs);
  }
 
  private static class ModuleDBOpenHelper extends SQLiteOpenHelper {
  
    private static final String DATABASE_NAME = "myDatabase.db";
    private static final String DATABASE_TABLE = "Modules";
    private static final int DATABASE_VERSION = 1;
    
    // SQL Statement to create a new database.
    private static final String DATABASE_CREATE = "create table " +
      DATABASE_TABLE + " (" + KEY_ID +
      " integer primary key autoincrement, " +
      KEY_MODULE_NAME_COLUMN + " text not null, " +
      KEY_MODULE_CODE_COLUMN + " text not null, " +
      KEY_SLOT_TYPE_COLUMN + " text not null, " +
      KEY_SLOT_DAY_COLUMN + " text not null, " +
      KEY_SLOT_START_COLUMN + " time string not null, " +
      KEY_SLOT_END_COLUMN + " time string not null, " +
      KEY_SLOT_NEXT_OCCURENCE_COLUMN + " time string not null);";

    public ModuleDBOpenHelper(Context context, String name,
                      CursorFactory factory, int version) {
      super(context, name, factory, version);
    }
    
    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
    }

    // Called when there is a database version mismatch meaning that
    // the version of the database on disk needs to be upgraded to
    // the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                          int newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " +
        oldVersion + " to " +
        newVersion + ", which will destroy all old data");

      // Upgrade the existing database to conform to the new 
      // version. Multiple previous versions can be handled by 
      // comparing oldVersion and newVersion values.

      // The simplest case is to drop the old table and create a new one.
      db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
      // Create a new one.
      onCreate(db);
    }
  }
}
