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

import java.util.concurrent.ThreadLocalRandom;

import es.upm.syst.IoT.components.Monitor;

public class MockConcentradorBiblioteca implements Listener<String, String> {

	final private Object lock = new Object();
	final private int timeLapse;
	
	private volatile boolean started = false; //synchronized by lock
	private volatile boolean closed = false; //synchronized by lock
	protected  Monitor<String, String> monitor;

	public MockConcentradorBiblioteca(int timeLapse) {
		this.timeLapse = timeLapse;
	}
	
	
	final private String TXT_HEADER = "<txt_ini>";
	final private String TXT_TAIL =   "<txt_end>";
	
	final private String LEC_HEADER = "<lec_ini>";
	final private String LEC_TAIL =   "<lec_end>"; 
	
	final private String MEASURE_MARKER = ";";
	final private String MARKER = "|";
	
	@Override
	public void run() {
		
		
	    int cycle = 5;
	    int i = 0;
	    
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
				StringBuffer buffer = new StringBuffer();
				
				if (i < cycle){
					
					buffer.append(LEC_HEADER);
					buffer.append(13);
					buffer.append(MEASURE_MARKER);
					generateMeasure(buffer,"temperature", "Cº");
					buffer.append(MEASURE_MARKER);
					generateMeasure(buffer,"humidity", "RH");
					buffer.append(MEASURE_MARKER);
					generateMeasure(buffer,"luminosity", "lx");
					buffer.append(MEASURE_MARKER);
//					generateMeasure(buffer,"CO2", "ppm");
//					buffer.append(MEASURE_MARKER);
//					generateMeasure(buffer,"CO", "ppm");
					buffer.append(LEC_TAIL);
					
					i++;
				}else{
					
					buffer.append(TXT_HEADER);
					buffer.append("Lore ipsum dolor");
					buffer.append(TXT_TAIL);
					
					i = 0;
				}
				
				
				String dataString = buffer.toString();
				monitor.dataObtained(dataString);
	
				try {
					Thread.sleep(timeLapse);
				} catch (InterruptedException e) {
					System.out.println("InfoThread ERROR: " + e);
				}
			}
		}
	}
	
	private String generateMeasure(StringBuffer buffer, String type, String unit){
		
		buffer.append(type);
		buffer.append(MARKER);
		int stop = ThreadLocalRandom.current().nextInt(1, 3);
		for (int j = 0; j < stop ; j++){
			buffer.append(ThreadLocalRandom.current().nextInt(0, 9));
		}
		buffer.append(".");
		buffer.append(ThreadLocalRandom.current().nextInt(0, 9));
		buffer.append(MARKER);
		buffer.append(unit);
		return buffer.toString();
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
