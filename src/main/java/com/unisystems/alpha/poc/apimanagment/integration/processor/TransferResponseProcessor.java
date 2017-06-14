package com.unisystems.alpha.poc.apimanagment.integration.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import com.unisystems.alpha.poc.apimanagment.integration.model.TransferResponse;

public class TransferResponseProcessor implements Processor {

	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		String errorCode = exchange.getIn().getHeader("custom-errorCode").toString();
		
		if(StringUtils.isNotBlank("errorCode") && errorCode.trim().length()>0) {
			
			String errorMsg = exchange.getIn().getHeader("custom-errorMsg").toString();
			exchange.getIn().setBody(errorMsg);
			
			if("1230".equals(errorCode)) {
				
				if(errorMsg.contains("έληξε")) {
					exchange.getIn().setBody("Unauthorized Access:OBP-20001: User not logged in. Authentication is required.");
		        	exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
				} else {
					exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 409);
					exchange.getIn().setBody("OBP-30106: Insufficient funds in account.");
				}
				
			} else if("1000".equals(errorCode))
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			else
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
			
			return;
			
		}
		
		try {
			String status = exchange.getIn().getHeader("custom-transferStatus").toString();
			String exportedStatus = "ACCP".equals(status)?"ACCEPTED":"RJCT".equals(status)?"REJECTED":"PDNG".equals(status)?"PENDING":"UNKNOWN";
			exchange.getIn().setBody(new TransferResponse(exchange.getIn().getHeader("custom-transferExecutionDate").toString(), exchange.getIn().getHeader("custom-transferId").toString() , exportedStatus, exchange.getIn().getHeader("custom-transferStatusCode").toString(), exchange.getIn().getHeader("custom-transferAdditionalInfo").toString()));
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
