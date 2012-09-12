
package uk.org.siri.siri;

import java.util.Date;

public class VehicleMonitoringDelivery{
   	
	private Date responseTimestamp;
   	private Date validUntil;
   	   	   	
 	public Date getResponseTimestamp(){
		return this.responseTimestamp;
	}
   	
	public void setResponseTimestamp(Date responseTimestamp){
		this.responseTimestamp = responseTimestamp;
	}
   	
 	public Date getValidUntil(){
		return this.validUntil;
	}
   	
	public void setValidUntil(Date validUntil){
		this.validUntil = validUntil;
	}   	
}
