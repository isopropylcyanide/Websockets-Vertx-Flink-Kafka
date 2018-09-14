package org.aman.wsvertx.model.payload;

public class SampleRequest {

	private String data;

	private String senderId;

	public SampleRequest(String data, String senderId) {
		this.data = data;
		this.senderId = senderId;
	}

	public SampleRequest() {
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
}
