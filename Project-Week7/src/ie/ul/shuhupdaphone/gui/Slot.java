package ie.ul.shuhupdaphone.gui;

/*
 *	Timeslot information: start and stop time (stored as int), day and CONTACT_TYPE  
 */

public class Slot {
	public enum DAY {SUN(1), MON(2), TUE(3), WED(4), THU(5), FRI(6), SAT(7); 
	final int numOfDay;
	 private DAY(int num){
	        this.numOfDay= num;
	    }
	public int getValue(){
        return this.numOfDay;
    }};
	public enum CONTACT_TYPE {LECTURE, LAB, TUTORIAL, STUDYTIME, ALL};
	
	private DAY day;
	private Integer startHour;
	private Integer endHour;
	private CONTACT_TYPE type;
	
	public Slot (DAY slotDay, int slotStart, int slotEnd, CONTACT_TYPE slotType) {
		day = slotDay;
		startHour = slotStart;
		endHour = slotEnd;
		type = slotType;
	}

	public DAY day() {
		return day;
	}
	
	public Integer start() {
		return startHour;
	}
	
	public Integer end() {
		return endHour;
	}
	
	public CONTACT_TYPE type() {
		return type;
	}
	
}
