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
package es.upm.syst.IoT.concentrator.simulators;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;

import es.upm.syst.IoT.components.BasicMonitor;
import es.upm.syst.IoT.components.Monitor;
import es.upm.syst.IoT.components.filters.Filter;
import es.upm.syst.IoT.components.filters.NoFilter;
import es.upm.syst.IoT.components.listeners.Listener;
import es.upm.syst.IoT.components.routers.ConsoleRouter;
import es.upm.syst.IoT.components.routers.Router;
import es.upm.syst.IoT.components.translators.ConcentradorBibliotecaTranslator;
import es.upm.syst.IoT.components.translators.Translator;

import com.google.gson.Gson; 



public class Concentrator {
	
	private static final Logger logger = LogManager.getLogger();
	
	    private static void escribirJson(String dato) throws java.io.IOException {
	    	 FileWriter fichero = null;
	         PrintWriter pw = null;
	         try
	         {
	             fichero = new FileWriter("E:\\Users\\Guillermo\\Documents\\concentrator\\generated_json\\pruebas.json");
	             pw = new PrintWriter(fichero);
	             pw.println(dato);

	         } catch (Exception e) {
	             e.printStackTrace();
	         } finally {
	            try {
	            // Nuevamente aprovechamos el finally para 
	            // asegurarnos que se cierra el fichero.
	            if (null != fichero)
	               fichero.close();
	            } catch (Exception e2) {
	               e2.printStackTrace();
	            }
	         }      
	    }
	 
	
	
	public static void main (String...args){
		
		logger.info("Executing Simulator Concentrator");
		
		Filter<String> filter = new NoFilter<String>();
		//Translator translator = new MotaMasterTranslator();
		Translator<String,String> translator = new ConcentradorBibliotecaTranslator();
		
		int cont =0;
		
		try (	
				//Listener listener = new es.upm.syst.IoT.components.listeners.MockMotaMasterListener(1000);
				Listener<String,String> listener = new es.upm.syst.IoT.components.listeners.MockConcentradorBiblioteca(1000);
				//Listener<String,String> listener = new es.upm.syst.IoT.components.listeners.RaspberryPiUARTListenerHeaders(1000);
				//Router<String> router = new ConsoleRouter();
				Router<String> router = new ConsoleRouter<String>();
				Monitor<String,String> monitor = new BasicMonitor<String,String>(listener, filter, translator, router);) {
			
			monitor.start();
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			monitor.stop();

		} /*catch (ResponseMapperException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/ catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
			//System.out.println(translator.getMnsg().substring(4));
			try {
			escribirJson(translator.getMnsg().substring(4));
			System.out.println("fichero creado");
			}catch(IOException E) {
				
			}
	}	
	

}