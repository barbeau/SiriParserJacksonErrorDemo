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

//Apache filename util import
import org.apache.commons.io.FilenameUtils;

//SIRI POJO imports
import uk.org.siri.siri.Siri;

//Jackson XML imports
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

//Jackson JSON imports
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;


/**
 * This class is an example of parsing a JSON or XML response from a SIRI feed
 * using Jackson
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriParserJacksonErrorDemo {

	/**
	 * Takes in a path to a JSON or XML file, parses the contents into a Siri
	 * object, and prints out the contents of the Siri object.
	 * 
	 * @param args
	 *            path to the JSON or XML file located on disk
	 */
	public static void main(String[] args) {

		if (args[0] == null) {
			System.out
					.println("Proper Usage is: java JacksonSiriParserExample path-to-siri-file-to-parse");
			System.exit(0);
		}

		try {

			// Siri object we're going to instantiate based on JSON or XML data
			Siri siri = null;

			// Get example JSON or XML from file
			File file = new File(args[0]);

			System.out.println("Input file = " + file.getAbsolutePath());

			String extension = FilenameUtils.getExtension(args[0]);

			if (extension.equalsIgnoreCase("json")) {
				System.out.println("Parsing JSON...");
				ObjectMapper mapper = new ObjectMapper();

				// Jackson 2.0 configuration settings
				mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
				mapper.configure(
						DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
						true);
				mapper.configure(
						DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
						true);
				mapper.configure(
						DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,
						true);
				mapper.configure(
						DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

				// Tell Jackson to expect the JSON in PascalCase, instead of
				// camelCase
				mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.PascalCaseStrategy());

				// Deserialize the JSON from the file into the Siri object
				siri = mapper.readValue(file, Siri.class);
			}

			if (extension.equalsIgnoreCase("xml")) {
				System.out.println("Parsing XML...");
				// Use Aalto StAX implementation explicitly
				XmlFactory f = new XmlFactory(new InputFactoryImpl(),
						new OutputFactoryImpl());

				JacksonXmlModule module = new JacksonXmlModule();

				/**
				 * Tell Jackson that Lists are using "unwrapped" style (i.e., 
				 * there is no wrapper element for list). This fixes the error
				 * "com.fasterxml.jackson.databind.JsonMappingException: Can not
				 * >> instantiate value of type [simple type, class >>
				 * uk.org.siri.siri.VehicleMonitoringDelivery] from JSON String;
				 * no >> single-String constructor/factory method (through
				 * reference chain: >>
				 * uk.org.siri.siri.Siri["ServiceDelivery"]->
				 * uk.org.siri.siri.ServiceDel >>
				 * ivery["VehicleMonitoringDelivery"])"
				 * 
				 * NOTE - This requires Jackson 2.1, which is still pre-release
				 * as of 9/12/2012
				 **/
				module.setDefaultUseWrapper(false);
				
				/**
				 * Handles "xml:lang" attribute, which is used in SIRI
				 * NaturalLanguage String, and looks like:
				 * <Description xml:lang="EN">b/d 1:00pm until f/n. loc al and 
				 * express buses run w/delays & detours. POTUS visit in MANH. 
				 * Allow additional travel time Details at www.mta.info</Description>
				 * 
				 * Passing "Value" (to match expected name in XML to map, 
				 * considering naming strategy) will make things work.  This is 
				 * since JAXB uses pseudo-property name of "value" for XML Text 
				 * segments, whereas Jackson by default uses "" (to avoid name 
				 * collisions).
				 * 
				 * NOTE - This requires Jackson 2.1, which is still pre-release
				 * as of 9/12/2012.
				 * 
				 * NOTE - This still requires a CustomPascalCaseStrategy to work.
				 * Please see the CustomPascalCaseStrategy in this app that is used below.
				 */
				module.setXMLTextElementName("lang");

				XmlMapper xmlMapper = new XmlMapper(f, module);

				xmlMapper.configure(
						DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
						true);
				xmlMapper
						.configure(
								DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
								true);
				xmlMapper.configure(
						DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY,
						true);
				xmlMapper
						.configure(
								DeserializationFeature.READ_ENUMS_USING_TO_STRING,
								true);

				// Tell Jackson to expect the XML in PascalCase, instead of
				// camelCase
				//xmlMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.PascalCaseStrategy());
				xmlMapper.setPropertyNamingStrategy(new CustomPascalCaseStrategy());

				// Parse the SIRI XML response
				siri = xmlMapper.readValue(file, Siri.class);
			}

			// If we successfully retrieved and parsed JSON or XML, print the
			// contents
			if (siri != null) {
				SiriUtils.printContents(siri);
			}

		} catch (IOException e) {
			System.err.println("Error parsing input file: " + e);
			e.printStackTrace();
		}

	}
}
