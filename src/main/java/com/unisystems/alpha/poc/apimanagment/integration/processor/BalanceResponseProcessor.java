package com.unisystems.alpha.poc.apimanagment.integration.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unisystems.alpha.poc.apimanagment.integration.model.Account;
import com.unisystems.alpha.poc.apimanagment.integration.model.Balance;

public class BalanceResponseProcessor implements Processor {

	final Logger logger = LoggerFactory.getLogger("UnmarshallAlphaResponseProcessor");
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String errorDescr = (String) exchange.getIn().getHeader("custom-errorDescr");
		if(errorDescr!=null && errorDescr.trim().length()>0) {
			
			if(errorDescr.contains("Wrong Account")) {
				
				exchange.getIn().setBody("OBP-30018: Bank Account not found. Please specify valid values for ACCOUNT_ID.");
	        	exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
	        	return;
	        	
			} else if(errorDescr.contains("έληξε")) {
				exchange.getIn().setBody("Unauthorized Access:OBP-20001: User not logged in. Authentication is required.");
	        	exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
	        	return;
			} else {
				exchange.getIn().setBody("Server Error");
	        	exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 500);
	        	return;
			}
		}
		exchange.getIn().setBody(new Account(exchange.getProperty("reqId").toString(), exchange.getProperty("accountId").toString(), "OK", exchange.getIn().getHeader("custom-bic").toString(), "alpha",exchange.getIn().getHeader("custom-responseId").toString(), new Balance(exchange.getIn().getHeader("custom-balanceCurrency").toString(), exchange.getIn().getHeader("custom-balanceAmount").toString()) ));

	}

}
