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

/**
 * Java imports
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
/**
 * Siri POJO imports
 */

/**
 * This class holds utility methods for the application
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriUtils {

	// For caching objects (ObjectMapper, ObjectReader, and XmlMapper) if
	// desired

	// Used to time cache read and write
	private static long cacheReadStartTime = 0;
	private static long cacheReadEndTime = 0;

	private static long cacheWriteStartTime = 0;
	private static long cacheWriteEndTime = 0;

	private static boolean usingCache = false;

	private static String CACHE_FILE_EXTENSION = ".cache";

	// Constants for defining which object type to read/write from/to cache
	public static final String OBJECT_READER = "ObjectReader";
	public static final String OBJECT_MAPPER = "ObjectMapper";
	public static final String XML_MAPPER = "XmlMapper";

	// Used to format decimals to 3 places
	static DecimalFormat df = new DecimalFormat("#,###.###");

	/**
	 * Prints the contents of a Siri object
	 * 
	 * @param siri
	 *            response from Mobile SIRI API
	 */
	public static void printContents(Siri siri) {
		System.out
				.println("-----------------------------------------------------");
		System.out
				.println("-               Service Delivery:                   -");
		System.out
				.println("-----------------------------------------------------");
		System.out.println("ResponseTimestamp: "
				+ siri.getServiceDelivery().getResponseTimestamp());

		System.out.println("------------------------------------------");
		System.out.println("-      Vehicle Monitoring Delivery:      -");
		System.out.println("------------------------------------------");

		List<VehicleMonitoringDelivery> listVMD = siri.getServiceDelivery()
				.getVehicleMonitoringDelivery();

		if (listVMD != null) {
			for (VehicleMonitoringDelivery vmd : listVMD) {

				List<VehicleActivity> vaList = vmd.getVehicleActivity();

				if (vaList != null) {
					for (VehicleActivity va : vaList) {
						System.out.println("------------------------");
						System.out.println("-   Vehicle Activity:  -");
						System.out.println("------------------------");

						System.out.println("LineRef: "
								+ va.getMonitoredVehicleJourney().getLineRef());
						System.out.println("DirectionRef: "
								+ va.getMonitoredVehicleJourney()
										.getDirectionRef());
						System.out
								.println("FramedVehicleJourneyRef.DataFrameRef: "
										+ va.getMonitoredVehicleJourney()
												.getFramedVehicleJourneyRef()
												.getDataFrameRef());
						System.out
								.println("FramedVehicleJourneyRef.DatedVehicleJourneyRef: "
										+ va.getMonitoredVehicleJourney()
												.getFramedVehicleJourneyRef()
												.getDatedVehicleJourneyRef());
						System.out.println("JourneyPatternRef: "
								+ va.getMonitoredVehicleJourney()
										.getJourneyPatternRef());
						System.out.println("PublishedLineName: "
								+ va.getMonitoredVehicleJourney()
										.getPublishedLineName());
						System.out.println("OperatorRef: "
								+ va.getMonitoredVehicleJourney()
										.getOperatorRef());
						System.out.println("OriginRef: "
								+ va.getMonitoredVehicleJourney()
										.getOriginRef());
						System.out.println("DestinationRef: "
								+ va.getMonitoredVehicleJourney()
										.getDestinationRef());
						System.out.println("DestinationName: "
								+ va.getMonitoredVehicleJourney()
										.getDestinationName());

						System.out.println("------------------");
						System.out.println("- Situation Ref: -");
						System.out.println("------------------");
						List<SituationRef> srList = va
								.getMonitoredVehicleJourney().getSituationRef();
						if (srList != null) {
							for (SituationRef sr : srList) {
								System.out.println("SituationRef: "
										+ sr.getSituationSimpleRef());
								System.out.println("SituationRef.FullRef: "
										+ sr.getSituationFullRef());

							}
						}
						System.out.println("----------------");

						System.out
								.println("Monitored: "
										+ va.getMonitoredVehicleJourney()
												.isMonitored());
						System.out.println("VehicleLocation.Longitude: "
								+ va.getMonitoredVehicleJourney()
										.getVehicleLocation().getLongitude());
						System.out.println("VehicleLocation.Latitude: "
								+ va.getMonitoredVehicleJourney()
										.getVehicleLocation().getLatitude());
						System.out.println("Bearing: "
								+ va.getMonitoredVehicleJourney().getBearing());
						System.out.println("ProgressRate: "
								+ va.getMonitoredVehicleJourney()
										.getProgressRate());
						System.out.println("ProgressStatus: "
								+ va.getMonitoredVehicleJourney()
										.getProgressStatus());
						System.out
								.println("BlockRef: "
										+ va.getMonitoredVehicleJourney()
												.getBlockRef());
						System.out.println("VehicleRef: "
								+ va.getMonitoredVehicleJourney()
										.getVehicleRef());
						if (va.getMonitoredVehicleJourney().getMonitoredCall() != null) {
							System.out
									.println("MonitoredCall.Extensions.Distances.PresentableDistance: "
											+ va.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getPresentableDistance());
							System.out
									.println("MonitoredCall.Extensions.Distances.DistanceFromCall: "
											+ va.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getDistanceFromCall());
							System.out
									.println("MonitoredCall.Extensions.Distances.StopsFromCall: "
											+ va.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getStopsFromCall());
							System.out
									.println("MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: "
											+ va.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getCallDistanceAlongRoute());
							System.out.println("MonitoredCall.StopPointRef: "
									+ va.getMonitoredVehicleJourney()
											.getMonitoredCall()
											.getStopPointRef());
							System.out.println("MonitoredCall.VisitNumber: "
									+ va.getMonitoredVehicleJourney()
											.getMonitoredCall()
											.getVisitNumber());
							System.out.println("MonitoredCall.StopPointName: "
									+ va.getMonitoredVehicleJourney()
											.getMonitoredCall()
											.getStopPointName());
						}
						if (va.getMonitoredVehicleJourney().getOnwardCalls() != null) {
							System.out.println("OnwardCalls.AimedArrivalTime: "
									+ va.getMonitoredVehicleJourney()
											.getOnwardCalls()
											.getAimedArrivalTime());
							System.out
									.println("OnwardCalls.AimedDepartureTime: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getAimedDepartureTime());
							System.out
									.println("OnwardCalls.AimedHeadwayInterval: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getAimedHeadwayInterval());
							System.out
									.println("OnwardCalls.ArrivalBoardingActivity: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getArrivalBoardingActivity());
							System.out
									.println("OnwardCalls.ArrivalPlatformName: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getArrivalPlatformName());
							System.out.println("OnwardCalls.ArrivalStatus: "
									+ va.getMonitoredVehicleJourney()
											.getOnwardCalls()
											.getArrivalStatus());
							System.out
									.println("OnwardCalls.DepartureBoardingActivity: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getDepartureBoardingActivity());
							System.out
									.println("OnwardCalls.DeparturePlatformName: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getDeparturePlatformName());
							System.out.println("OnwardCalls.DepartureStatus: "
									+ va.getMonitoredVehicleJourney()
											.getOnwardCalls()
											.getDepartureStatus());
							System.out
									.println("OnwardCalls.ExpectedArrivalTime: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getExpectedArrivalTime());
							System.out
									.println("OnwardCalls.ExpectedDepartureTime: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getExpectedDepartureTime());
							System.out
									.println("OnwardCalls.ExpectedHeadwayInterval: "
											+ va.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getExpectedHeadwayInterval());

							if (va.getMonitoredVehicleJourney()
									.getOnwardCalls().getExtensions() != null) {
								System.out
										.println("OnwardCalls.Extensions.Distances.PresentableDistance: "
												+ va.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getPresentableDistance());
								System.out
										.println("OnwardCalls.Extensions.Distances.DistanceFromCall: "
												+ va.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getDistanceFromCall());
								System.out
										.println("OnwardCalls.Extensions.Distances.StopsFromCall: "
												+ va.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getStopsFromCall());
								System.out
										.println("OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: "
												+ va.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getCallDistanceAlongRoute());
							}
						}

						System.out.println("RecordedAtTime: "
								+ va.getRecordedAtTime());
						System.out.println("------------------------");
					}
				}

				System.out.println("ResponseTimestamp: "
						+ vmd.getResponseTimestamp());
				System.out.println("ValidUntil: " + vmd.getValidUntil());
			}
		}

		System.out.println("------------------------------------------");

		System.out.println("------------------------------------------");
		System.out.println("-        Stop Monitoring Delivery:       -");
		System.out.println("------------------------------------------");

		List<StopMonitoringDelivery> listSMD = siri.getServiceDelivery()
				.getStopMonitoringDelivery();

		if (listSMD != null) {

			for (StopMonitoringDelivery smd : listSMD) {

				List<MonitoredStopVisit> msvList = smd.getMonitoredStopVisit();

				if (msvList != null) {

					for (MonitoredStopVisit msv : msvList) {

						System.out.println("----------------------------");
						System.out.println("-   Monitored Stop Visit:  -");
						System.out.println("----------------------------");

						System.out
								.println("LineRef: "
										+ msv.getMonitoredVehicleJourney()
												.getLineRef());
						System.out.println("DirectionRef: "
								+ msv.getMonitoredVehicleJourney()
										.getDirectionRef());
						System.out
								.println("FramedVehicleJourneyRef.DataFrameRef: "
										+ msv.getMonitoredVehicleJourney()
												.getFramedVehicleJourneyRef()
												.getDataFrameRef());
						System.out
								.println("FramedVehicleJourneyRef.DatedVehicleJourneyRef: "
										+ msv.getMonitoredVehicleJourney()
												.getFramedVehicleJourneyRef()
												.getDatedVehicleJourneyRef());
						System.out.println("JourneyPatternRef: "
								+ msv.getMonitoredVehicleJourney()
										.getJourneyPatternRef());
						System.out.println("PublishedLineName: "
								+ msv.getMonitoredVehicleJourney()
										.getPublishedLineName());
						System.out.println("OperatorRef: "
								+ msv.getMonitoredVehicleJourney()
										.getOperatorRef());
						System.out.println("OriginRef: "
								+ msv.getMonitoredVehicleJourney()
										.getOriginRef());
						System.out.println("DestinationRef: "
								+ msv.getMonitoredVehicleJourney()
										.getDestinationRef());
						System.out.println("DestinationName: "
								+ msv.getMonitoredVehicleJourney()
										.getDestinationName());

						System.out.println("------------------");
						System.out.println("- Situation Ref: -");
						System.out.println("------------------");
						List<SituationRef> srList = msv
								.getMonitoredVehicleJourney().getSituationRef();
						if (srList != null) {
							for (SituationRef sr : srList) {
								System.out.println("SituationRef: "
										+ sr.getSituationSimpleRef());
								System.out.println("SituationRef.FullRef: "
										+ sr.getSituationFullRef());

							}
						}
						System.out.println("----------------");

						System.out.println("Monitored: "
								+ msv.getMonitoredVehicleJourney()
										.isMonitored());
						System.out.println("VehicleLocation.Longitude: "
								+ msv.getMonitoredVehicleJourney()
										.getVehicleLocation().getLongitude());
						System.out.println("VehicleLocation.Latitude: "
								+ msv.getMonitoredVehicleJourney()
										.getVehicleLocation().getLatitude());
						System.out
								.println("Bearing: "
										+ msv.getMonitoredVehicleJourney()
												.getBearing());
						System.out.println("ProgressRate: "
								+ msv.getMonitoredVehicleJourney()
										.getProgressRate());
						System.out.println("ProgressStatus: "
								+ msv.getMonitoredVehicleJourney()
										.getProgressStatus());
						System.out.println("BlockRef: "
								+ msv.getMonitoredVehicleJourney()
										.getBlockRef());
						System.out.println("VehicleRef: "
								+ msv.getMonitoredVehicleJourney()
										.getVehicleRef());

						if (msv.getMonitoredVehicleJourney().getMonitoredCall() != null) {
							System.out
									.println("MonitoredCall.Extensions.Distances.PresentableDistance: "
											+ msv.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getPresentableDistance());
							System.out
									.println("MonitoredCall.Extensions.Distances.DistanceFromCall: "
											+ msv.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getDistanceFromCall());
							System.out
									.println("MonitoredCall.Extensions.Distances.StopsFromCall: "
											+ msv.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getStopsFromCall());
							System.out
									.println("MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: "
											+ msv.getMonitoredVehicleJourney()
													.getMonitoredCall()
													.getExtensions()
													.getDistances()
													.getCallDistanceAlongRoute());
							System.out.println("MonitoredCall.StopPointRef: "
									+ msv.getMonitoredVehicleJourney()
											.getMonitoredCall()
											.getStopPointRef());
							System.out.println("MonitoredCall.VisitNumber: "
									+ msv.getMonitoredVehicleJourney()
											.getMonitoredCall()
											.getVisitNumber());
							System.out.println("MonitoredCall.StopPointName: "
									+ msv.getMonitoredVehicleJourney()
											.getMonitoredCall()
											.getStopPointName());
						}

						if (msv.getMonitoredVehicleJourney().getOnwardCalls() != null) {
							System.out.println("OnwardCalls.AimedArrivalTime: "
									+ msv.getMonitoredVehicleJourney()
											.getOnwardCalls()
											.getAimedArrivalTime());
							System.out
									.println("OnwardCalls.AimedDepartureTime: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getAimedDepartureTime());
							System.out
									.println("OnwardCalls.AimedHeadwayInterval: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getAimedHeadwayInterval());
							System.out
									.println("OnwardCalls.ArrivalBoardingActivity: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getArrivalBoardingActivity());
							System.out
									.println("OnwardCalls.ArrivalPlatformName: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getArrivalPlatformName());
							System.out.println("OnwardCalls.ArrivalStatus: "
									+ msv.getMonitoredVehicleJourney()
											.getOnwardCalls()
											.getArrivalStatus());
							System.out
									.println("OnwardCalls.DepartureBoardingActivity: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getDepartureBoardingActivity());
							System.out
									.println("OnwardCalls.DeparturePlatformName: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getDeparturePlatformName());
							System.out.println("OnwardCalls.DepartureStatus: "
									+ msv.getMonitoredVehicleJourney()
											.getOnwardCalls()
											.getDepartureStatus());
							System.out
									.println("OnwardCalls.ExpectedArrivalTime: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getExpectedArrivalTime());
							System.out
									.println("OnwardCalls.ExpectedDepartureTime: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getExpectedDepartureTime());
							System.out
									.println("OnwardCalls.ExpectedHeadwayInterval: "
											+ msv.getMonitoredVehicleJourney()
													.getOnwardCalls()
													.getExpectedHeadwayInterval());

							if (msv.getMonitoredVehicleJourney()
									.getOnwardCalls().getExtensions() != null) {
								System.out
										.println("OnwardCalls.Extensions.Distances.PresentableDistance: "
												+ msv.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getPresentableDistance());
								System.out
										.println("OnwardCalls.Extensions.Distances.DistanceFromCall: "
												+ msv.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getDistanceFromCall());
								System.out
										.println("OnwardCalls.Extensions.Distances.StopsFromCall: "
												+ msv.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getStopsFromCall());
								System.out
										.println("OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: "
												+ msv.getMonitoredVehicleJourney()
														.getOnwardCalls()
														.getExtensions()
														.getDistances()
														.getCallDistanceAlongRoute());
							}
						}

						System.out.println("RecordedAtTime: "
								+ msv.getRecordedAtTime());
						System.out.println("------------------------");
					}
				}

				System.out.println("ResponseTimestamp: "
						+ smd.getResponseTimestamp());
				System.out.println("ValidUntil: " + smd.getValidUntil());
			}

		}

		System.out.println("------------------------------------------");

		System.out.println("------------------------------------------");
		System.out.println("-    Situation Exchange Delivery:        -");
		System.out.println("------------------------------------------");

		List<SituationExchangeDelivery> sedList = siri.getServiceDelivery()
				.getSituationExchangeDelivery();

		if (sedList != null) {
			for (SituationExchangeDelivery sed : sedList) {
				List<PtSituationElement> ptseList = sed.getSituations()
						.getPtSituationElement();

				System.out.println("----------------------------");
				System.out.println("-     PtSituationElement:  -");
				System.out.println("----------------------------");

				for (PtSituationElement ptse : ptseList) {
					System.out
							.println("PtSituationElement.PublicationWindow.StartTime: "
									+ ptse.getPublicationWindow()
											.getStartTime());
					System.out
							.println("PtSituationElement.PublicationWindow.EndTime: "
									+ ptse.getPublicationWindow().getEndTime());
					System.out.println("PtSituationElement.Severity: "
							+ ptse.getSeverity());
					System.out.println("PtSituationElement.Summary.Lang: "
							+ ptse.getSummary().getLang());
					System.out.println("PtSituationElement.Summary.Value: "
							+ ptse.getSummary().getValue());
					System.out.println("PtSituationElement.Summary.toString: "
							+ ptse.getSummary().toString());
					System.out.println("PtSituationElement.Description.Lang: "
							+ ptse.getDescription().getLang());
					System.out.println("PtSituationElement.Description.Value: "
							+ ptse.getDescription().getValue());
					System.out
							.println("PtSituationElement.Description.toString: "
									+ ptse.getDescription().toString());

					List<AffectedVehicleJourney> avjList = ptse.getAffects()
							.getVehicleJourneys().getAffectedVehicleJourney();

					for (AffectedVehicleJourney avj : avjList) {

						System.out.println("---------------------------");
						System.out.println("- AffectedVehicleJounrey: -");
						System.out.println("---------------------------");
						System.out.println("LineRef: " + avj.getLineRef()); // TODO
																			// -
																			// check
																			// this
																			// output
						System.out.println("DirectionRef: "
								+ avj.getDirectionRef()); // TODO - check this
															// output
					}

					System.out.println("---------------------------");

					List<PtConsequence> ptConList = ptse.getConsequences()
							.getConsequence(); // TODO - check this output

					for (PtConsequence ptCon : ptConList) {
						System.out.println("----------------------");
						System.out.println("-    PtConsequences: -");
						System.out.println("----------------------");
						System.out.println("Condition: "
								+ ptCon.getCondition().toString());
					}

					System.out.println("----------------------");

					System.out.println("PtSituationElement.SituationNumber: "
							+ ptse.getSituationNumber()); // TODO - check this
															// output
				}
				System.out.println("----------------------------");
			}
		}
		System.out.println("------------------------------------------");
	}

	/**
	 * Returns a benchmark of the amount of time the last cache read took for
	 * the ObjectMapper or ObjectReader or XmlReader (in nanoseconds)
	 * 
	 * @return a benchmark of the amount of time the last cache read took for
	 *         the ObjectMapper or ObjectReader or XmlReader (in nanoseconds)
	 */
	public static long getLastCacheReadTime() {
		return cacheReadEndTime - cacheReadStartTime;
	}

	/**
	 * Returns a benchmark of the amount of time the last cache write took for
	 * the ObjectMapper or ObjectReader or XmlReader (in nanoseconds)
	 * 
	 * @return a benchmark of the amount of time the last cache write took for
	 *         the ObjectMapper or ObjectReader or XmlReader (in nanoseconds)
	 */
	public static long getLastCacheWriteTime() {
		return cacheWriteEndTime - cacheWriteStartTime;
	}

	/**
	 * Forces the write of a ObjectMapper, ObjectReader, or XmlMapper to the app
	 * cache. The cache is used to reduce the cold-start delay for Jackson
	 * parsing on future runs, after this VM instance is destroyed.
	 * 
	 * Applications may call this after a JSON or XML call to the server to
	 * attempt to hide the cache write latency from the user, instead of having
	 * the cache write occur as part of the first request to use the
	 * ObjectMapper, ObjectReader, or XmlMapper.
	 * 
	 * This method is non-blocking.
	 * 
	 * @param instance
	 *            of object to be written to the cache
	 */
	public static void forceCacheWrite(final Serializable object) {
		new Thread() {
			public void run() {
				writeToCache(object);
			};
		}.start();
	}

	/**
	 * Write the given object to Android internal storage for this app
	 * 
	 * @param object
	 *            serializable object to be written to cache (ObjectReader,
	 *            ObjectMapper, or XmlReader)
	 * @return true if object was successfully written to cache, false if it was
	 *         not
	 */
	private synchronized static boolean writeToCache(Serializable object) {

		FileOutputStream fileStream = null;
		ObjectOutputStream objectStream = null;
		String fileName = "";
		boolean success = false;

		try {
			if (object instanceof XmlMapper) {
				fileName = XML_MAPPER + CACHE_FILE_EXTENSION;
			}
			if (object instanceof ObjectMapper) {
				fileName = OBJECT_MAPPER + CACHE_FILE_EXTENSION;
			}
			if (object instanceof ObjectReader) {
				fileName = OBJECT_READER + CACHE_FILE_EXTENSION;
			}

			cacheWriteStartTime = System.nanoTime();
			fileStream = new FileOutputStream(fileName);
			objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(object);
			objectStream.flush();
			fileStream.getFD().sync();
			cacheWriteEndTime = System.nanoTime();
			success = true;

			// Get size of serialized object
			File file = new File(fileName);

			long fileSize = file.length();

			System.out.println("Wrote " + fileName + " to cache (" + fileSize
					+ " bytes) in " + df.format(getLastCacheWriteTime()/ 1000000.0)
					+ " ms.");
		} catch (IOException e) {
			// Reset timestamps to show there was an error
			cacheWriteStartTime = 0;
			cacheWriteEndTime = 0;
			System.out.println("Couldn't write object to cache: " + e);
			e.printStackTrace();
		} finally {
			try {
				if (objectStream != null) {
					objectStream.close();
				}
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (Exception e) {
				System.out.println("Error closing file connections: " + e);
			}
		}

		return success;
	}

	/**
	 * Read the given object from Android internal storage for this app
	 * 
	 * @param objectType
	 *            object type, defined by class constant Strings, to retrieve
	 *            from cache (ObjectReader, ObjectMapper, or XmlReader)
	 * 
	 * @return deserialized Object, or null if object couldn't be deserialized
	 */
	public static synchronized Serializable readFromCache(String objectType) {

		FileInputStream fileStream = null;
		ObjectInputStream objectStream = null;

		// Holds object to be read from cache
		Serializable object = null;

		try {
			String fileName = objectType + CACHE_FILE_EXTENSION;

			File file = new File(fileName);

			cacheReadStartTime = System.nanoTime();
			fileStream = new FileInputStream(file);
			objectStream = new ObjectInputStream(fileStream);
			object = (Serializable) objectStream.readObject();
			cacheReadEndTime = System.nanoTime();

			// Get size of serialized object
			long fileSize = file.length();

			System.out.println("Read " + fileName + " from cache (" + fileSize
					+ " bytes) in " + df.format(getLastCacheReadTime()/ 1000000.0)
					+ " ms.");
		} catch (FileNotFoundException e) {
			System.out.println("Cache miss - Jackson object '" + objectType
					+ "' does not exist in app cache: " + e);
			return null;
		} catch (Exception e) {
			// Reset timestamps to show there was an error
			cacheReadStartTime = 0;
			cacheReadEndTime = 0;
			System.out.println("Couldn't read Jackson object '" + objectType
					+ "' from cache: " + e);
		} finally {
			try {
				if (objectStream != null) {
					objectStream.close();
				}
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (Exception e) {
				System.out
						.println("Error closing cache file connections: " + e);
			}
		}

		return object;
	}
}
