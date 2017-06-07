package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unisystems.alpha.poc.apimanagment.integration.model.TransferResponse;

public class UnmarshallAlphaBalanceTransferResponseProcessor implements Processor {

	final Logger logger = LoggerFactory.getLogger("UnmarshallAlphaBalanceTransactionsResponseProcessor");
	private static int cnt =1;
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		
		exchange.getIn().setBody(new TransferResponse(sdf.format(new Date()), ""+cnt++ , "COMPLETED", "00", "Success"));
		exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 201);
	}

}
