package uk.org.siri.siri;

import java.util.Date;
import java.util.List;

public class ServiceDelivery {
	private Date responseTimestamp;
	private List<VehicleMonitoringDelivery> vehicleMonitoringDelivery;
				
	public Date getResponseTimestamp() {
		return this.responseTimestamp;
	}

	
	public void setResponseTimestamp(Date responseTimestamp) {
		this.responseTimestamp = responseTimestamp;
	}

	
	public List<VehicleMonitoringDelivery> getVehicleMonitoringDelivery() {
		return this.vehicleMonitoringDelivery;
	}

	
	public void setVehicleMonitoringDelivery(
			List<VehicleMonitoringDelivery> vehicleMonitoringDelivery) {
		this.vehicleMonitoringDelivery = vehicleMonitoringDelivery;
	}	

}
