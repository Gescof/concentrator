package es.upm.syst.IoT.examples;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class MotaMeasure {
	Timestamp timestamp;
	String MotaId;
	Geometry geometry;
	Measures measures;
	
	public MotaMeasure()
	{
		//TODO--Default Constructor
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	@JsonGetter("MotaId")
	public String getMotaId() {
		return MotaId;
	}
	@JsonSetter("MotaId")
	public void setMotaId(String motaId) {
		MotaId = motaId;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public Measures getMeasures() {
		return measures;
	}
	public void setMeasures(Measures measures) {
		this.measures = measures;
	}
	
}
