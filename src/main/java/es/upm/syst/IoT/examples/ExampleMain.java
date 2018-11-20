package es.upm.syst.IoT.examples;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExampleMain {
	
	public static void ImprimirMotaMeasure(MotaMeasureTraza motaMeasureTraza)
	{
		MotaMeasure motaMeasure = motaMeasureTraza.getMotaMeasure();
		String imprimirEsto = "timestamp: " + motaMeasure.getTimestamp().getDate().toString()
				+ "\nMotaId: " + motaMeasure.getMotaId()
		+ "\nGeometry:\n\ttype:" + motaMeasure.getGeometry().getType() + "\n\tcoordinates: " +  motaMeasure.getGeometry().getCoordinates()[0] + ", " + motaMeasure.getGeometry().getCoordinates()[1]
		+ "\nMeasures:" 
		+ "\n\ttemperature: value: " + motaMeasure.getMeasures().getTemperature().getValue() + " unit: " + motaMeasure.getMeasures().getTemperature().getUnit()
		+ "\n\thumidity: value: " + motaMeasure.getMeasures().getHumidity().getValue() + " unit: " + motaMeasure.getMeasures().getHumidity().getUnit()
		+ "\n\tluminosity: value: " + motaMeasure.getMeasures().getLuminosity().getValue() + " unit: " + motaMeasure.getMeasures().getLuminosity().getUnit();
		
		System.out.println(imprimirEsto);
		
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		String trazaPrueba = "{\"MotaMeasure\":{\"timestamp\":{\"$date\":\"2018-11-19T22:06:52.863Z\"},\"MotaId\":\"13\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[40.390575,-3.626924]},\"measures\":{\"temperature\":{\"value\":5.1,\"unit\":\"Cº\"},\"humidity\":{\"value\":73.3,\"unit\":\"RH\"},\"luminosity\":{\"value\":31.0,\"unit\":\"lx\"}}}}";
		ObjectMapper objectMapper = new ObjectMapper();
		
		MotaMeasureTraza motaMeasure = objectMapper.readValue(trazaPrueba, MotaMeasureTraza.class);
		
		ImprimirMotaMeasure(motaMeasure);
		
	}

}
