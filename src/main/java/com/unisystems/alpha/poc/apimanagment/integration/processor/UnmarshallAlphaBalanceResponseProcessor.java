package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.math.BigDecimal;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unisystems.alpha.poc.apimanagment.integration.model.Account;
import com.unisystems.alpha.poc.apimanagment.integration.model.Balance;

public class UnmarshallAlphaBalanceResponseProcessor implements Processor {

	final Logger logger = LoggerFactory.getLogger("UnmarshallAlphaResponseProcessor");
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		logger.info("unmarshalling alpha response");
		
		exchange.getOut().setBody(new Account("123", exchange.getIn().getHeader("accountId").toString(), "None", "CRBAGRAA", "alpha","101002101047414", new Balance("EUR", new BigDecimal(10)) ));

	}

}
