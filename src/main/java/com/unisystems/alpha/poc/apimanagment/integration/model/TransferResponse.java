package com.unisystems.alpha.poc.apimanagment.integration.model;

public class TransferResponse {
	
	private String date;
	private String id;
	private String status;
	private String reason_code;
	private String additional_info;
	
	
	
	public TransferResponse(String date, String id, String status, String reason_code, String additional_info) {
		super();
		this.date = date;
		this.id = id;
		this.status = status;
		this.reason_code = reason_code;
		this.additional_info = additional_info;
	}
	
	
	public TransferResponse() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason_code() {
		return reason_code;
	}
	public void setReason_code(String reason_code) {
		this.reason_code = reason_code;
	}
	public String getAdditional_info() {
		return additional_info;
	}
	public void setAdditional_info(String additional_info) {
		this.additional_info = additional_info;
	}
	
	
}
