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

import javax.json.Json;
import javax.json.JsonObject;

public class MotaMasterTranslator implements Translator<String, String> {

	//we remove the char 13 and 10 of the end of the message.
	//TODO data should be parsed to avoid invalid data.
	
	@Override
	 public String translate(String data) {
		String string = (String) data;
		string =  string.substring(0, string.length()-2);
		string = formatMessage(data);
		return string;
	}
	
	
	//Example of entity in MotaMeasure (v3) Ontology of Sofia2
//		{
//			  "MotaMeasure": {
//			    "timestamp": {
//			      "$date": "2014-01-30T17:14:00Z"
//			    },
//			    "MotaId": "string",
//			    "geometry": {
//			      "type": "Point",
//			      "coordinates": [
//			        9,
//			        19.3
//			      ]
//			    },
//			    "measures": {
//			      "humidity": {
//			        "value": 28.6,
//			        "unit": "string"
//			      },
//			      "temperature": {
//			        "value": 28.6,
//			        "unit": "string"
//			      },
//			      "luminosity": {
//			        "value": 28.6,
//			        "unit": "string"
//			      }
//			    }
//			  }
//			}
		
		
		
		 private JsonObject generateTimeStamp(){
			return Json.createObjectBuilder()
					.add("$date", "2014-01-30T17:14:00Z")
					.build();
		}
		
	 private String generateMotaId(){
			return  "mota1";
		} 
		
		private JsonObject generateGeometry(){
			 return Json.createObjectBuilder()
					.add("type", "Point")
					.add("coordinates", Json.createArrayBuilder()
						.add(9)
							.add(19.3))
					.build();
		} 
		
		private JsonObject generateMeasures(final Object data){
			String dataString = (String) data;
			float temperature = Float.parseFloat(dataString.substring(0, 3));
			float humidity = Float.parseFloat(dataString.substring(4,7));
			float luminosity = Float.parseFloat(dataString.substring(8,11));
			return Json.createObjectBuilder()
					.add("humidity", generateMeasure("h",humidity))
					.add("temperature", generateMeasure("t",temperature))
					.add("luminosity", generateMeasure("l",luminosity))
					.build();
		} 
		
		private JsonObject generateMeasure(String unidad, float value){
			return Json.createObjectBuilder()
					.add("value", value)
					.add("unit", unidad)
					.build();
		}
		
		private JsonObject generateMotaMeasure(final Object data){
			return Json.createObjectBuilder()
					.add("timestamp", generateTimeStamp())
					.add("MotaId", generateMotaId())
					.add("geometry", generateGeometry())
					.add("measures", generateMeasures( data))
					.build();
		}
		
	private String formatMessage(final Object data){
			JsonObject motaMeasureJSON = Json.createObjectBuilder().add("MotaMeasure", generateMotaMeasure(data)).build();
			String instanciaOntologia=motaMeasureJSON.toString();
			return instanciaOntologia;
		} 

}
