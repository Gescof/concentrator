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
package es.upm.syst.IoT.components.routers;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.indra.sofia2.ssap.kp.implementations.rest.SSAPResourceAPI;
import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;
import com.indra.sofia2.ssap.kp.implementations.rest.resource.SSAPResource;

public class Sofia2Router implements Router<String>{
	
	private static final Logger logger = LogManager.getLogger();
	
	private SSAPResourceAPI api=new SSAPResourceAPI("http://138.100.58.3:9000/sib/services/api_ssap/");
	private final String ontology = "MotaMeasures";
	private final String token = "c460e7cfb55f4be4bfde7dc2016571cd";
	private final String instanceKP = "kp_concentrador1:router";
	private volatile String sessionKey;
	
	private volatile boolean closed = false;
	final private Object lock = new Object();	
	
	
	public Sofia2Router() {
		logger.info("Connecting Sofia2");
		join();
		
		
	}
	
	private void join(){
		
		SSAPResource ssapResource=new SSAPResource();
		ssapResource.setJoin(true);
		ssapResource.setToken(token);
		ssapResource.setInstanceKP(instanceKP);
		
		synchronized (lock) {	
			if (!closed){
				Response responseJoin=api.insert(ssapResource);
				
				if(responseJoin.getStatus() == 200){
					try {
						sessionKey=api.responseAsSsap(responseJoin).getSessionKey();
					} catch (ResponseMapperException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					logger.info("Connected to Sofia2 with session {}", sessionKey);
									
				}else{
					//TODO exception
				}
			}
		}		
	}

	
	//Query to see new data in sofia2:
	//select * from MotaMeasures order by contextData.timestamp desc limit 3;
	
	@Override
	public void send(final String data) {
		
		SSAPResource ssapResourceMedida=createResource(data);
		
		Response responseInsert=api.insert(ssapResourceMedida);
		
		int status = responseInsert.getStatus();
		
		if(status!=200){
			logger.error("Error Insertando");
			System.out.println(status);
			if (status==401){
				logger.warn("Trying reconnection");
				join();
				api.insert(ssapResourceMedida);
				if(status!=200){
					logger.error("Error Reinsertando");
				}
			}
		}else{
			logger.debug("Data transmited: {}", data);
		}
	}

	private SSAPResource createResource(final String data) {
		SSAPResource ssapResourceMedida = new SSAPResource();
		ssapResourceMedida.setSessionKey(sessionKey);
		ssapResourceMedida.setOntology(ontology);
		ssapResourceMedida.setData(data);
		return ssapResourceMedida;
	}

	@Override
	public void close() throws Exception {
		Response responseLeave = null;
		SSAPResource ssapResource=new SSAPResource();
		ssapResource.setLeave(true);
		synchronized (lock) {
			if (!closed){
				closed = true;
				//close the session
				ssapResource.setSessionKey(sessionKey);
				responseLeave=api.insert(ssapResource);
				
				if(responseLeave.getStatus() != 200){
					logger.info(responseLeave.getStatus());
					throw new Exception("It was not possible to close the SSAP session");
				}else{
					logger.info("Session closed: " + sessionKey);
					logger.info("Session closed:" + responseLeave.getStatus());
				}
			}
		}
	}
}
