package com.unisystems.alpha.poc.apimanagment.integration.model;

public class AmountOfMoney {
	
	private String amount;
	private String currency;
	
	
	public AmountOfMoney(String amount, String currency) {
		super();
		this.amount = amount;
		this.currency = currency;
	}
	
	public AmountOfMoney() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
}
