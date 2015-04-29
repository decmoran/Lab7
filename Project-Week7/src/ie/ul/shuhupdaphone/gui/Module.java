package ie.ul.shuhupdaphone.gui;

/*
 * Provides a structure to store module information: name, code and list of time slots (based on Slot class)
 */

import java.util.ArrayList;

public class Module {
	private String moduleName;
	private String moduleCode;
	private ArrayList<Slot> moduleTimeSlots = new ArrayList<Slot>();
		
	public Module(String name, String code) {
		moduleName = name;
		moduleCode = code;
	}
	
	public Module() {
	}

	public void setName(String name) {
		moduleName = name;
	}
	
	public void setCode(String code) {
		moduleCode = code;
	}

	
	/*
	 * Add slot to ArrayList of time slots in increasing order
	 */
	public void addTimeSlot(Slot newSlot) {
		int newTime = newSlot.day().ordinal()*24+newSlot.start();
		for (int i=0; i<moduleTimeSlots.size()-1;i++) {
			Slot thisSlot = moduleTimeSlots.get(i);
			if (newTime < thisSlot.day().ordinal()*24+thisSlot.start()) {
				moduleTimeSlots.add(i,newSlot);
				return;
			}
		}
		// fall through will only occur if ArrayList empty or newSlot has latest time
		moduleTimeSlots.add(newSlot);
	}
	
	public void removeTimeSlot(int index) {
		moduleTimeSlots.remove(index);
	}
	
	public String name() {
		return moduleName;
	}
	
	public String code() {
		return moduleCode;
	}
	
	/*
	 * Return time slots in string format: {MON 10:00-11:00, THU 09:00-11:00} for the type requested. If type = ALL all slots are returned
	 */
	public String[] getTimeSlotsAsStrings(Slot.CONTACT_TYPE type)
	{
		String[] timeSlots = new String[moduleTimeSlots.size()];
		for (int i=0; i<moduleTimeSlots.size(); i++) {
			Slot thisSlot = moduleTimeSlots.get(i);
			if ((thisSlot.type()==type) ||(type==Slot.CONTACT_TYPE.ALL)) {
				Integer startHour = thisSlot.start();
				Integer endHour = thisSlot.end();
				
				timeSlots[i]= thisSlot.day().name()+' '+ (startHour<10? '0':"") + startHour +":00 - " + (endHour<10? '0':"") + endHour + ":00";
			}
		}
		return timeSlots;
		
	}
	
	/*
	 * Return time slots as a list of Slots. If type = ALL all slots are returned
	 */
	public Slot[] getTimeSlots(Slot.CONTACT_TYPE type)
	{
		int requiredLength=0;
		for (int i=0; i<moduleTimeSlots.size(); i++) {
			Slot thisSlot = moduleTimeSlots.get(i);
			if ((thisSlot.type()==type) ||(type==Slot.CONTACT_TYPE.ALL)) {
				requiredLength++;
			}
		}
		Slot[] timeSlots = new Slot[requiredLength];
		for (int i=0; i<moduleTimeSlots.size(); i++) {
			Slot thisSlot = moduleTimeSlots.get(i);
			if ((thisSlot.type()==type) ||(type==Slot.CONTACT_TYPE.ALL)) {
				timeSlots[i]= thisSlot;
			}
		}
		return timeSlots;
	}
	
	public int getNumberOfTimeSlots() {
		return moduleTimeSlots.size();
	}
	
	
}
