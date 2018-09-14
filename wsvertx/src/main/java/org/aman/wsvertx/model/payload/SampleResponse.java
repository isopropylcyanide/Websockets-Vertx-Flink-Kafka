package org.aman.wsvertx.model.payload;

public class SampleResponse {

	private String response;

	private String senderId;

	private Boolean success;

	public SampleResponse(String response, String senderId, Boolean success) {
		this.response = response;
		this.senderId = senderId;
		this.success = success;
	}

	public SampleResponse() {
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
}
