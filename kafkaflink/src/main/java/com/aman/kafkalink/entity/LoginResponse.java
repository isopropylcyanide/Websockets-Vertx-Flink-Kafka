package com.aman.kafkalink.entity;

public class LoginResponse {

	private String response;

	private String senderId;

	private Boolean success;

	private MessageType messageType;

	public LoginResponse(String response, String senderId, Boolean success, MessageType messageType) {
		this.response = response;
		this.senderId = senderId;
		this.success = success;
		this.messageType = messageType;
	}

	public LoginResponse() {
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
}
