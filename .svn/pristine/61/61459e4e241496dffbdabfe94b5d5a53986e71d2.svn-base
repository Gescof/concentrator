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
package es.upm.syst.IoT.components.filters;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JSONFilterExperimental<T> implements Filter<T> {
	static Logger logger = LogManager.getLogger();
	
	/*
	 * EXPERIMENTAL VARIABLES
	 */
	
	int sucess, fail = 0;
	Date fechaInicial= new Date();
	Date fechaFinal;
	String exito = "", fallo = "", esperados = "";
	int expected = 0;
	int anterior = -1;
	
	/*////////////////////////////////////////
	 */

	@Override
	public T filter(T object) {
		/*
		 *  TEST/////////////////////////////////////////////////////////////
		 */
		fechaFinal = new Date();
		expected = (int) (((fechaFinal.getTime() - fechaInicial.getTime()) / 1000)/30)*5;
		if(expected % 30 == 0 && expected != anterior) {			
			logger.info("Experiment results: Sucess = "+sucess+" Fail = "+fail+" TotalExpected = "+expected);
			exito = exito + sucess + ",";			
			fallo = fallo + fail + ",";			
			esperados = esperados + expected + ",";
			logger.info("Exito : "+exito);
			logger.info("Fallo : "+fallo);
			logger.info("Esperados : "+esperados);
			anterior = expected;
		}
		
		/*//////////////////////////////////////////////////////*/
		
		int openCurlyBracket = charCounter(object.toString(), '{');
		int closeCurlyBracket = charCounter(object.toString(), '}');
		if(openCurlyBracket == closeCurlyBracket && openCurlyBracket!=0 ) {
			if(openCurlyBracket > 1) {
				String message = object.toString();
				String result = "[";
				for(int i=0; i<openCurlyBracket;i++){
					result = result + message.substring(0, message.indexOf('}')+1) + ",";
					message = message.substring(message.indexOf('}')+1, message.length());	
					sucess++; //TEST
				}
				result = result + "]";
				return (T) result;
			}
			else {
				sucess++; //TEST
				return object;		
			}
		}
		else {
			fail++; //TEST
			logger.info("Bad format message: "+object.toString());
			return (T) "";
		}
		
	}
	
	private int charCounter(String cadena, char caracter) {
        int posicion, contador = 0;
        posicion = cadena.indexOf(caracter);
        while (posicion != -1) { 
            contador++;       
            posicion = cadena.indexOf(caracter, posicion + 1);
        }
        return contador;
	}
}