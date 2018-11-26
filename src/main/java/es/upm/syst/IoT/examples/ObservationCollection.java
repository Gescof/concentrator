package es.upm.syst.IoT.examples;

import java.util.ArrayList;

public class ObservationCollection {
	String id;
	Timestamp phenomenomTime;
	ArrayList<OMMember> members;
	
	public ObservationCollection()
	{
		//TODO--Default Constructor
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Timestamp getPhenomenomTime() {
		return phenomenomTime;
	}
	public void setPhenomenomTime(Timestamp phenomenomTime) {
		this.phenomenomTime = phenomenomTime;
	}
	
	public ArrayList<OMMember> getMembers() {
		return members;
	}
	public void setMembers(ArrayList<OMMember> members) {
		this.members = members;
	}
	
}
