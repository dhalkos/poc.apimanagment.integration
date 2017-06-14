package com.unisystems.alpha.poc.apimanagment.integration;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.unisystems.alpha.poc.apimanagment.integration.beans.RequestCounter;
import com.unisystems.alpha.poc.apimanagment.integration.exception.InvalidRequestException;
import com.unisystems.alpha.poc.apimanagment.integration.exception.UnauthorizedException;
import com.unisystems.alpha.poc.apimanagment.integration.processor.BalanceResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.TransactionsResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.TransferResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.UrlEncoderProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.XmlEncryptionProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.XmlResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.validator.TransactionsRequestValidateProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.validator.TransferRequestValidateProcessor;

@Component	
public class CamelRouter extends RouteBuilder {

	private HashMap<String, String> map = new HashMap<String, String>();
	
	
	@Override
	public void configure() throws Exception {
		
		//dhalk params
		//map.put("server","localhost:8443");
		//map.put("trustore", "dhalkosTrustore1.jks");
				
		//alpha params
		map.put("server","webtest.alpha.gr");
		map.put("trustore", "new_alphaTrustore.jks");
		
		
		configureHttpClient(getContext());
		
		
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
		
		//onException(Exception.class).maximumRedeliveries(0).logStackTrace(true	).to("direct:serverError");
		
		from("direct:getAccountBalance").routeId("account-balance")
			.log(">>> balance API route started")			
			.onException(Exception.class)
				.maximumRedeliveries(0)
				.handled(true)	
				.to("direct:serverError")
			.end()
			.choice()				
				.when().simple("${header.Authorization} == null").to("direct:unauthorized")
				.otherwise()
			//		.doTry()
						.setProperty("reqId").method(RequestCounter.class, "getBalanceRequestId")
						.setProperty("accountId", simple("${header.accountId}"))
						.setHeader("currentDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"))
						.to("velocity:templates/BalanceTemplate.vm?contentCache=true")
						.log(">>>>>>>>>XML REQUEST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
						.log("${body}")
						.process(new XmlEncryptionProcessor()).id("balance-XmlEncryptionProcessor")
						.setBody(body().regexReplaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", ""))
						.setHeader("custom-Content-Type", simple("${header.Content-Type}"))
						.removeHeaders("CamelHttp*")
						//.to("log:DEBUG?showHeaders=true")
						.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST.name()))
						.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
						.setHeader(Exchange.CONTENT_ENCODING, constant("UTF-8"))
						.setHeader(Exchange.HTTP_URI, simple("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/balance"))			
						.process(new UrlEncoderProcessor()).id("balance-UrlEncoderProcessor")
						.setBody(simple("Initiation=${body}&DateToPost=${date:now:yyyyMMdd}&DeveloperId=Unisystems-dhalk&Token=123456&UniqueIdentifier=${exchangeProperty.reqId}"))
						//.log(">>> xml request encrypteed without new lines message is:\n${body}")			
						.to("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/balance?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&disableStreamCache=false")
						.convertBodyTo(String.class)
						.process(new XmlResponseProcessor()).id("balance-XmlResponseProcessor")				
						//.log(">>> xml response message is:\n ${body} ")
						.setHeader("Content-Type", simple("${header.custom-Content-Type}"))
						.to("xslt:xslt/removeNamespaces.xsl?")
						.log(">>>>>>>>>XML RESPONSE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
						.log("${body}")
						.setHeader("custom-errorDescr", xpath("/Document/BkToCstmrAcctRpt/Rpt/AddtlRptInf/text()").resultType(String.class))
						.setHeader("custom-balanceAmount", xpath("/BankToCustomerAccountReportV06/Rpt/Bal/Amt/text()").resultType(String.class))
						.setHeader("custom-balanceCurrency", constant("EUR")) //xpath("/BankToCustomerAccountReportV06/Rpt/Bal/Amt/@Ccy/text()").resultType(String.class))
						.setHeader("custom-bic", xpath("/BankToCustomerAccountReportV06/Rpt/Acct/Svcr/FinInstnId/BICFI/text()").resultType(String.class))
						.setHeader("custom-responseId", xpath("/BankToCustomerAccountReportV06/Rpt/Id/text()").resultType(String.class))
			    		.process(new BalanceResponseProcessor()).id("UnmarshallAlphaResponseProcessor")
			    		.removeHeaders("custom-*")
			    	/*.doCatch(Exception.class)
						.log("${body}")
						.to("direct:serverError")*/
		    .end();
    
		
		
		
		
		
		from("direct:getAccountTransactions").routeId("account-transactions")			
			.onException(InvalidRequestException.class)
				.maximumRedeliveries(0)
				.handled(true)
				.log(">>>>>>>>>>>>>>>>>>>>>>>>InvalidRequestException>>>>>>>>>>>>>>>>>>>\n${body}")
				.log("${body}")
				.to("direct:badRequest")
			.end()
			.onException(Exception.class)
				.maximumRedeliveries(0)
				.handled(true)
				 .transform(exceptionMessage())
				.log(">>>>>>>>>>>>>>>>>>>>>>>>Exception>>>>>>>>>>>>>>>>>>>\n${body}")
				.to("direct:serverError")
			.end()
			.log(">>> transactions API route started")
			.choice()				
				.when().simple("${header.Authorization} == null").to("direct:unauthorized")				
				.otherwise()
						.process(new TransactionsRequestValidateProcessor()).id("transactions-Request-Validator")
						.setProperty("reqId").method(RequestCounter.class, "getTransactionsRequestId")
						.setHeader("currentDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"))
						.to("velocity:templates/TransactionsTemplate.vm?contentCache=true")
						.log(">>>>>>>>>XML REQUEST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
						.log("${body}")						
						.process(new XmlEncryptionProcessor()).id("transactions-XmlEncryptionProcessor")
						.setBody(body().regexReplaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", ""))						
						.setHeader("custom-Content-Type", simple("${header.Content-Type}"))
						.removeHeaders("CamelHttp*")
						.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST.name()))
						.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
						.setHeader(Exchange.CONTENT_ENCODING, constant("UTF-8"))
						.setHeader(Exchange.HTTP_URI, simple("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/statement"))			
						.process(new UrlEncoderProcessor()).id("transactions-UrlEncoderProcessor")
						.setBody(simple("Initiation=${body}&DateToPost=${date:now:yyyyMMdd}&DeveloperId=Unisystems-dhalk&Token=123456&UniqueIdentifier=${exchangeProperty.reqId}"))						
						.to("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/statement?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&disableStreamCache=true")
						.convertBodyTo(String.class)
						.log(">>>>>>>>>XML RESPONSE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
						.log("${body}")
						.process(new XmlResponseProcessor()).id("transactions-XmlResponseProcessor")
						.setHeader("Content-Type", simple("${header.custom-Content-Type}"))					
						.to("xslt:xslt/removeNamespaces.xsl?")
						.process(new TransactionsResponseProcessor()).id("UnmarshallAlphaTransactionsResponseProcessor")
			.end();
		
		
		
		from("direct:transferAmount").routeId("account-transfer")
			.streamCaching()
			.onException(InvalidRequestException.class)
				.maximumRedeliveries(0)
				.handled(true)
				.transform(exceptionMessage())
				.log(">>>>>>>>>>>>>>>>>>>>>>>>InvalidRequestException>>>>>>>>>>>>>>>>>>>\n${body}")
				.log("${body}")
				.to("direct:badRequest")
			.end()
			.onException(UnauthorizedException.class)
				.maximumRedeliveries(0)
				.handled(true)
				 .transform(exceptionMessage())
				.log(">>>>>>>>>>>>>>>>>>>>>>>>UnauthorizedException>>>>>>>>>>>>>>>>>>>\n${body}")
				.to("direct:unauthorized")
			.end()
			.onException(Exception.class)
				.maximumRedeliveries(0)
				.handled(true)
				 .transform(exceptionMessage())
				.log(">>>>>>>>>>>>>>>>>>>>>>>>Exception>>>>>>>>>>>>>>>>>>>\n${body}")
				.to("direct:serverError")
			.end()
			.log(">>> transfer API route started")
			.choice()				
				.when().simple("${header.Authorization} == null").to("direct:unauthorized")								
				.otherwise()					
					.setProperty("reqId").method(RequestCounter.class, "getTransferRequestId")					
					.setHeader("currentDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"))					
					//.process(new TransferRequestValidatorProcessor()).id("transfer-Request-Validator")
					.process(new Processor() {
						
						@Override
						public void process(Exchange exchange) throws Exception {
							
							try {
								String authorization_header = exchange.getIn().getHeader("Authorization").toString();
								String token = authorization_header.split(" ")[1];
								DecodedJWT jwt = JWT.decode(token);
								String userId = jwt.getClaim("alpha_session_id").asString();
								exchange.getIn().setHeader("userAccountId", userId);
							} catch (Exception e) {
								throw new UnauthorizedException(e.getMessage());
							}
						}
						
						
					})
					.to("velocity:templates/TransferTemplate.vm?contentCache=true&encoding=utf-8")		
					.log(">>>>>>>>>XML REQUEST>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
					.log("${body}")
					.process(new TransferRequestValidateProcessor())
					.process(new XmlEncryptionProcessor()).id("transfer-XmlEncryptionProcessor")
					.setBody(body().regexReplaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", ""))			
					.setHeader("custom-Content-Type", simple("${header.Content-Type}"))
					.removeHeaders("CamelHttp*")
					.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST.name()))
					.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
					.setHeader(Exchange.CONTENT_ENCODING, constant("UTF-8"))			
					.setHeader(Exchange.HTTP_URI, simple("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/transfer"))
					.process(new UrlEncoderProcessor()).id("transfer-UrlEncoderProcessor")
					.setBody(simple("Initiation=${body}&DateToPost=${date:now:yyyyMMdd}&DeveloperId=Unisystems-dhalk&Token=123456&UniqueIdentifier=${exchangeProperty.reqId}"))						
					.to("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/transfer?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&disableStreamCache=true")						
					.convertBodyTo(String.class)
					.log(">>>>>>>>>XML RESPONSE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
					.log("${body}")
					.process(new XmlResponseProcessor()).id("transfer-XmlResponseProcessor")					
					.to("xslt:xslt/removeNamespaces.xsl?")
					.setHeader("Content-Type", simple("${header.custom-Content-Type}"))
					.setHeader("custom-errorCode", xpath("/S2S_TransferReply/ErrorResult/DetailErrorCode/text()").resultType(String.class))
					.setHeader("custom-errorMsg", xpath("/S2S_TransferReply/ErrorResult/ErrorMessage/text()").resultType(String.class))
					.setHeader("custom-transferExecutionDate", xpath("/CustomerPaymentStatusReportV03/GrpHdr/CreDtTm/text()").resultType(String.class))			
					.setHeader("custom-transferId", xpath("/CustomerPaymentStatusReportV03/OrgnlPmtInfAndSts/OrgnlPmtInfId/text()").resultType(String.class))
					.setHeader("custom-transferStatus", xpath("/CustomerPaymentStatusReportV03/OrgnlPmtInfAndSts/TxInfAndSts/TxSts/text()").resultType(String.class))
					.setHeader("custom-transferStatusCode", xpath("/CustomerPaymentStatusReportV03/OrgnlPmtInfAndSts/TxInfAndSts/StsRsnInf/Rsn/Cd/text()").resultType(String.class))
					.setHeader("custom-transferAdditionalInfo", xpath("/CustomerPaymentStatusReportV03/OrgnlPmtInfAndSts/TxInfAndSts/StsRsnInf/AddtlInf/text()").resultType(String.class))
					.process(new TransferResponseProcessor()).id("UnmarshallAlphaBalanceTransferResponseProcessor")
					.removeHeaders("custom-*")					
			.end();
				
		
		
		from("direct:badRequest").setHeader(Exchange.HTTP_RESPONSE_CODE, constant("400"));
		from("direct:unauthorized").setBody(constant("Unauthorized Access:OBP-20001: User not logged in. Authentication is required.")).setHeader(Exchange.HTTP_RESPONSE_CODE, constant("401"));
		from("direct:serverError").setBody(constant("Server Error")).setHeader(Exchange.HTTP_RESPONSE_CODE, constant("500"));

	}

	
	private void configureHttpClient(CamelContext context) throws Exception {
    	
    	KeyStoreParameters ksp = new KeyStoreParameters();
    	ksp.setResource("certificates/" + map.get("trustore"));
    	ksp.setPassword("unisystems");
    	 
    	KeyManagersParameters kmp = new KeyManagersParameters();
    	kmp.setKeyStore(ksp);
    	kmp.setKeyPassword("unisystems");
    	
    	
    	TrustManagersParameters tmp = new TrustManagersParameters();
        tmp.setKeyStore(ksp);
    	 
    	SSLContextParameters scp = new SSLContextParameters();
    	scp.setKeyManagers(kmp);
    	scp.setTrustManagers(tmp);
    	 
    	HttpComponent httpComponent = context.getComponent("https4", HttpComponent.class);
    	httpComponent.setSslContextParameters(scp);
    }

}
