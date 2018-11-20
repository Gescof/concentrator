package es.upm.syst.IoT.examples;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class Timestamp {

	private Date date;

	public Timestamp()
	{
		//XD
	}
	
	@JsonGetter("$date")
	public Date getDate() {
		return date;
	}

	@JsonSetter("$date")
	public void setDate(Date date) {
		this.date = date;
	}
	
}
