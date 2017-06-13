package com.unisystems.alpha.poc.apimanagment.integration.model;

import java.math.BigDecimal;

public class Balance {
	
	private String currency;
	private String amount;
	
	
	
	public Balance(String currency, String amount) {
		super();
		this.currency = currency;
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
	
}
