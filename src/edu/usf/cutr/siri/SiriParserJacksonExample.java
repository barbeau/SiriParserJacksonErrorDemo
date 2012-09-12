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
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

//SIRI POJO imports
import uk.org.siri.siri.AffectedVehicleJourney;
import uk.org.siri.siri.MonitoredStopVisit;
import uk.org.siri.siri.PtConsequence;
import uk.org.siri.siri.PtSituationElement;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDelivery;
import uk.org.siri.siri.SituationRef;
import uk.org.siri.siri.StopMonitoringDelivery;
import uk.org.siri.siri.VehicleActivity;
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
public class SiriParserJacksonExample {

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
			
			/*
			 * Alternately, instead of passing in a File, a String encoded in JSON or XML can be
			 * passed into Jackson for parsing.  Uncomment the below line to read the 
			 * JSON or XML into the String.
			 */			
			//String inputExample = readFile(file);	
			
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
				
				/*
				 * Alternately, you can also deserialize the JSON from a String into the Siri object.
				 * Uncomment the below line to parsing the JSON from a String instead of the File.
				 */
				//siri = mapper.readValue(inputExample, Siri.class);
			
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
			System.err.println("Error reading or parsing input file: " + e);
		}
		
	}

	/**
	 * Read in input file to string
	 * @param file file containing the JSON or XML data
	 * @return String representation of the JSON or XML file
	 * @throws IOException
	 */
	private static String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());			
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
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
            	            	
            	List<VehicleActivity> vaList = vmd.getVehicleActivity();
            		            	            	
            	for(VehicleActivity va : vaList){
            		System.out.println("------------------------");
	            	System.out.println("-   Vehicle Activity:  -");
	            	System.out.println("------------------------");
	            	
            		System.out.println("LineRef: " + va.getMonitoredVehicleJourney().getLineRef());
            		System.out.println("DirectionRef: " + va.getMonitoredVehicleJourney().getDirectionRef());
            		System.out.println("FramedVehicleJourneyRef.DataFrameRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
            		System.out.println("FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
            		System.out.println("JourneyPatternRef: " + va.getMonitoredVehicleJourney().getJourneyPatternRef());
            		System.out.println("PublishedLineName: " + va.getMonitoredVehicleJourney().getPublishedLineName());
            		System.out.println("OperatorRef: " + va.getMonitoredVehicleJourney().getOperatorRef());
            		System.out.println("OriginRef: " + va.getMonitoredVehicleJourney().getOriginRef());
            		System.out.println("DestinationRef: " + va.getMonitoredVehicleJourney().getDestinationRef());
            		System.out.println("DestinationName: " + va.getMonitoredVehicleJourney().getDestinationName());
            		
            		System.out.println("------------------");
                	System.out.println("- Situation Ref: -");
                	System.out.println("------------------");
                	List<SituationRef> srList = va.getMonitoredVehicleJourney().getSituationRef();                	
                	for(SituationRef sr : srList){
                		System.out.println("SituationRef: " + sr.getSituationSimpleRef());
                		System.out.println("SituationRef.FullRef: " + sr.getSituationFullRef());
                		
                	}
                	System.out.println("----------------");
                	
                	System.out.println("Monitored: " + va.getMonitoredVehicleJourney().isMonitored());
                	System.out.println("VehicleLocation.Longitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
                	System.out.println("VehicleLocation.Latitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
                	System.out.println("Bearing: " + va.getMonitoredVehicleJourney().getBearing());
                	System.out.println("ProgressRate: " + va.getMonitoredVehicleJourney().getProgressRate());
                	System.out.println("ProgressStatus: " + va.getMonitoredVehicleJourney().getProgressStatus());
                	System.out.println("BlockRef: " + va.getMonitoredVehicleJourney().getBlockRef());
                	System.out.println("VehicleRef: " + va.getMonitoredVehicleJourney().getVehicleRef());
                	System.out.println("MonitoredCall.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
                	System.out.println("MonitoredCall.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
                	System.out.println("MonitoredCall.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
                	System.out.println("MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
                	System.out.println("MonitoredCall.StopPointRef: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
                	System.out.println("MonitoredCall.VisitNumber: " + va.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
                	System.out.println("MonitoredCall.StopPointName: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
                	
                	System.out.println("OnwardCalls.AimedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
                	System.out.println("OnwardCalls.AimedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
                	System.out.println("OnwardCalls.AimedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
                	System.out.println("OnwardCalls.ArrivalBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
                	System.out.println("OnwardCalls.ArrivalPlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
                	System.out.println("OnwardCalls.ArrivalStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
                	System.out.println("OnwardCalls.DepartureBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
                	System.out.println("OnwardCalls.DeparturePlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
                	System.out.println("OnwardCalls.DepartureStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
                	System.out.println("OnwardCalls.ExpectedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
                	System.out.println("OnwardCalls.ExpectedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
                	System.out.println("OnwardCalls.ExpectedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
                	
                	if(va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
                		System.out.println("OnwardCalls.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
                		System.out.println("OnwardCalls.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
                		System.out.println("OnwardCalls.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
                		System.out.println("OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
                	}
                	
                	System.out.println("RecordedAtTime: " + va.getRecordedAtTime());
                	System.out.println("------------------------");
            	}
            		            	
            	System.out.println("ResponseTimestamp: " + vmd.getResponseTimestamp());
            	System.out.println("ValidUntil: " + vmd.getValidUntil());
            }
		}
		
		System.out.println("------------------------------------------");
		
		System.out.println("------------------------------------------");
    	System.out.println("-        Stop Monitoring Delivery:       -");
    	System.out.println("------------------------------------------");
    	
    	List<StopMonitoringDelivery> listSMD = siri.getServiceDelivery().getStopMonitoringDelivery();
    	
    	if(listSMD != null){
    		
			for(StopMonitoringDelivery smd : listSMD){
            	            	
            	List<MonitoredStopVisit> msvList = smd.getMonitoredStopVisit();
            		            		            	
            	for(MonitoredStopVisit msv : msvList){
            		
            		System.out.println("----------------------------");
	            	System.out.println("-   Monitored Stop Visit:  -");
	            	System.out.println("----------------------------");
            		
            		System.out.println("LineRef: " + msv.getMonitoredVehicleJourney().getLineRef());
            		System.out.println("DirectionRef: " + msv.getMonitoredVehicleJourney().getDirectionRef());
            		System.out.println("FramedVehicleJourneyRef.DataFrameRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
            		System.out.println("FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
            		System.out.println("JourneyPatternRef: " + msv.getMonitoredVehicleJourney().getJourneyPatternRef());
            		System.out.println("PublishedLineName: " + msv.getMonitoredVehicleJourney().getPublishedLineName());
            		System.out.println("OperatorRef: " + msv.getMonitoredVehicleJourney().getOperatorRef());
            		System.out.println("OriginRef: " + msv.getMonitoredVehicleJourney().getOriginRef());
            		System.out.println("DestinationRef: " + msv.getMonitoredVehicleJourney().getDestinationRef());
            		System.out.println("DestinationName: " + msv.getMonitoredVehicleJourney().getDestinationName());
            		
            		System.out.println("------------------");
                	System.out.println("- Situation Ref: -");
                	System.out.println("------------------");
                	List<SituationRef> srList = msv.getMonitoredVehicleJourney().getSituationRef();                	
                	for(SituationRef sr : srList){
                		System.out.println("SituationRef: " + sr.getSituationSimpleRef());
                		System.out.println("SituationRef.FullRef: " + sr.getSituationFullRef());
                		
                	}
                	System.out.println("----------------");
                	
                	System.out.println("Monitored: " + msv.getMonitoredVehicleJourney().isMonitored());
                	System.out.println("VehicleLocation.Longitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
                	System.out.println("VehicleLocation.Latitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
                	System.out.println("Bearing: " + msv.getMonitoredVehicleJourney().getBearing());
                	System.out.println("ProgressRate: " + msv.getMonitoredVehicleJourney().getProgressRate());
                	System.out.println("ProgressStatus: " + msv.getMonitoredVehicleJourney().getProgressStatus());
                	System.out.println("BlockRef: " + msv.getMonitoredVehicleJourney().getBlockRef());
                	System.out.println("VehicleRef: " + msv.getMonitoredVehicleJourney().getVehicleRef());
                	System.out.println("MonitoredCall.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
                	System.out.println("MonitoredCall.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
                	System.out.println("MonitoredCall.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
                	System.out.println("MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
                	System.out.println("MonitoredCall.StopPointRef: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
                	System.out.println("MonitoredCall.VisitNumber: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
                	System.out.println("MonitoredCall.StopPointName: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
                	
                	System.out.println("OnwardCalls.AimedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
                	System.out.println("OnwardCalls.AimedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
                	System.out.println("OnwardCalls.AimedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
                	System.out.println("OnwardCalls.ArrivalBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
                	System.out.println("OnwardCalls.ArrivalPlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
                	System.out.println("OnwardCalls.ArrivalStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
                	System.out.println("OnwardCalls.DepartureBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
                	System.out.println("OnwardCalls.DeparturePlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
                	System.out.println("OnwardCalls.DepartureStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
                	System.out.println("OnwardCalls.ExpectedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
                	System.out.println("OnwardCalls.ExpectedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
                	System.out.println("OnwardCalls.ExpectedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
                	
                	if(msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
                		System.out.println("OnwardCalls.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
                		System.out.println("OnwardCalls.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
                		System.out.println("OnwardCalls.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
                		System.out.println("OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
                	}
                	
                	System.out.println("RecordedAtTime: " + msv.getRecordedAtTime());
                	System.out.println("------------------------");
            	}
            	
            	
            	System.out.println("ResponseTimestamp: " + smd.getResponseTimestamp());
            	System.out.println("ValidUntil: " + smd.getValidUntil());
            }
    	
    	}
		
    	System.out.println("------------------------------------------");
    	
		System.out.println("------------------------------------------");
    	System.out.println("-    Situation Exchange Delivery:        -");
    	System.out.println("------------------------------------------");
    	
    	List<SituationExchangeDelivery> sedList = siri.getServiceDelivery().getSituationExchangeDelivery();
    	
    	for(SituationExchangeDelivery sed : sedList){
    		List<PtSituationElement> ptseList = sed.getSituations().getPtSituationElement();
    		
    		System.out.println("----------------------------");
        	System.out.println("-     PtSituationElement:  -");
        	System.out.println("----------------------------");
    		
    		for(PtSituationElement ptse : ptseList){
    			System.out.println("PtSituationElement.PublicationWindow.StartTime: " + ptse.getPublicationWindow().getStartTime());
    			System.out.println("PtSituationElement.PublicationWindow.EndTime: " + ptse.getPublicationWindow().getEndTime());
    			System.out.println("PtSituationElement.Severity: " + ptse.getSeverity());
    			System.out.println("PtSituationElement.Summary: " + ptse.getSummary()); //TODO - check this output
    			System.out.println("PtSituationElement.Description: " + ptse.getDescription()); //TODO - check this output
    			       			
    			List<AffectedVehicleJourney> avjList = ptse.getAffects().getVehicleJourneys().getAffectedVehicleJourney();
    			
    			for(AffectedVehicleJourney avj : avjList){
    				
    				System.out.println("---------------------------");
                	System.out.println("- AffectedVehicleJounrey: -");
                	System.out.println("---------------------------");
                	System.out.println("LineRef: " + avj.getLineRef()); //TODO - check this output
                	System.out.println("DirectionRef: " + avj.getDirectionRef()); //TODO - check this output        				
    			}
    			
    			System.out.println("---------------------------");
    			
    			List<PtConsequence> ptConList = ptse.getConsequences().getConsequence();  //TODO - check this output
    			
    			for(PtConsequence ptCon: ptConList){
    				System.out.println("----------------------");
                	System.out.println("-    PtConsequences: -");
                	System.out.println("----------------------");
                	System.out.println("Condition: " + ptCon.getCondition().toString());
    			}
    			
    			System.out.println("----------------------");
    			
    			System.out.println("PtSituationElement.SituationNumber: " + ptse.getSituationNumber()); //TODO - check this output        			
    		}
    		System.out.println("----------------------------");
    		
    	}
    	System.out.println("------------------------------------------");
	}

}
