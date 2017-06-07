package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unisystems.alpha.poc.apimanagment.integration.model.Account;
import com.unisystems.alpha.poc.apimanagment.integration.model.AmountOfMoney;
import com.unisystems.alpha.poc.apimanagment.integration.model.Transaction;
import com.unisystems.alpha.poc.apimanagment.integration.model.TransactionDetails;

public class UnmarshallAlphaBalanceTransactionsResponseProcessor implements Processor {

	final Logger logger = LoggerFactory.getLogger("UnmarshallAlphaBalanceTransactionsResponseProcessor");
	
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		logger.info("header info: " + exchange.getIn().getHeader("obp_from_date").toString() + "," + exchange.getIn().getHeader("obp_to_date").toString());
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		transactions.add(new Transaction("1", new Account("1111", exchange.getIn().getHeader("accountId").toString(), null, "CRBAGRAA", null, "1", null),  new Account("2222", "other_account1", null, "CRBAGRAA", null, "2", null), new TransactionDetails(new AmountOfMoney("100,45", "EUR"), "GOOD", "Tue Jan 22 00:08:00 UTC 2017", "AC", new AmountOfMoney("10035,65", "EUR"), "Tue Jan 22 00:08:00 UTC 2017")));
		transactions.add(new Transaction("2", new Account("1111", exchange.getIn().getHeader("accountId").toString(), null, "CRBAGRAA", null, "1", null),  new Account("3333", "other_account2", null, "CRBAGRAA", null, "3", null), new TransactionDetails(new AmountOfMoney("650.5", "EUR"), "GOOD", "Tue Jan 22 00:08:00 UTC 2017", "AC", new AmountOfMoney("1035", "EUR"), "Tue Jan 22 00:08:00 UTC 2017")));
		exchange.getIn().setBody(transactions);

	}

}
