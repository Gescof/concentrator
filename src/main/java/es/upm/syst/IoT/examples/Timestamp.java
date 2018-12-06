package es.upm.syst.IoT.examples;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class Timestamp {

	private String date;

	public Timestamp()
	{
		//TODO--Default Constructor
	}
	
	@JsonGetter("$date")
	public String getDate() {
		return date;
	}
	@JsonSetter("$date")
	public void setDate(String date) {
		this.date = date;
	}
	
}
