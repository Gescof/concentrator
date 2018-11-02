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

package es.upm.syst.IoT.components.listeners;

import java.io.IOException;
import java.io.InputStream;
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

/*Raspi 3 y Zero Disabling on-board Bluetooth
The steps below shows how to disable on-board Bluetooth and related services. Those steps also disable loading the related kernel modules such as bluetooth, hci_uart, btbcm, etc at boot.

1. Open /boot/config.txt file.
	
sudo nano /boot/config.txt

2. Add below, save and close the file.
	
	# Disable Bluetooth
	dtoverlay=pi3-disable-bt

3. Disable related services.
	
	sudo systemctl disable hciuart.service
	sudo systemctl disable bluealsa.service
	sudo systemctl disable bluetooth.service

4. Reboot to apply the changes	

	sudo reboot

Even after disabling on-board Bluetooth and related services, Bluetooth will be available when a Bluetooth adapter is plugged in.
 */

public class RaspiUART implements Listener<String, String> {

	static Logger logger = LogManager.getLogger();

	private Properties properties;
	private static final String file = "UART.properties";

	final private Object lock = new Object();
	final private int timeLapse;

	private volatile boolean started = false; // synchronized by lock
	private volatile boolean closed = false; // synchronized by lock
	private Monitor<String, String> monitor;

	final private Serial serial = SerialFactory.createInstance();

	final private SerialDataEventListener listener = new SerialDataEventListener() {
		@Override
		public void dataReceived(SerialDataEvent event) {

			// NOTE! - It is extremely important to read the data received from
			// the
			// serial port. If it does not get read from the receive buffer, the
			// buffer will continue to grow and consume memory.

			try {
				synchronized (lockPO) {
					String buffer = event.getAsciiString(); // Serial data obtaining					
					
					if (!buffer.isEmpty()) {
						processingString = concatenate(processingString, buffer.replaceAll("\\s", "")); // Blank spaces removed to avoid parsing errors
						if (processingString.startsWith("{") && processingString.endsWith("}")) {
							logger.debug("Message sended to monitor: "+processingString);
							monitor.dataObtained(processingString); // If message is complete, send it
							processingString = ""; // Empty the processing variable
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	final private Object lockPO = new Object();
	String processingString; // synchronized by lockPO

	final private String portName;
	final private int bauds;
	final private int databits;
	final private int stopbits;
	final private String parity;
	final private SerialConfig config;

	public RaspiUART(final int timeLapse) {

		logger.info("Configuring Listener");

		this.timeLapse = timeLapse;

		properties = new Properties();
		try {
			InputStream iStream = RaspiUART.class.getClassLoader().getResourceAsStream(file);
			properties.load(iStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		portName = properties.getProperty("portName");
		bauds = Integer.parseInt(properties.getProperty("bauds"));
		databits = Integer.parseInt(properties.getProperty("databits"));
		stopbits = Integer.parseInt(properties.getProperty("stopbits"));
		parity = properties.getProperty("parity");

		config = new SerialConfig();
		config.device(portName).baud(Baud.getInstance(bauds)).dataBits(DataBits.getInstance(databits))
				.parity(Parity.getInstance(parity))
				.stopBits(StopBits.getInstance(stopbits)).flowControl(FlowControl.NONE);

		processingString = "";
		logger.info("Listener ready");
	}

	@Override
	public void run() {
		while (!closed) {
			try {
				if (!started) {
					synchronized (lock) {
						while (!started && !closed)
							lock.wait();
					}
				}
				Thread.sleep(timeLapse);
			} catch (InterruptedException e) {
				System.out.println("InfoThread ERROR: " + e);
			}
		}
	}

	@Override
	public void close() {
		synchronized (lock) {
			closed = true; // stopped is volatile.
			stop();
			SerialFactory.shutdown(); // this is important to finish all thread and services opened by pi4j library.
			lock.notify();
		}
	}

	@Override
	public void stop() {
		synchronized (lock) {
			if (started) {
				try {
					serial.removeListener(listener);
					if (serial.isOpen()) {
						serial.close();
					}
				} catch (IllegalStateException | IOException e) {				
					e.printStackTrace();
				}
				started = false;
			}
		}
	}

	@Override
	public void start() {
		logger.info("Starting listener");
		synchronized (lock) {
			if (!started) {
				if (monitor == null) {
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
	
	 @Override 
	 public boolean isStarted(){ 
		 synchronized (lock){ 
			 return	started; 
		 } 
	 }	 

	@Override
	public void setMonitor(final Monitor<String, String> monitor) {
		this.monitor = monitor;
	}

	private String concatenate(String a, String b) {
		String result = "";
		if (a.startsWith("{"))
			result = a + b;
		else if (a.isEmpty() && b.startsWith("{"))
			result = b;
		return result;
	}
}
