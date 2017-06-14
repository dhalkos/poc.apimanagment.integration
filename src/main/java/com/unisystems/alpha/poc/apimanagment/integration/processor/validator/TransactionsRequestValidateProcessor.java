package com.unisystems.alpha.poc.apimanagment.integration.processor.validator;

import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.unisystems.alpha.poc.apimanagment.integration.exception.InvalidRequestException;

public class TransactionsRequestValidateProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws InvalidRequestException, Exception {

		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateTimeSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		
		
		
		String fromDateInput=null , toDateInput=null;
		try {
			fromDateInput =  exchange.getIn().getHeader("obp_from_date").toString();
			toDateInput = exchange.getIn().getHeader("obp_to_date").toString();
		} catch(Exception e) {
			exchange.getIn().setBody("OBP-10001:header obp_from_date and obp_to_date fields are required");			
			throw new InvalidRequestException("OBP-10001:header obp_from_date and obp_to_date fields are required");
		}
		
		
		
		try {
			
			String fromDate = dateSdf.format(dateSdf.parse(fromDateInput));
			String fromTime = "00:00:00.0000000+03:00";
			
			try {
				dateTimeSdf.format(dateTimeSdf.parse(fromDateInput));
				fromTime = fromDateInput.substring(11);
			} catch(Exception e) {
				if(fromDateInput.length()>10)
					throw new Exception();
				
			}
						
			String toDate = dateSdf.format(dateSdf.parse(toDateInput));
			String toTime = "23:59:59.0000000+03:00";
			try {
				dateTimeSdf.format(dateTimeSdf.parse(toDateInput));
				toTime = toDateInput.substring(11);
			} catch(Exception e) {
				if(toDateInput.length()>10)
					throw new Exception();
				
			}
			
			exchange.getIn().setHeader("fromDate", fromDate);
			exchange.getIn().setHeader("fromTime", fromTime);
			exchange.getIn().setHeader("toDate", toDate);
			exchange.getIn().setHeader("toTime", toTime);
			
		} catch (Exception e) {
			exchange.getIn().setBody("OBP-10001:header obp_from_date and obp_to_date fields format should be yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			throw new InvalidRequestException("OBP-10001:header obp_from_date and obp_to_date fields format should be yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		}
		
		
	}

}
