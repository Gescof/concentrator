/*
 * #%L
 * IoT xfiles
 * %%
 * Copyright (C) 2016 - 2018 SYST Research Group, Universidad Politecnica de Madrid
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

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MQTTRouter implements Router {
	
	private MqttClient client;
	static Logger logger = LogManager.getLogger();
	private static Properties properties;
	private static final String file = "MQTTBroker.properties";
	private static final String IP = "192.168.155.2";
	private static final int PORT = 10000;
	Socket socket; 
	BufferedWriter salidaTCP;


	public MQTTRouter() throws  MqttException {
		properties = new Properties();
		InputStream iStream = MQTTRouter.class.getClassLoader().getResourceAsStream(file);
		try {
			properties.load(iStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (connect("00000000000000000000")) {
			logger.info("MQTT Client connected to the broker succesfully at " + properties.getProperty("url"));
			client.disconnect();
		}
		
		try {
			//CONNECT TCP
			socket = new Socket(InetAddress.getByName(IP),PORT);
			salidaTCP  = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		client.disconnect();
	}

	@Override
	public void send(Object data) {
		if(isJSONValid(data.toString())) {
			try{
				JSONObject message;
				if(data.toString().startsWith("[")){
					JSONArray jsonArr = new JSONArray (data.toString());
					for (int i = 0; i < jsonArr.length(); i++)
			        {
			           message = jsonArr.getJSONObject(i);	           
			           sendToThingspeak(message);
			        }
				}
				else if(data.toString().startsWith("{")){
					message = new JSONObject(data.toString());
					sendToThingspeak(message);
				}
			} catch (Throwable e) {
			    logger.error("Uncaught exception", e); // prints the stack trace.
			}
		} else logger.info("Bad format message: "+data.toString());		
	}

	private boolean connect(String user) {
		MqttConnectOptions conOpt = new MqttConnectOptions();
		conOpt.setCleanSession(false);
		conOpt.setUserName(user);
		try {
			client = new MqttClient(properties.getProperty("url"), // URI
									properties.getProperty("clientID"), // ClientId
									new MemoryPersistence());// Persistence
			client.connect(conOpt);
		} catch (MqttException e) {
			logger.error("Error connecting to MQTT Broker with user " + user+ " " +  e.toString());
		}
		return client.isConnected();
	}

	private void sendToThingspeak(JSONObject message){
		try{
			if (connect(message.getString("token"))) {
				
				String aux = "{\"Temperature\":" 	+ message.getString("Temperature") 
							 + ", \"Humidity\":" 	+ message.getString("Humidity") 
							 + ", \"Luminosity\":"	+ message.getString("Luminosity") 
							 + ", \"Obstacle\": "	+ String.valueOf(message.getBoolean("Obstacle")) + "}";
				client.publish("v1/devices/me/telemetry", aux.getBytes("UTF-8") ,2, false);
				logger.info(aux);
				
				salidaTCP.write(message.toString());
				salidaTCP.newLine();
				salidaTCP.flush();
				
			} else
				throw new MqttException(0);

		} catch (UnsupportedEncodingException | MqttException e) {
			logger.error("Error while sending message to the broker");
		} catch (JsonParsingException e) {
			logger.error("Error while parsing the message from the mote");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error while sending the message to the Server");
		}

		
	}
	

	private boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        // edited, to include @Arthur's comment
	        // e.g. in case JSONArray is valid as well...
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
}
