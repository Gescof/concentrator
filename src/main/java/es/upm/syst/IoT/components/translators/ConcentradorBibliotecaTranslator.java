/*
 * #%L
 * TDManager
 * %%
 * Copyright (C) 2016 - 2017 SYST Group, Universidad Politecnica de Madrid
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package es.upm.syst.IoT.components.translators;

import java.time.Instant;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcentradorBibliotecaTranslator implements Translator<String, String>{

	private static final Logger logger = LogManager.getLogger();
	
	final private String TXT_HEADER = "<txt_ini>";
	final private String TXT_TAIL =   "<txt_end>";
	
	final private String LEC_HEADER = "<lec_ini>";
	final private String LEC_TAIL =   "<lec_end>"; 
	
	final private String MEASURE_MARKER = ";";
	final private String MARKER = "\\|";
	

	
	@Override
	public String translate(String data) {
		String msg = null;
		if (data.startsWith(TXT_HEADER)){
			msg = data.substring(TXT_HEADER.length(), data.length()-TXT_TAIL.length());
			logger.info(msg);
			return null;
		}else if (data.startsWith(LEC_HEADER)){
			msg = data.substring(LEC_HEADER.length(), data.length()-LEC_TAIL.length());
			msg = formatMessage(msg);
		}
		else logger.error("Error during the translation process. Is the received data properly formatted?");
		
		return msg;
	}
	
	//Example of entity in MotaMeasure  Ontology of Sofia2
//	{"MotaMeasure":
//	  { "timestamp":{"$date": "2014-01-30T17:14:00Z"},
//      "MotaId":"string",
//      "geometry":{"type":"Point", "coordinates":[9,19.3]},
//      "measures":{
//        "humidity":{"value":28.6,"unit":"string"},
//        "temperature":{"value":28.6,"unit":"string"},
//        "luminosity":{"value":28.6,"unit":"string"},
//        "CO2":{"value":28.6,"unit":"string"},
//        "CO":{"value":28.6,"unit":"string"}}
//    }
//	}
	
	
	private String formatMessage(final String data){
		JsonObject motaMeasureJSON = Json.createObjectBuilder().add("MotaMeasure", generateMotaMeasure(data)).build();
		String instanciaOntologia=motaMeasureJSON.toString();
		return instanciaOntologia;
	}
	
	private JsonObject generateMotaMeasure(final String data){
		String[] strings = data.split(MEASURE_MARKER);		
		return Json.createObjectBuilder()
				.add("timestamp", generateTimeStamp())
				.add("MotaId", strings[0])
				.add("geometry", generateGeometry())
				.add("measures", generateMeasures(strings))
				.build();
	}
	
	private JsonObject generateTimeStamp(){
		 
		return Json.createObjectBuilder()
				.add("$date", Instant.now().toString())
				.build();
	}
	
	private JsonObject generateGeometry(){
		return Json.createObjectBuilder()
				.add("type", "Point")
				.add("coordinates", Json.createArrayBuilder()
						.add(40.390575)
						.add(-3.626924))
				.build();
	}
	
	private JsonObject generateMeasures(String [] strings){
			
		//i = 0 is for the id of the mota
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		for(int i = 1; i < strings.length; i++){
			String[] data = strings[i].split(MARKER);
			String stringValue = data[1];
			Double value = Double.parseDouble(stringValue);
			builder.add(data[0], generateMeasure(data[2], value));
		}
		
		return builder.build();
	}
	
	private JsonObject generateMeasure(String unidad, Double value){
		return Json.createObjectBuilder()
				.add("value", value)
				.add("unit", unidad)
				.build();
	}

}
