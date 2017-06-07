package com.unisystems.alpha.poc.apimanagment.integration;

import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.unisystems.alpha.poc.apimanagment.integration.processor.UnmarshallAlphaBalanceResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.UnmarshallAlphaBalanceTransactionsResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.UnmarshallAlphaBalanceTransferResponseProcessor;

@Component	
public class CamelRouter extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		restConfiguration()
        	.component("servlet")
        	.bindingMode(RestBindingMode.json)
        	.dataFormatProperty("prettyPrint", "true");
		
		rest("/api/{accountId}")         
     		.get("/balance")
	     		.produces(MediaType.APPLICATION_JSON)
	     		 .route()
	     		.to("direct:getAccountBalance")
	     		.endRest()
	     	.get("/transactions")
	     		.produces(MediaType.APPLICATION_JSON)
	            .route()
	            .to("direct:getAccountTransactions")
	            .endRest()
	         .post("/transfer")
	         	.consumes(MediaType.APPLICATION_JSON)
	         	.produces(MediaType.APPLICATION_JSON)	         	
	            .route()
	            .to("direct:transferAmount")
	            .endRest();
		
		
		from("direct:getAccountBalance")
			.log(">>> getAccountBalance of account: ${header.accountId} ")		
    		.process(new UnmarshallAlphaBalanceResponseProcessor()).id("UnmarshallAlphaResponseProcessor");
    
		from("direct:getAccountTransactions")
			.log(">>> direct:getAccountTransactions of account: ${header.accountId} from date ${header.obp_from_date} to ${header.obp_to_date}")
			.process(new UnmarshallAlphaBalanceTransactionsResponseProcessor()).id("UnmarshallAlphaTransactionsResponseProcessor");
		
		from("direct:transferAmount")
			.log(">>> direct:transferAmount of account: ${header.accountId}")
			.process(new UnmarshallAlphaBalanceTransferResponseProcessor()).id("UnmarshallAlphaBalanceTransferResponseProcessor");

	}

}
