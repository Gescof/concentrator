package es.upm.syst.IoT.examples;

public class Member extends ResulType {
	float value;
	String unit;
	
	public Member()
	{
		//TODO--Default Constructor
	}
	
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String toStringOM() {
		String string = "";
		
		string += "value: " + this.value;
		string += "\n\t\tuom: " + this.unit;
		
		return string;
	}
	
}
