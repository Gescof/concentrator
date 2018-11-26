package es.upm.syst.IoT.examples;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExampleMain {

	private static void imprimirMotaMeasure(MotaMeasureTraza motaMeasureTraza) {
		MotaMeasure motaMeasure = motaMeasureTraza.getMotaMeasure();
		String imprimirEsto = "timestamp: " + motaMeasure.getTimestamp().getDate().toString() + "\nMotaId: "
				+ motaMeasure.getMotaId() + "\nGeometry:\n\ttype:" + motaMeasure.getGeometry().getType()
				+ "\n\tcoordinates: " + motaMeasure.getGeometry().getCoordinates()[0] + ", "
				+ motaMeasure.getGeometry().getCoordinates()[1] + "\nMeasures:" + "\n\ttemperature: value: "
				+ motaMeasure.getMeasures().getTemperature().getValue() + " unit: "
				+ motaMeasure.getMeasures().getTemperature().getUnit() + "\n\thumidity: value: "
				+ motaMeasure.getMeasures().getHumidity().getValue() + " unit: "
				+ motaMeasure.getMeasures().getHumidity().getUnit() + "\n\tluminosity: value: "
				+ motaMeasure.getMeasures().getLuminosity().getValue() + " unit: "
				+ motaMeasure.getMeasures().getLuminosity().getUnit();

		System.out.println(imprimirEsto);
	}
	
	private static void imprimirOMCollection(ObservationCollecionTraza omTraza) {
		ObservationCollection omCollection = omTraza.getOmCollection();
		String imprimirMeasures = "";
		for (int i = 1; i < 4; i++) {
			if(i == 3) {
				imprimirMeasures += "\n\t{\n\tid: " + omCollection.getMembers().get(i).getId()
					+ "\n\ttype: " + omCollection.getMembers().get(i).getType()
					+ "\n\tresultTime: " + omCollection.getMembers().get(i).getResultTime().getDate().toString()
					+ "\n\tresult: {"
					+ "\n\t\t" + omCollection.getMembers().get(i).getResult().toStringOM()
					+ "\n\t}\n\t}";
			}
			else {
				imprimirMeasures += "\n\t{\n\tid: " + omCollection.getMembers().get(i).getId()
					+ "\n\ttype: " + omCollection.getMembers().get(i).getType()
					+ "\n\tresultTime: " + omCollection.getMembers().get(i).getResultTime().getDate().toString()
					+ "\n\tresult: {"
					+ "\n\t\t" + omCollection.getMembers().get(i).getResult().toStringOM()
					+ "\n\t}\n\t},";
			}
		}
		String imprimirEsto = "{\nid: " + omCollection.getId() + "\nphenomenomTime: "
				+ omCollection.getPhenomenomTime().getDate().toString()
				+ "\nmember: ["
				+ "\n\t{"
				+ "\n\tid: " + omCollection.getMembers().get(0).getId()
				+ "\n\ttype: " + omCollection.getMembers().get(0).getType()
				+ "\n\tresultTime: " + omCollection.getMembers().get(0).getResultTime().getDate().toString()
				+ "\n\tresult: {"
				+ "\n\t\t" + omCollection.getMembers().get(0).getResult().toStringOM()
				+ "\n\t}\n\t},"
				+ imprimirMeasures
				+ "\n]\n}";
		
		System.out.println(imprimirEsto);
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		String trazaPrueba = "{\"MotaMeasure\":{\"timestamp\":{\"$date\":\"2018-11-19T22:06:52.863Z\"},\"MotaId\":\"13\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[40.390575,-3.626924]},\"measures\":{\"temperature\":{\"value\":5.1,\"unit\":\"Cº\"},\"humidity\":{\"value\":73.3,\"unit\":\"RH\"},\"luminosity\":{\"value\":31.0,\"unit\":\"lx\"}}}}";
		ObjectMapper objectMapper = new ObjectMapper();

		MotaMeasureTraza motaMeasure = objectMapper.readValue(trazaPrueba, MotaMeasureTraza.class);		
		imprimirMotaMeasure(motaMeasure);
		System.out.println();
		
		ObservationCollecionTraza omTraza = new ObservationCollecionTraza();
		omTraza.omCollection = new ObservationCollection();
		ArrayList<OMMember> members = new ArrayList<OMMember>();
		members.add(new OMMember("geometry" + motaMeasure.MotaMeasure.getMotaId(), "Geometry Observation", motaMeasure.MotaMeasure.getTimestamp(), motaMeasure.MotaMeasure.getGeometry()));
		members.add(new OMMember("temperature" + motaMeasure.MotaMeasure.getMotaId(), "Category Observation", motaMeasure.MotaMeasure.getTimestamp(), motaMeasure.MotaMeasure.measures.getTemperature()));
		members.add(new OMMember("humidity" + motaMeasure.MotaMeasure.getMotaId(), "Category Observation", motaMeasure.MotaMeasure.getTimestamp(), motaMeasure.MotaMeasure.measures.getHumidity()));
		members.add(new OMMember("luminosity" + motaMeasure.MotaMeasure.getMotaId(), "Category Observation", motaMeasure.MotaMeasure.getTimestamp(), motaMeasure.MotaMeasure.measures.getLuminosity()));
		omTraza.omCollection.setId(motaMeasure.MotaMeasure.getMotaId());
		omTraza.omCollection.setPhenomenomTime(motaMeasure.MotaMeasure.getTimestamp());
		omTraza.omCollection.setMembers(members);
		imprimirOMCollection(omTraza);
	}
}
