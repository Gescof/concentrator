/*
 * #%L
 * TDManager
 * %%
 * Copyright (C) 2014 - 2016 SYST Group, Universidad Politecnica de Madrid
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
package es.upm.syst.IoT.components;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.upm.syst.IoT.components.filters.Filter;
import es.upm.syst.IoT.components.listeners.Listener;
import es.upm.syst.IoT.components.routers.Router;
import es.upm.syst.IoT.components.translators.Translator;

public class BasicMonitor<I,O> implements Monitor<I,O> {
	
	private static final Logger logger = LogManager.getLogger();
	
	private Listener<I,O> listener;
	private Filter<I> filter;
	private Translator<I,O> translator;
	private Router<O> router;
	
	private boolean started = false;
	final private Object lock = new Object();
	
	public  BasicMonitor(final Listener<I,O> listener, final Filter<I> filter, final Translator<I,O> translator, final Router<O> router){
		logger.info("Preparing monitor");
		this.listener = listener;
		listener.setMonitor(this);
		this.filter = filter;
		this.translator = translator;
		this.router = router;
		Thread thread = new Thread(listener, listener.getClass().getName());
		thread.start();
		logger.info("Monitor ready");
	}

	/**
	 * this method is executed when the listener has a data.
	 * this method must not have side effects. Without state, it will not have synchronization problems
	 */
	@Override
	public void dataObtained(final I object) {
		
			I filtered = filter.filter(object);
			O translated = translator.translate(filtered);
			if (translated != null){
				router.send(translated);
			}		
		
	}

	@Override
	public void close() throws Exception {
		synchronized (lock) {
			listener.close();
			router.close();
		}
	}
	
	@Override
	public void start() {
		synchronized (lock) {
			listener.start();
			started = true;
		}
	}
	
	@Override
	public void stop() {
		synchronized (lock) {
			listener.stop();
			started = false;
		}
		
	}	

	@Override
	public boolean isStarted() {
		synchronized (lock) {
			return started;
		}
	}
	
	
	
	
}
