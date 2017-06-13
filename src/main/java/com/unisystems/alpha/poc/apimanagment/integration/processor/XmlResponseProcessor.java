package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringEscapeUtils;

public class XmlResponseProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		Message in = exchange.getIn();
        String body = in.getBody(String.class);
        	
        body = StringEscapeUtils.unescapeXml(body);
        
        /*System.out.println("==============================================================================");
        System.out.println(body);
        System.out.println("==============================================================================");*/
        
        Pattern pattern = Pattern.compile("Serialization/\">(.+?)</string>");
		Matcher matcher = pattern.matcher(body);
		matcher.find();
		exchange.getIn().setBody(matcher.group(1));

	}

}
