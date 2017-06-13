package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.unisystems.alpha.poc.apimanagment.integration.model.Account;
import com.unisystems.alpha.poc.apimanagment.integration.model.AmountOfMoney;
import com.unisystems.alpha.poc.apimanagment.integration.model.Transaction;
import com.unisystems.alpha.poc.apimanagment.integration.model.TransactionDetails;

public class TransactionsResponseProcessor implements Processor {

	final Logger logger = LoggerFactory.getLogger("UnmarshallAlphaBalanceTransactionsResponseProcessor");

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		// logger.info("header info: " +
		// exchange.getIn().getHeader("obp_from_date").toString() + "," +
		// exchange.getIn().getHeader("obp_to_date").toString());

		List<Transaction> transactions = new ArrayList<Transaction>();
		String xml = exchange.getIn().getBody(String.class);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;

		builder = factory.newDocumentBuilder();
		
		//System.out.println(new String(xml.getBytes(Charset.defaultCharset())));
        //doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		/*InputStream ism = new ByteArrayInputStream(xml.getBytes(Charset.defaultCharset()));
		InputSource isc = new InputSource(ism);
		isc.setEncoding("UTF-8");*/
        //doc = builder.parse(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
        //doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_16)));
		doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		//doc = builder.parse(isc);
        
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        //xpath.setNamespaceContext(new NamespaceResolver(doc));
        
        
        NodeList transactionNodes = (NodeList) xpath.evaluate("/Document/BkToCstmrStmt/Stmt", doc, XPathConstants.NODESET);
        
        
        XPathExpression id = xpath.compile("Id/text()");
        
        XPathExpression this_iban = xpath.compile("Acct/Id/IBAN/text()");
        XPathExpression this_label = xpath.compile("Acct/Ownr/Nm/text()");
        XPathExpression this_swift_bic = xpath.compile("Acct/Svcr/BrnchId/Id/text()");
        
        XPathExpression other_iban = xpath.compile("RltdAcct/Id/IBAN/text()");
        XPathExpression other_label = xpath.compile("RltdAcct/Nm/text()");
        
        XPathExpression creationDateTime = xpath.compile("CreDtTm/text()");
        XPathExpression ammount = xpath.compile("TxsSummry/TtlNetNtry/Amt/text()");
        XPathExpression type = xpath.compile("TxsSummry/TtlNetNtry/CdtDbtInd/text()");
        
        
        for (int i = 0; i < transactionNodes.getLength(); i++) {
        	
        	Node transaction = transactionNodes.item(i);
        	
        	//System.out.println(Charset.forName("UTF-8").encode(this_label.evaluate(transaction, XPathConstants.STRING).toString().getBytes("UTF-8")));
        	transactions.add(new Transaction(id.evaluate(transaction, XPathConstants.STRING).toString(),
    						 				new Account(null, this_iban.evaluate(transaction, XPathConstants.STRING).toString(), this_label.evaluate(transaction, XPathConstants.STRING).toString(), this_swift_bic.evaluate(transaction, XPathConstants.STRING).toString(), null, null ,null),
    						 				new Account(null, other_iban.evaluate(transaction, XPathConstants.STRING).toString(), other_label.evaluate(transaction, XPathConstants.STRING).toString(),null, null, null ,null),
    						 				new TransactionDetails(null, null, creationDateTime.evaluate(transaction, XPathConstants.STRING).toString(), type.evaluate(transaction, XPathConstants.STRING).toString(), new AmountOfMoney(ammount.evaluate(transaction, XPathConstants.STRING).toString(), "EUR"), null)));
       }
        
		
        exchange.getIn().setBody(transactions);

	}

}
