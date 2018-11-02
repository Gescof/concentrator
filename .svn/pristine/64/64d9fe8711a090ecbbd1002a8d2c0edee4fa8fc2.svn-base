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


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;

import es.upm.syst.IoT.components.Monitor;


public class RaspberryPiUARTListenerFixedSize implements Listener<Object, String> {
	
	private static final Logger logger = LogManager.getLogger();
	
	private Properties properties;
	private static final String file = "UART.properties";
	
	final private Object lock = new Object();
	final private int timeLapse;
	
	private volatile boolean started = false; //synchronized by lock
	private volatile boolean closed = false; //synchronized by lock
	private Monitor<Object, String> monitor;
	
	final private Serial serial = SerialFactory.createInstance();
	
	final private SerialDataEventListener listener = new SerialDataEventListener() {
        @Override
        public void dataReceived(SerialDataEvent event) {

            // NOTE! - It is extremely important to read the data received from the
            // serial port.  If it does not get read from the receive buffer, the
            // buffer will continue to grow and consume memory.
        							
            try {   
            	synchronized (lockPO) {
            		byte[] buffer = event.getBytes();
	        	    int len = buffer.length;
            		int pLen = processingOffset.length;
					processingOffset = concatenate(processingOffset, buffer, len);
                	
					if (len + pLen >= size){
						processingOffset = extractData(processingOffset, size, len+pLen, charset);
					}	
				}               
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
	
	final private Object lockPO = new Object();
	byte[] processingOffset = new byte [0]; //synchronized by lockPO
	
	final private Charset charset = StandardCharsets.ISO_8859_1;
	final private int size = 14;
	
	final private String portName;
	final private int bauds;
	final private int databits;
	final private int stopbits;
	final private String parity;
    final private SerialConfig config;
    
	public RaspberryPiUARTListenerFixedSize(final int timeLapse) {
		
		logger.info("Configuring Listener");
		
		this.timeLapse = timeLapse;
		
		properties = new Properties();
		try{
			InputStream iStream = RaspberryPiUARTListenerFixedSize.class.getClassLoader().getResourceAsStream(file);
			properties.load(iStream);
		} catch (IOException e){
			e.printStackTrace();
		}
		portName = properties.getProperty("portName");
		bauds = Integer.parseInt(properties.getProperty("bauds"));
		databits = Integer.parseInt(properties.getProperty("databits"));
		stopbits = Integer.parseInt(properties.getProperty("stopbits"));
		parity = properties.getProperty("parity");
		
		config = new SerialConfig();
		config.device(portName)
	    	.baud(Baud.getInstance(bauds))
	    	.dataBits(DataBits.getInstance(databits))
	    	.parity(Parity.getInstance(parity)) //TODO to use the value in configuration
	    	.stopBits(StopBits.getInstance(stopbits))
	    	.flowControl(FlowControl.NONE);
		logger.info("Listener ready");
	}
	
	
	@Override
	public void run() {
	   		
		while (!closed){	//stopped is volatile	
			
			// Notify observers in the View layer.
			//I am having performance problems in dependeicies analysis
			//this.setChanged();
			//this.notifyObservers();
			
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
			
			
			try {
				Thread.sleep(timeLapse);
			} catch (InterruptedException e) {
				System.out.println("InfoThread ERROR: " + e);
			}						
		}		
	}

	@Override
	public void close(){
		synchronized (lock) {
			closed = true; //stopped is volatile.
			stop();
			SerialFactory.shutdown(); //this is important to finish all thread and services opened by pi4j library.
			lock.notify();
		}
	}
	
	@Override
	public void stop(){
		synchronized (lock) {
			if (started){
				try {
					serial.removeListener(listener);
					if (serial.isOpen()){						
						serial.close();
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				started = false;
			}
		}
	}
	
	@Override
	public void start(){
		logger.info("Starting listener");
		synchronized (lock) {
			if (!started){
				if (monitor == null){
					throw new IllegalStateException("The listener do not have monitor");
				}
				
				// create and register the serial data listener
		        serial.addListener(listener);
		
		        try {
					serial.open(config);
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		        started = true;
		        lock.notify();
			}
		}	
		logger.info("Listener Started");
	}
	
	//TODO esto no está en funcionamiento ya que serial no se ejeucta en otro thread por eventos.
	@Override
	public boolean isStarted(){
		synchronized (lock){
			return started;
		}
	}
	

	@Override
	public void setMonitor(final Monitor<Object, String> monitor) {
		this.monitor = monitor;
		
	}
	
	private byte[] extractData(final byte[] buffer, final int size, final int len, final Charset charset) {
		int i = 0;
		while ( i < len / size){
			String data = new String(buffer, i*size, size-1, charset);
			monitor.dataObtained(data);
			i++;
		}
		int restLen = len-i*size;
		byte[] rest = new byte[restLen];
		System.arraycopy(buffer, i*size, rest, 0, restLen);
		return rest;
	}


	private byte[] concatenate(final byte[] a, final byte[] b, final int bLen) {
		int aLen = a.length;
		byte[] c= new byte[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;		
	}



}

