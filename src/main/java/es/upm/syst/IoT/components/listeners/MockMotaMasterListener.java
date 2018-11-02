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
package es.upm.syst.IoT.components.listeners;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import es.upm.syst.IoT.components.Monitor;



public class MockMotaMasterListener implements Listener<Object, Object> {
	
	final private Object lock = new Object();
	final private int timeLapse;
	
	private volatile boolean started = false; //synchronized by lock
	private volatile boolean closed = false; //synchronized by lock
	protected  Monitor<Object, Object> monitor;
	

	public MockMotaMasterListener(int timeLapse) {
		this.timeLapse = timeLapse;	
	}
	
	
	@Override
	public void run() {
			
	
	    int min = 48;
		int max = 58;
		Charset charset = StandardCharsets.ISO_8859_1;
	    
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
				ByteBuffer buffer = ByteBuffer.allocate(14);
				for (int i = 0 ; i < 12 ; i++){
					if (i == 0 || i == 4 || i == 8){
						buffer.put((byte) 32);
					}else{
						int digit = ThreadLocalRandom.current().nextInt(min, max);
						buffer.put((byte) digit);
					}
				}
				byte[] data = buffer
						.put((byte) 13)		
						.put((byte) 10)
						.array();
				
				String dataString = new String(data, charset);
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
	public void setMonitor(Monitor<Object, Object> monitor) {
		this.monitor = monitor;
		
	}

}
