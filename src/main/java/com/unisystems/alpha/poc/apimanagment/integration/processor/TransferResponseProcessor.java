package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unisystems.alpha.poc.apimanagment.integration.model.TransferResponse;

public class TransferResponseProcessor implements Processor {

	final Logger logger = LoggerFactory.getLogger("UnmarshallAlphaBalanceTransactionsResponseProcessor");
	private static int cnt =1;
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		
		String status = exchange.getIn().getHeader("custom-transferStatus").toString();
		String exportedStatus = "ACCP".equals(status)?"ACCEPTED":"RJCT".equals(status)?"REJECTED":"PDNG".equals(status)?"PENDING":"UNKNOWN";
		exchange.getIn().setBody(new TransferResponse(exchange.getIn().getHeader("custom-transferExecutionDate").toString(), exchange.getIn().getHeader("custom-transferId").toString() , exportedStatus, exchange.getIn().getHeader("custom-transferStatusCode").toString(), exchange.getIn().getHeader("custom-transferAdditionalInfo").toString()));
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
	}

}
