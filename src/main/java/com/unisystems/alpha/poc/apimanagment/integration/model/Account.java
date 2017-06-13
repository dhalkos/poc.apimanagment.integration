package com.unisystems.alpha.poc.apimanagment.integration.model;

public class Account {

	private String number;
	private String IBAN;
	private String label;
	private String swift_bic;
	private String bank_id;
	private String id;
	private Balance balance;
	
	
	public Account(String number, String iBAN, String label, String swift_bic, String bank_id, String id, Balance balance) {
		
		super();
		this.number = number;
		this.IBAN = iBAN;
		this.label = label;
		this.swift_bic = swift_bic;
		this.bank_id = bank_id;
		this.id = id;
		this.balance = balance;
	}

	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	public String getIBAN() {
		return IBAN;
	}
	public void setIBAN(String iBAN) {
		IBAN = iBAN;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getSwift_bic() {
		return swift_bic;
	}
	public void setSwift_bic(String swift_bic) {
		this.swift_bic = swift_bic;
	}
	public String getBank_id() {
		return bank_id;
	}
	public void setBank_id(String bank_id) {
		this.bank_id = bank_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Balance getBalance() {
		return balance;
	}
	public void setBalance(Balance balance) {
		this.balance = balance;
	}
	
	
	
}
