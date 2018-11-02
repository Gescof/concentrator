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
package es.upm.syst.IoT.concentrator;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.upm.syst.IoT.components.BasicMonitor;
import es.upm.syst.IoT.components.Monitor;
import es.upm.syst.IoT.components.filters.*;
import es.upm.syst.IoT.components.listeners.*;
import es.upm.syst.IoT.components.routers.*;
import es.upm.syst.IoT.components.translators.*;

public class Concentrator {

	static Logger logger = LogManager.getLogger(Concentrator.class.getName());

	private static Properties properties;
	private static final String file = "Configuration.properties";
	
	private static Listener listener;
	private static Filter<String> filter;
	private static Translator<String, String> translator;
	private static Router<String> router;

	public static void main(String... args) throws Exception {

		logger.info("Executing non-simulation Concentrator");

		properties = new Properties();
		InputStream iStream = RaspberryPiUARTListenerFixedSize.class.getClassLoader().getResourceAsStream(file);
		properties.load(iStream);
		String listenerConf = properties.getProperty("listener");
		String filterConf = properties.getProperty("filter");
		String translatorConf = properties.getProperty("translator");
		String routerConf = properties.getProperty("router");
		
		
		
		if(listenerConf.compareTo("MockConcentradorBiblioteca")==0) listener = new MockConcentradorBiblioteca(1000);
		else if(listenerConf.compareTo("MockMotaMasterListener")==0) listener = new MockMotaMasterListener(1000);
		else if(listenerConf.compareTo("RaspberryPiUARTListenerFixedSize")==0) listener = new RaspberryPiUARTListenerFixedSize(0);
		else if(listenerConf.compareTo("RaspberryPiUARTListenerHeaders")==0) listener = new RaspberryPiUARTListenerHeaders(0);
		else if(listenerConf.compareTo("MockArduinoHector")==0) listener = new MockArduinoHector(1000);
		else if(listenerConf.compareTo("RaspiUART")==0) listener = new RaspiUART(1000);
		else logger.error("Listener not defined in Configuration File");
		
		if(filterConf.compareTo("NoFilter")==0) filter = new NoFilter<String>();
		else if(filterConf.compareTo("JSONFilter")==0) filter = new JSONFilter<String>();
		else logger.error("Filter not defined in Configuration File");
		
		if(translatorConf.compareTo("ConcentradorBibliotecaTranslator")==0) translator = new ConcentradorBibliotecaTranslator();
		else if(translatorConf.compareTo("MotaMasterTranslator")==0) translator = new MotaMasterTranslator();
		else if(translatorConf.compareTo("NoTranslator")==0) translator = new NoTranslator();
		else logger.error("Translator not defined in Configuration File");
		
		if(routerConf.compareTo("ThingspeakRouter")==0) router = new ThingspeakRouter();
		else if(routerConf.compareTo("MQTTRouter")==0) router = new MQTTRouter();
		else if(routerConf.compareTo("Sofia2Router")==0) router = new Sofia2Router();
		else if(routerConf.compareTo("ConsoleRouter")==0) router = new ConsoleRouter();
		else logger.error("Translator not defined in Configuration File");
			
		
		
		Monitor<String, String> monitor = new BasicMonitor<String, String>(listener, filter, translator, router);
				
		logger.info("Concentrator initialized");

		monitor.start();

		logger.info("Concentrator started");

		try {
			while (true) {
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
