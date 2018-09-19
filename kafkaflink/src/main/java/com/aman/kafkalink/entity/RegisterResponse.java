package com.aman.kafkalink.entity;

public class RegisterResponse {

	private String data;

	private String error;

	private Boolean success;

	private String cause;

	private MessageType messageType;

	private String senderId;

	public RegisterResponse() {
	}

	public RegisterResponse(String data, String error, Boolean success, String cause, MessageType messageType,
			String senderId) {
		this.data = data;
		this.error = error;
		this.success = success;
		this.cause = cause;
		this.messageType = messageType;
		this.senderId = senderId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}
