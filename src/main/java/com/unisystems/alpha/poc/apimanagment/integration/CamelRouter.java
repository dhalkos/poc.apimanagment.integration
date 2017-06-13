package com.unisystems.alpha.poc.apimanagment.integration;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.stereotype.Component;

import com.unisystems.alpha.poc.apimanagment.integration.beans.RequestCounter;
import com.unisystems.alpha.poc.apimanagment.integration.processor.BalanceResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.TransactionsResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.TransferResponseProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.UrlEncoderProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.XmlEncryptionProcessor;
import com.unisystems.alpha.poc.apimanagment.integration.processor.XmlResponseProcessor;

@Component	
public class CamelRouter extends RouteBuilder {

	private HashMap<String, String> map = new HashMap<String, String>();
	
	
	@Override
	public void configure() throws Exception {
		
		//String server = "localhost:8443";
		//String server = "webtest.alpha.gr:8443";
		//String server = "webtest.alpha.gr";
		
		//dhalk params
		//map.put("server","localhost:8443");
		//map.put("trustore", "dhalkosTrustore1.jks");
				
		//alpha params
		map.put("server","webtest.alpha.gr");
		map.put("trustore", "new_alphaTrustore.jks");
		
		//map.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IjNMTHl3cXpHQURzOVh1Zmw1MndrM2NsbjBVSSIsImtpZCI6IjNMTHl3cXpHQURzOVh1Zmw1MndrM2NsbjBVSSJ9.eyJpc3MiOiJodHRwczovL3dlYnRlc3QuYWxwaGEuZ3IvQWxwaGFSZXZlcnNlUHJveHkvb2F1dGgiLCJhdWQiOiJodHRwczovL3dlYnRlc3QuYWxwaGEuZ3IvQWxwaGFSZXZlcnNlUHJveHkvb2F1dGgvcmVzb3VyY2VzIiwiZXhwIjoxNDk3Mjk0MjMwLCJuYmYiOjE0OTcyOTA2MzAsImNsaWVudF9pZCI6ImF1dGhDb2RlY2xpZW50Iiwic2NvcGUiOiJzYW1wbGVBcGkiLCJzdWIiOiIyMzEyMiIsImF1dGhfdGltZSI6MTQ5NzI5MDYxNiwiaWRwIjoiaWRzcnYiLCJuYW1lIjoizqPOn86mzpnOkSDOoM6RzqfOnc6ZzqPOpM6XIiwiZ2l2ZW5fbmFtZSI6Is6jzp_Ops6ZzpEiLCJmYW1pbHlfbmFtZSI6Is6gzpHOp86dzpnOo86kzpciLCJhbHBoYV9zZXNzaW9uX2lkIjoiMEs0N1pHUEdLM1laIiwic3Vic2NyaXB0aW9uX2lkIjoiMjA4MjIiLCJpZGxlX3RpbWVvdXQiOiI5MDAiLCJzZXNzaW9uX3RpbWVvdXQiOiIzNjAwIiwiY2xpZW50X2lwIjoiMjEzLjE2LjE4MS42MSIsImp0aSI6IjhjNDBmNWQwNDc0MGE1MDUyZjE1N2NjMzMyZDhiNmJmIiwiYW1yIjpbInBhc3N3b3JkIl19.g35MPW_yzyk1TfvqDzvW1MPPavOGOvcwRVuMI8jQsN-thgGK_7FkIuwPmZEyo06uyJDbkCvQLGYH_dPfr5pdsAxJq1X7pVjPbRjlEWxqRSRN6tGClbiBoGIb2IS3ozVmwprBgeIZaYZE7P9Rxi0akYV_BQkwGuB0sN9sNC-usycVuIBwkw312yVUMtCFPc8un_aHVc9E8hCZSfCfm23oWf4Nm7mJrjcRQvRrswVMKx1hXyY1mDnx3P_lV9l-yIRshnfYnY5ELpQSkj3qMLMKGsUhRqK8nPYAfcmbwMyknYw8I4m22MDK_EV3Ry04v1WmeQVTxWJZPsS_VXmoqzjA0A");
		
		configureHttpClient(getContext());
		
		/*Namespaces blnNS = new Namespaces("c", "urn:iso:std:iso:20022:tech:xsd:camt.052.001.06");		
		Namespaces trnsfHeaderNS = new Namespaces("c", "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03");
		Namespaces trnsfPaymentNS = new Namespaces("c", "urn:iso:std:iso:20022:tech:xsd:pain.002.001.03");*/
		
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
		
		
		from("direct:getAccountBalance").routeId("account-balance")
			.log(">>> balance API route started")
			.setProperty("reqId").method(RequestCounter.class, "getBalanceRequestId")
			.setProperty("accountId", simple("${header.accountId}"))
			.setHeader("currentDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"))
			.to("velocity:templates/BalanceTemplate.vm?contentCache=true")
			//.log(">>> xml request xml message is:\n ${body} ")
			.process(new XmlEncryptionProcessor()).id("balance-XmlEncryptionProcessor")
			.setBody(body().regexReplaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", ""))
			//.log(">>> xml request xml message is:\n ${body} ")			
			.setHeader("custom-Content-Type", simple("${header.Content-Type}"))
			.removeHeaders("CamelHttp*")
			.to("log:DEBUG?showHeaders=true")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST.name()))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setHeader(Exchange.CONTENT_ENCODING, constant("UTF-8"))
			//.setHeader("Authorization", constant("Bearer " + map.get("token")))
			.setHeader(Exchange.HTTP_URI, simple("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/balance"))
			/*.process(new Processor() {
			    public void process(Exchange exchange) {
			        Message in = exchange.getIn();
			        String body = in.getBody(String.class);
			        //body = body.replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "");
			        try {
						exchange.getIn().setBody(URLEncoder.encode(body, "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			})		*/	
			.process(new UrlEncoderProcessor()).id("balance-UrlEncoderProcessor")
			.setBody(simple("Initiation=${body}&DateToPost=${date:now:yyyyMMdd}&DeveloperId=Unisystems-dhalk&Token=123456&UniqueIdentifier=${exchangeProperty.reqId}"))
			.log(">>> xml request encrypteed without new lines message is:\n${body}")			
			.to("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/balance?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&disableStreamCache=false")
			.convertBodyTo(String.class)			
			.process(new XmlResponseProcessor()).id("balance-XmlResponseProcessor")
			/*.process(new Processor() {
			    public void process(Exchange exchange) {
			        Message in = exchange.getIn();
			        String body = in.getBody(String.class);
			        	
			        body = StringEscapeUtils.unescapeXml(body);
			        
					Pattern pattern = Pattern.compile("<BankToCustomerAccountReportV06(.+?)</string>");
					Matcher matcher = pattern.matcher(body);
					matcher.find();
					exchange.getIn().setBody("<BankToCustomerAccountReportV06" + matcher.group(1));
					
			    }
			})		*/	
			.log(">>> xml response message is:\n ${body} ")
			.setHeader("Content-Type", simple("${header.custom-Content-Type}"))
			//.to("velocity:templates/responses/BalanceResponse.vm?contentCache=true")
			.to("xslt:xslt/removeNamespaces.xsl?")		
			.setHeader("custom-balanceAmount", xpath("/BankToCustomerAccountReportV06/Rpt/Bal/Amt/text()").resultType(String.class))
			.setHeader("custom-balanceCurrency", constant("EUR")) //xpath("/BankToCustomerAccountReportV06/Rpt/Bal/Amt/@Ccy/text()").resultType(String.class))
			.setHeader("custom-bic", xpath("/BankToCustomerAccountReportV06/Rpt/Acct/Svcr/FinInstnId/BICFI/text()").resultType(String.class))
			.setHeader("custom-responseId", xpath("/BankToCustomerAccountReportV06/Rpt/Id/text()").resultType(String.class))
			//.log(">>> xml custom-balanceCurrency is:\n ${header.custom-balanceCurrency}")
    		.process(new BalanceResponseProcessor()).id("UnmarshallAlphaResponseProcessor")
    		.removeHeaders("custom-*");
    
		
		
		
		
		
		from("direct:getAccountTransactions").routeId("account-transactions")
			.log(">>> transactions API route started")
			.setProperty("reqId").method(RequestCounter.class, "getTransactionsRequestId")
			.setHeader("currentDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"))
			.setHeader("fromDate", simple("${header.obp_from_date.substring(0,10)}"))
			.setHeader("fromTime", simple("${header.obp_from_date.substring(11)}"))
			.setHeader("toDate", simple("${header.obp_to_date.substring(0,10)}"))
			.setHeader("toTime", simple("${header.obp_to_date.substring(11)}"))			
			.to("velocity:templates/TransactionsTemplate.vm?contentCache=true")
			.process(new XmlEncryptionProcessor()).id("transactions-XmlEncryptionProcessor")
			.setBody(body().regexReplaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", ""))
			.setHeader("custom-Content-Type", simple("${header.Content-Type}"))
			.removeHeaders("CamelHttp*")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST.name()))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setHeader(Exchange.CONTENT_ENCODING, constant("UTF-8"))
			//.setHeader("Authorization", constant("Bearer " + map.get("token")))
			.setHeader(Exchange.HTTP_URI, simple("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/statement"))			
			.process(new UrlEncoderProcessor()).id("transactions-UrlEncoderProcessor")
			.setBody(simple("Initiation=${body}&DateToPost=${date:now:yyyyMMdd}&DeveloperId=Unisystems-dhalk&Token=123456&UniqueIdentifier=${exchangeProperty.reqId}"))
			.to("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/statement?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&disableStreamCache=true")
			.convertBodyTo(String.class)
			.process(new XmlResponseProcessor()).id("transactions-XmlResponseProcessor")
			.setHeader("Content-Type", simple("${header.custom-Content-Type}"))					
			.to("xslt:xslt/removeNamespaces.xsl?")
			.log(">>> xml response message is:\n ${body} ")
			.process(new TransactionsResponseProcessor()).id("UnmarshallAlphaTransactionsResponseProcessor");
		
		
		
		from("direct:transferAmount").routeId("account-transfer")
			.log(">>> transfer API route started")
			.setProperty("reqId").method(RequestCounter.class, "getTransferRequestId")
			.setHeader("currentDateTime", simple("${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSXXX}"))
			.to("velocity:templates/TransferTemplate.vm?contentCache=true")			
			.process(new XmlEncryptionProcessor()).id("transfer-XmlEncryptionProcessor")
			.setBody(body().regexReplaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", ""))			
			.setHeader("custom-Content-Type", simple("${header.Content-Type}"))
			.removeHeaders("CamelHttp*")
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST.name()))
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setHeader(Exchange.CONTENT_ENCODING, constant("UTF-8"))			
			//.setHeader("Authorization", constant("Bearer " + map.get("token")))
			.setHeader(Exchange.HTTP_URI, simple("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/transfer"))
			.process(new UrlEncoderProcessor()).id("transfer-UrlEncoderProcessor")
			.setBody(simple("Initiation=${body}&DateToPost=${date:now:yyyyMMdd}&DeveloperId=Unisystems-dhalk&Token=123456&UniqueIdentifier=${exchangeProperty.reqId}"))
			.to("https4://" + map.get("server") + "/alphareverseproxy/psd2/api/psd2/transfer?bridgeEndpoint=true&amp;throwExceptionOnFailure=false&disableStreamCache=true")
			.convertBodyTo(String.class)
			.process(new XmlResponseProcessor()).id("transfer-XmlResponseProcessor")
			.setHeader("Content-Type", simple("${header.custom-Content-Type}"))
			.setHeader("custom-transferExecutionDate", xpath("/CustomerPaymentStatusReportV03/c:GrpHdr/c:CreDtTm/text()").resultType(String.class))			
			.setHeader("custom-transferId", xpath("/CustomerPaymentStatusReportV03/c:OrgnlPmtInfAndSts/c:OrgnlPmtInfId/text()").resultType(String.class))
			.setHeader("custom-transferStatus", xpath("/CustomerPaymentStatusReportV03/c:OrgnlPmtInfAndSts/c:TxInfAndSts/c:TxSts/text()").resultType(String.class))
			.setHeader("custom-transferStatusCode", xpath("/CustomerPaymentStatusReportV03/c:OrgnlPmtInfAndSts/c:TxInfAndSts/c:StsRsnInf/c:Rsn/c:Cd/text()").resultType(String.class))
			.setHeader("custom-transferAdditionalInfo", xpath("/CustomerPaymentStatusReportV03/c:OrgnlPmtInfAndSts/c:TxInfAndSts/c:StsRsnInf/c:AddtlInf/text()").resultType(String.class))
			.process(new TransferResponseProcessor()).id("UnmarshallAlphaBalanceTransferResponseProcessor")
			.removeHeaders("custom-*");

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
