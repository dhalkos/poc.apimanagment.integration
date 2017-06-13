package com.unisystems.alpha.poc.apimanagment.integration.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class XmlEncryptionProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		
	    org.apache.xml.security.Init.init();
	    
		
		String xml = exchange.getIn().getBody(String.class);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		

		builder = factory.newDocumentBuilder();
		
		Document xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes(Charset.defaultCharset())));
		InputStream certStream = this.getClass().getClassLoader().getResourceAsStream("certificates/alpha.cer");

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
	    X509Certificate encryptionCert = (X509Certificate)certificateFactory.generateCertificate(certStream);
	    
		PublicKey keyEncryptKey = encryptionCert.getPublicKey();

	    // Generate a secret key
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(128);
	    SecretKey secretKey = keyGenerator.generateKey();
		
		/*char[] password = "unisystemspass".toCharArray();
		byte[] salt = "usisysemssalt".getBytes();
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
		SecretKey tmp = keyfactory.generateSecret(spec);
		SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");*/
		
	    System.out.println("----->" + secretKey.getEncoded().length);
	    XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
	    keyCipher.init(XMLCipher.WRAP_MODE, keyEncryptKey);
	    EncryptedKey encryptedKey = keyCipher.encryptKey(xmlDocument, secretKey);
	    Element elementToEncrypt = xmlDocument.getDocumentElement();

	   
    	
	    XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_256);
	    xmlCipher.init(XMLCipher.ENCRYPT_MODE, secretKey);  

	    KeyInfo kiEnc = new KeyInfo(xmlDocument);
    	X509Data xData = new X509Data(xmlDocument);
    	xData.addCertificate(encryptionCert);    	
    	kiEnc.add(xData);
    	encryptedKey.setKeyInfo(kiEnc);
    	
	    // Add KeyInfo to the EncryptedData element
	    EncryptedData encryptedDataElement = xmlCipher.getEncryptedData();
	    KeyInfo keyInfo = new KeyInfo(xmlDocument);
	    keyInfo.add(encryptedKey);
	    encryptedDataElement.setKeyInfo(keyInfo);
	    
	    
	    
	    // Encrypt the assertion
	    Document encryptedDoc = xmlCipher.doFinal(xmlDocument, elementToEncrypt, false);
	    exchange.getIn().setBody(encryptedDoc);
	}

}
