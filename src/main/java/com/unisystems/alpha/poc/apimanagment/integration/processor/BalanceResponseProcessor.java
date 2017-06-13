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
		
		exchange.getIn().setBody(new Account(exchange.getProperty("reqId").toString(), exchange.getProperty("accountId").toString(), "OK", exchange.getIn().getHeader("custom-bic").toString(), "alpha",exchange.getIn().getHeader("custom-responseId").toString(), new Balance(exchange.getIn().getHeader("custom-balanceCurrency").toString(), exchange.getIn().getHeader("custom-balanceAmount").toString()) ));

	}

}
