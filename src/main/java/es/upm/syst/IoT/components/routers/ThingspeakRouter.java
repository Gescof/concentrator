/*
 * #%L
 * TDManager
 * %%
 * Copyright (C) 2016 SYST Group, Universidad Politecnica de Madrid
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
package es.upm.syst.IoT.components.routers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import es.upm.syst.IoT.concentrator.Concentrator;

import javax.net.ssl.HttpsURLConnection;

/* NOTA IMPORTANTE: Esta clase fue desarrollada por los estudiantes búlgaros y aunque funciona no está optimizada
 * Hay que refactorizarla llevando cosas del método send() a un constructor y revisar que todas las conexiones se cierran correctamente.
 */

public class ThingspeakRouter<O> implements Router<O> {
	
	static Logger logger = LogManager.getLogger(Concentrator.class.getName());
	
	@Override
	public void send(O data) {	 
		try{
			 while(true){
			    JSONObject rawResponseObject = new JSONObject(data.toString()); // raw data parsed to a object
		        ArrayList<Object> updates = new ArrayList<Object>(); // creating an object array
			          
			    // GET timeStamp dynamic value
			    JSONObject timestampValueObject = rawResponseObject.getJSONObject("MotaMeasure").getJSONObject("timestamp");	  
		
			    // GET Humidity dynamic value
			    JSONObject humidityValueObject = rawResponseObject.getJSONObject("MotaMeasure").getJSONObject("measures").getJSONObject("humidity");
			    
			    // GET Luminosity dynamic value
			    JSONObject luminosityValueObject = rawResponseObject.getJSONObject("MotaMeasure").getJSONObject("measures").getJSONObject("luminosity");			  
			    
			   // GET Temperature dynamic value
			    JSONObject measuresValueObject = rawResponseObject.getJSONObject("MotaMeasure").getJSONObject("measures").getJSONObject("temperature");
			 			    		    		    
			    // START OF temperature & timeStamp & humidity & luminosity dynamic implementation
			    measuresValueObject.remove("unit");
			    measuresValueObject.put("field2", measuresValueObject.get("value").toString());
			    measuresValueObject.remove("value");
			    measuresValueObject.put("created_at", timestampValueObject.get("$date"));
			   
			    humidityValueObject.remove("unit");
			    measuresValueObject.put("field3", humidityValueObject.get("value").toString());
			  
			    luminosityValueObject.remove("unit");
			    measuresValueObject.put("field4", luminosityValueObject.get("value").toString());		    
			    // END OF temperature & timeStamp & humidity & luminosity dynamic implementation
			    
			    updates.add(measuresValueObject); // adding temp & timestamp keys to the Array
			 
			    JSONObject mainObject = new JSONObject();
			    mainObject.put("updates", updates );
			    mainObject.put("write_api_key","ZYYRR8IWODO81IDO");   		
			    
			    String url = "https://api.thingspeak.com/channels/467910/bulk_update.json";
				URL obj = new URL(url);
				HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
				
				//add request header
				con.setRequestMethod("POST");
				con.addRequestProperty("Content-Type", "application/json");
						
				// Send post request
				con.setDoOutput(true);
				
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				wr.writeBytes(mainObject.toString());
				wr.close();
				
				//TODO Falta revisar este cacho de codigo para ver si es necesario que salga todo esto por pantalla en el logger
		
				int responseCode = con.getResponseCode();
				logger.info("\nSending 'POST' request to URL : " + url);
				logger.info("Post parameters : " + mainObject);
				logger.info("Response Code : " + responseCode);
		
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
		
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				logger.info(response.toString());
				Thread.sleep(20000);
			} }
			
			 catch (Exception e){};
   
	}	

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
 