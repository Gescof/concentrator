package es.upm.syst.IoT.examples;

public class OMMember {	
	String id;
	String type;
	Timestamp resultTime;
	ResulType result;
	
	public OMMember()
	{
		//TODO--Default Constructor
	}
	
	public OMMember(String id, String type, Timestamp resultTime, ResulType result)
	{
		this.id = id;
		this.type = type;
		this.resultTime = resultTime;
		this.result = result;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getResultTime() {
		return resultTime;
	}
	public void setResultTime(Timestamp resultTime) {
		this.resultTime = resultTime;
	}

	public ResulType getResult() {
		return result;
	}
	public void setResult(ResulType result) {
		this.result = result;
	}
	
}
