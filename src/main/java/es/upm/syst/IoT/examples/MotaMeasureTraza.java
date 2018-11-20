package es.upm.syst.IoT.examples;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class MotaMeasureTraza {
	MotaMeasure MotaMeasure;
	
	public MotaMeasureTraza()
	{
		//SD
	}

	@JsonGetter("MotaMeasure")
	public MotaMeasure getMotaMeasure() {
		return MotaMeasure;
	}

	@JsonSetter("MotaMeasure")
	public void setMotaMeasure(MotaMeasure MotaMeasure) {
		this.MotaMeasure = MotaMeasure;
	}
	
	
}
