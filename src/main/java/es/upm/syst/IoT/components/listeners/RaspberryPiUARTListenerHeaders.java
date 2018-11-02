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

import es.upm.syst.IoT.components.BasicMonitor;
import es.upm.syst.IoT.components.Monitor;
import es.upm.syst.IoT.components.filters.NoFilter;
import es.upm.syst.IoT.components.routers.ConsoleRouter;
import es.upm.syst.IoT.components.translators.NoTranslator;


public class RaspberryPiUARTListenerHeaders implements Listener<String, String> {
	
	final private String TXT_HEADER = "<txt_ini>";
	final private String TXT_TAIL =   "<txt_end>";
	
	final private String LEC_HEADER = "<lec_ini>";
	final private String LEC_TAIL =   "<lec_end>";
	
	private static final Logger logger = LogManager.getLogger();
	
	private Properties properties;
	private static final String file = "UART.properties";
	
	final private Object lock = new Object();
	final private int timeLapse;
	
	private volatile boolean started = false; //synchronized by lock
	private volatile boolean closed = false; //synchronized by lock
	private Monitor<String, String> monitor;
	
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
	        	    
					processingOffset = concatenate(processingOffset, buffer);
					processingOffset = extractData(processingOffset, charset);
						
				}               
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
	
	final private Object lockPO = new Object();
	byte[] processingOffset = new byte [0]; //synchronized by lockPO
	
	final private Charset charset = StandardCharsets.ISO_8859_1;
	
	final private String portName;
	final private int bauds;
	final private int databits;
	final private int stopbits;
	final private String parity;
    final private SerialConfig config;
    
	public RaspberryPiUARTListenerHeaders(final int timeLapse) {
		
		logger.info("Configuring Listener");
		
		this.timeLapse = timeLapse;
		
		properties = new Properties();
		try{
			InputStream iStream = RaspberryPiUARTListenerHeaders.class.getClassLoader().getResourceAsStream(file);
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
	public void setMonitor(final Monitor<String, String> monitor) {
		this.monitor = monitor;
		
	}
	
	private byte[] extractData(final byte[] buffer, final Charset charset) {
		boolean thereIsMsg = true;
		
		byte[] rest = new byte[buffer.length];
		System.arraycopy(buffer, 0, rest, 0, buffer.length);
		
		while (thereIsMsg){
			String data = new String(rest, charset);
			int lastCharInMsg = getMsg(data);
			if (lastCharInMsg == -1){
				thereIsMsg = false;
			}else{
				String msg = data.substring(0, lastCharInMsg);
				monitor.dataObtained(msg);
				String restString = (lastCharInMsg < data.length()) ? data.substring(lastCharInMsg, data.length()) : "" ;
				rest = restString.getBytes(charset);
			}
		}
		
		return rest;
	}


	private int getMsg(String data) {
		int txtIndex = data.indexOf(TXT_TAIL);
		int lecIndex = data.indexOf(LEC_TAIL);
		int index;
		
		if (lecIndex <0){
			if (txtIndex<0){
				return -1;
			}else{
				index = txtIndex + TXT_TAIL.length();
			}
		}else{ 
			if (txtIndex < 0){
				index = lecIndex + LEC_TAIL.length();
			}else{
				if(lecIndex < txtIndex){
					index = lecIndex + LEC_TAIL.length();
				}else{
					index = txtIndex + TXT_TAIL.length();
				}				
			}
		}
		
		return index;
	}


	private byte[] concatenate(final byte[] a, final byte[] b) {
		int aLen = a.length;
		int bLen = b.length;
		byte[] c= new byte[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;		
	}

	
	public static void main(String[] args){
		RaspberryPiUARTListenerHeaders rp = new RaspberryPiUARTListenerHeaders(1000);
		
		rp.setMonitor(new BasicMonitor<String, String>(rp , new NoFilter<String>(), new NoTranslator<String>(), new ConsoleRouter<String>()));
		
		
		byte[] datos = (rp.TXT_HEADER + "1234567890" + rp.TXT_TAIL + rp.LEC_HEADER + "12345").getBytes(rp.charset);
		byte[] sobra = rp.extractData(datos, rp.charset);
		datos = (new String(sobra, rp.charset) + "67" + rp.LEC_TAIL).getBytes(rp.charset);
		sobra = rp.extractData(datos, rp.charset);
		rp.close();
	}


}

