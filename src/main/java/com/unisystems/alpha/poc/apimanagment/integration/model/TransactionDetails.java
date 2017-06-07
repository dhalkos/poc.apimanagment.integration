package com.unisystems.alpha.poc.apimanagment.integration.model;

public class TransactionDetails {
	
	private AmountOfMoney new_balance;
	private String description;
	private String completed;
	private String type;
	private AmountOfMoney value;
	private String posted;
	
	
	
	public TransactionDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TransactionDetails(AmountOfMoney new_balance, String description, String completed, String type,
			AmountOfMoney value, String posted) {
		super();
		this.new_balance = new_balance;
		this.description = description;
		this.completed = completed;
		this.type = type;
		this.value = value;
		this.posted = posted;
	}

	public AmountOfMoney getNew_balance() {
		return new_balance;
	}
	
	public void setNew_balance(AmountOfMoney new_balance) {
		this.new_balance = new_balance;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public AmountOfMoney getValue() {
		return value;
	}
	public void setValue(AmountOfMoney value) {
		this.value = value;
	}
	public String getPosted() {
		return posted;
	}
	public void setPosted(String posted) {
		this.posted = posted;
	}
	
	

}
