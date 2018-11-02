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
package es.upm.syst.IoT.examples;

import javax.ws.rs.core.Response;

import com.indra.sofia2.ssap.kp.implementations.rest.SSAPResourceAPI;
import com.indra.sofia2.ssap.kp.implementations.rest.exception.ResponseMapperException;
import com.indra.sofia2.ssap.kp.implementations.rest.resource.SSAPResource;

public class ReaderSofia2 {

	private SSAPResourceAPI api=new SSAPResourceAPI("http://138.100.58.3:9000/sib/services/api_ssap/");
	private final String ontology = "MotaMeasures";
	private final String token = "c460e7cfb55f4be4bfde7dc2016571cd";
	private final String instanceKP = "kp_concentrador1:router";
	private volatile String sessionKey;
	
	public void join(){
		
		SSAPResource ssapResource=new SSAPResource();
		ssapResource.setJoin(true);
		ssapResource.setToken(token);
		ssapResource.setInstanceKP(instanceKP);
		
		
		Response responseJoin=api.insert(ssapResource);
		
		if(responseJoin.getStatus() == 200){
			try {
				sessionKey=api.responseAsSsap(responseJoin).getSessionKey();
			} catch (ResponseMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
							
		}else{
			//TODO exception
		}
			
				
	}
	
	public void close(){
		Response responseLeave = null;
		SSAPResource ssapResource=new SSAPResource();
		ssapResource.setLeave(true);
		
		//close the session
		ssapResource.setSessionKey(sessionKey);
		responseLeave=api.insert(ssapResource);
		
		if(responseLeave.getStatus() != 200){
			System.out.println(responseLeave.getStatus());
			System.out.println("It was not possible to close the SSAP session");
		}else{
			System.out.println("Session closed: " + sessionKey);
			System.out.println("Session closed:" + responseLeave.getStatus());
		}
			
	}
	
	public String read(){
		String query = "db.SensorTemperatura.find().limit(3)";
		Response response = api.query(sessionKey, ontology, query, null, "NATIVE" );
		if(response.getStatus() != 200){
			System.out.println(response.getStatus());
		}else{
			System.out.println("Response:" + response);
		}
		return response.toString();
	}
	
	public static void main(String...args){
		ReaderSofia2 reader = new ReaderSofia2();
		reader.join();
		reader.read();
		reader.close();
	}
}
