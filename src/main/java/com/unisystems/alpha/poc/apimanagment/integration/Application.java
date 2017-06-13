/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.unisystems.alpha.poc.apimanagment.integration;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
public class Application  {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
        CamelContext context = new DefaultCamelContext();
        System.out.println("----------------------------------------TEST---------------------------------");
        try {
			configureHttpClient(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/camel/*");
        registration.setName("CamelServlet");
        return registration;
    }
    
    
    private static void configureHttpClient(CamelContext context) throws Exception {
    	
    	KeyStoreParameters ksp = new KeyStoreParameters();
    	ksp.setResource("certificates/dhalkTrustore.jks");
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

    /*
    @Override
    public void configure() throws Exception {
    	
    	restConfiguration().component("servlet").contextPath("/alpha-poc-rest/").port(8080).bindingMode(RestBindingMode.json);
    	
    	 rest("/api/")         
         	.get("balance")
         		.produces(MediaType.APPLICATION_JSON)
         		 .route()
         		.to("direct:getAccountBalance")
         		.endRest()
         	.get("hello")
         		.produces("text/plain")
                .route()
                .transform().constant("Hello World!")
                .endRest()
         	.get("/transactions")
         		.produces(MediaType.APPLICATION_JSON)         		
         		.to("direct:getAccountStatment")
         	.post("/transfer")
         		.produces(MediaType.APPLICATION_JSON)
         		.type(Transfer.class)
         		.to("direct:transferAmmount")
         		;
    	
       
        
        from("direct:getAccountBalance")
        	.log(">>> getAccountBalance: ${body} ")
        	.process(new UnmarshallAlphaResponseProcessor()).id("UnmarshallAlphaResponseProcessor");
        
        from("direct:getAccountStatment")
    		.log(">>> getAccountStatment: ${body} ");
        
        from("direct:transferAmmount")
			.log(">>> transferAmmount: ${body} ");
    }*/
}
