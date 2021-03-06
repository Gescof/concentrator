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
package es.upm.syst.IoT.components.listeners;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import es.upm.syst.IoT.components.Monitor;

public class MockArduinoHector implements Listener<String, String> {

	final private Object lock = new Object();
	final private int timeLapse;
	
	private volatile boolean started = false; //synchronized by lock
	private volatile boolean closed = false; //synchronized by lock
	protected  Monitor<String, String> monitor;
	
	 Random randomGenerator = new Random();

	public MockArduinoHector(int timeLapse) {
		this.timeLapse = timeLapse;
	}
	
	@Override
	public void run() {
	    
		while (!closed){	//stopped is volatile	
			
			//this code block is for implementing pause behaviour for this task
			//use the if before the synchronized block is a pattern for optimization found in http://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
			//this code is used together with the pause and shutdown methods.
			//paused and stopped have to be volatile
			try {
		        if (!started) { //only block if the task has been paused, paused is volatile.
		            synchronized(lock) {
		                while (!started && !closed)
		                    lock.wait();
		            }
		        }
		    } catch (InterruptedException e){
		    }
			
			if (started){
				//String dataString = "{\"token\":\"000000000000000Mota"+randomGenerator.nextInt(5)+"\",\"Temperature\":\""+randomGenerator.nextInt(40)+"\",\"Humidity\":\""+randomGenerator.nextInt(100)+"\",\"Luminosity\":\""+randomGenerator.nextInt(100)+"\",\"Obstacle\":false}";
				String dataString = "{\"token\":\"000000000000000Mota4\",\"Temperature\":\""+randomGenerator.nextInt(40)+"\",\"Humidity\":\""+randomGenerator.nextInt(100)+"\",\"Luminosity\":\""+randomGenerator.nextInt(100)+"\",\"Obstacle\":\"" + randomGenerator.nextBoolean() +"\"}"
							   +"{\"token\":\"000000000000000Mota5\",\"Temperature\":\""+randomGenerator.nextInt(40)+"\",\"Humidity\":\""+randomGenerator.nextInt(100)+"\",\"Luminosity\":\""+randomGenerator.nextInt(100)+"\",\"Obstacle\":\"" + randomGenerator.nextBoolean() +"\"}"
							   +"{\"token\":\"000000000000000Mota6\",\"Temperature\":\""+randomGenerator.nextInt(40)+"\",\"Humidity\":\""+randomGenerator.nextInt(100)+"\",\"Luminosity\":\""+randomGenerator.nextInt(100)+"\",\"Obstacle\":\"" + randomGenerator.nextBoolean() +"\"}";
				//String dataString = "{\"token\":\"000000000000000Mota1\",\"Temperature\":\"28.1\",\"Humidity\":\"31.8\",\"Luminosity\":\"81\",\"Obstacle\":false}";
				
				//String dataString = "{\"token\":\"000000000000000Mota14\",\"Temperature\":\""+randomGenerator.nextInt(40)+"\",\"Humidity\":\""+randomGenerator.nextInt(100)+"\",\"Luminosity\":\""+randomGenerator.nextInt(100)+"\",\"Obstacle\":false, \"Consumo\": \"" +randomGenerator.nextInt(50)+"\"}"
				//		     	   +"{\"token\":\"000000000000000Mota25\",\"Temperature\":\""+randomGenerator.nextInt(40)+"\",\"Humidity\":\""+randomGenerator.nextInt(100)+"\",\"Luminosity\":\""+randomGenerator.nextInt(100)+"\",\"Obstacle\":false, \"Consumo\": \"" +randomGenerator.nextInt(50)+"\"}"
				//		 		   +"{\"token\":\"000000000000000Mota36\",\"Temperature\":\""+randomGenerator.nextInt(40)+"\",\"Humidity\":\""+randomGenerator.nextInt(100)+"\",\"Luminosity\":\""+randomGenerator.nextInt(100)+"\",\"Obstacle\":false, \"Consumo\": \"" +randomGenerator.nextInt(50)+"\"}";
				
				monitor.dataObtained(dataString);
	
				try {
					Thread.sleep(timeLapse);
				} catch (InterruptedException e) {
					System.out.println("InfoThread ERROR: " + e);
				}
			}
		}
	}
	
@Override
	public void close(){
		synchronized (lock) {
			closed = true; 
			stop();
			lock.notify();
		}
	}
	@Override
	public void stop(){
		synchronized (lock) {
			started = false; 
		}
	}
	@Override
	public void start(){
		synchronized (lock) {
			started = true;	
			lock.notify();
		}	
	}
	@Override
	public boolean isStarted(){
		synchronized (lock){
			return started;
		}
	}

	@Override
	public void setMonitor(Monitor<String,String> monitor) {
		this.monitor = monitor;

	}

}
