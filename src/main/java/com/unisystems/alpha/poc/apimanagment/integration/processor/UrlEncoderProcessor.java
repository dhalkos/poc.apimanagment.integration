package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class UrlEncoderProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
        String body = in.getBody(String.class);
        
        try {
			exchange.getIn().setBody(URLEncoder.encode(body, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

}
