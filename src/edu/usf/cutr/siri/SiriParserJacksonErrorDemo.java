/*
 * Copyright 2012 University of South Florida
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package edu.usf.cutr.siri;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

//SIRI POJO imports
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.VehicleMonitoringDelivery;

//Jackson XML imports
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

//Jackson JSON imports
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

//PascalCase import, to help Jackson deserialize PascalCase instead of the normal camelCase
import edu.usf.cutr.siri.jackson.PascalCaseStrategy;

/**
 * This class is an example of parsing a JSON or XML response from a SIRI feed using Jackson
 * 
 * @author Sean J. Barbeau
 *
 */
public class SiriParserJacksonErrorDemo {

	/**
	 * Takes in a path to a JSON or XML file, parses the contents into a Siri object, 
	 * and prints out the contents of the Siri object.
	 * 
	 * @param args path to the JSON or XML file located on disk
	 */
	public static void main(String[] args) {
		
		if(args[0] == null){
	        System.out.println("Proper Usage is: java JacksonSiriParserExample path-to-siri-file-to-parse");
	        System.exit(0);
	    }		
		
		try {
			
			//Siri object we're going to instantiate based on JSON or XML data
			Siri siri = null;
			
			//Get example JSON or XML from file
			File file = new File(args[0]);
			
			System.out.println("Input file = " + file.getAbsolutePath());
			
			String extension = FilenameUtils.getExtension(args[0]);
			
			if(extension.equalsIgnoreCase("json")){	
				System.out.println("Parsing JSON...");								
				ObjectMapper mapper = new ObjectMapper();
	           
	            //Jackson 2.0 configuration settings
	            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);            
	            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
	            mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
	            mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
	           
	            //Tell Jackson to expect the JSON in PascalCase, instead of camelCase
				mapper.setPropertyNamingStrategy(new PascalCaseStrategy());
	                       
				//Deserialize the JSON from the file into the Siri object
				siri = mapper.readValue(file, Siri.class);			
			}
			
			if(extension.equalsIgnoreCase("xml")){	
				System.out.println("Parsing XML...");
				//Use Aalto StAX implementation explicitly				
				XmlFactory f = new XmlFactory(new InputFactoryImpl(), new OutputFactoryImpl());
				
				XmlMapper xmlMapper = new XmlMapper(f);
				
				xmlMapper.configure(
						DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				xmlMapper.configure(
						DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
						true);
				xmlMapper.configure(
						DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
				xmlMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING,
						true);
				
				//Tell Jackson to expect the XML in PascalCase, instead of camelCase
				xmlMapper.setPropertyNamingStrategy(new PascalCaseStrategy());
				
				//Parse the SIRI XML response				
				siri = xmlMapper.readValue(file,  Siri.class);				
			}			
			
			//If we successfully retrieved and parsed JSON or XML, print the contents
			if(siri != null){
				printContents(siri);
			}
			
     		
		} catch (IOException e) {
			System.err.println("Error parsing input file: " + e);
		}
		
	}

	/**
	 * Prints the contents of a Siri object
	 * 
	 * @param siri Siri object whose contents will be printed
	 */
	private static void printContents(Siri siri){
		System.out.println("-----------------------------------------------------");
    	System.out.println("-               Service Delivery:                   -");
    	System.out.println("-----------------------------------------------------");
    	System.out.println("ResponseTimestamp: " + siri.getServiceDelivery().getResponseTimestamp());
		
		System.out.println("------------------------------------------");
    	System.out.println("-      Vehicle Monitoring Delivery:      -");
    	System.out.println("------------------------------------------");
    	
		List<VehicleMonitoringDelivery> listVMD = siri.getServiceDelivery().getVehicleMonitoringDelivery();
		
		if(listVMD != null){
			for(VehicleMonitoringDelivery vmd : listVMD){            	            	
            	            		            	
            	System.out.println("ResponseTimestamp: " + vmd.getResponseTimestamp());
            	System.out.println("ValidUntil: " + vmd.getValidUntil());
            }
		}
		
		System.out.println("------------------------------------------");
		    	
		
	}

}
